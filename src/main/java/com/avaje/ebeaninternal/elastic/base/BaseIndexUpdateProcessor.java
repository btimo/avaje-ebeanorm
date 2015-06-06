package com.avaje.ebeaninternal.elastic.base;

import com.avaje.ebean.text.json.EJson;
import com.avaje.ebeaninternal.elastic.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of the IndexUpdateProcessor.
 */
public class BaseIndexUpdateProcessor implements IndexUpdateProcessor {

  public static final Logger elaLogger = LoggerFactory.getLogger("org.avaje.ebean.ELA");

  protected final Logger logger = LoggerFactory.getLogger(BaseIndexUpdateProcessor.class);

  protected final JsonFactory jsonFactory;

  protected final IndexQueue queue;

  protected final BulkMessageSender messageSender;

  protected final int bulkBatchSize;

  public BaseIndexUpdateProcessor(IndexQueue queue, JsonFactory jsonFactory, BulkMessageSender messageSender, int bulkBatchSize) {
    this.queue = queue;
    this.jsonFactory = jsonFactory;
    this.messageSender = messageSender;
    this.bulkBatchSize = bulkBatchSize;
  }

  @Override
  public void process(IndexUpdates indexUpdates, int txnbulkBatchSize) {

    int batchSize = (txnbulkBatchSize > 0) ? txnbulkBatchSize : bulkBatchSize;
    sendBulkUpdate(indexUpdates, true, batchSize);
    sendQueueEvents(indexUpdates);
  }

  /**
   * Add the queue entries to the queue for later processing.
   */
  protected void sendQueueEvents(IndexUpdates indexUpdates) {

    queue.queue(indexUpdates.getQueueEntries());
  }


  /**
   * Send the 'bulk entries' to the ElasticSearch Bulk API.
   *
   * @param indexUpdates        The index updates holding the bulk entries to send
   * @param addToQueueOnFailure if true then failures are added tho the queue
   * @param batchSize           The batch size to use for sending to the Bulk API.
   */
  protected void sendBulkUpdate(IndexUpdates indexUpdates, boolean addToQueueOnFailure, int batchSize) {

    if (!indexUpdates.hasBulkEvents()) {
      // nothing to send
      return;
    }

    List<BulkElasticRequest> bulkEntries = indexUpdates.getBulkEntries();
    if (bulkEntries.size() <= batchSize) {
      // send them all in one go
      sendBulkUpdateBatch(indexUpdates, bulkEntries, addToQueueOnFailure);

    } else {
      // break into batches using the batchSize
      List<List<BulkElasticRequest>> batches = createBatches(bulkEntries, batchSize);
      for (int i = 0; i < batches.size(); i++) {
        sendBulkUpdateBatch(indexUpdates, batches.get(i), addToQueueOnFailure);
      }
    }
  }

  /**
   * Send the bulk entries to ElasticSearch using the Bulk API.  If addToQueueOnFailure is set to true then
   * any entries that failed will be added to the queue.
   *
   * @param indexUpdates        The index updates holding the bulk entries to send
   * @param bulkEntries         The entries to send
   * @param addToQueueOnFailure if true then failures are added tho the queue
   */
  protected void sendBulkUpdateBatch(IndexUpdates indexUpdates, List<BulkElasticRequest> bulkEntries, boolean addToQueueOnFailure) {
    try {

      StringWriter writer = new StringWriter();
      BulkElasticUpdate bulk = createBulkElasticUpdate(writer);

      for (BulkElasticRequest bulkEntry : bulkEntries) {
        bulkEntry.elasticBulkUpdate(bulk);
      }

      bulk.flush();

      String payload = writer.toString();
      if (elaLogger.isTraceEnabled()) {
        elaLogger.trace("ElasticBulkMessage Request:\n" + payload + "\n");
      }

      // send to Bulk API
      String response = messageSender.post(payload);

      // parse the response
      Map<String, Object> responseMap = parseBulkResponse(response);
      Boolean errors = (Boolean) responseMap.get("errors");

      elaLogger.debug("request entries:{} errors:{} payloadSize:{} responseSize:{}", bulkEntries.size(), errors, payload.length(), response.length());

      if (elaLogger.isTraceEnabled()) {
        elaLogger.trace("ElasticBulkMessage Response:\n" + response);
      }

      if (addToQueueOnFailure && errors) {
        // for any errors add the matching bulk request to the queue
        logger.debug("processing errors on response ...");
        List<Map<String, Object>> responseItems = (List<Map<String, Object>>) responseMap.get("responseItems");
        for (int i = 0; i < responseItems.size(); i++) {
          if (addToQueueForStatus(responseItems.get(i))) {
            logger.debug("... responseEntry:{} adding to queue", i);
            bulkEntries.get(i).addToQueue(indexUpdates);
          }
        }
      }

    } catch (IOException e) {
      logger.error("Failed to successfully send bulk update to ElasticSearch", e);
      if (addToQueueOnFailure) {
        // add all remaining requests the the queue
        for (BulkElasticRequest bulkEntry : bulkEntries) {
          bulkEntry.addToQueue(indexUpdates);
        }
      }
    }

  }

  /**
   * Return true if this responseEntry should be added to the queue (typically based on it's returned status).
   */
  protected boolean addToQueueForStatus(Map<String, Object> responseEntry) {

    String status = (String) responseEntry.get("status");
    return "400".equals(status);
  }

  /**
   * Break up the requests into batches using the batchSize.
   */
  protected List<List<BulkElasticRequest>> createBatches(List<BulkElasticRequest> allRequests, int batchSize) {

    List<List<BulkElasticRequest>> parts = new ArrayList<List<BulkElasticRequest>>();
    int totalSize = allRequests.size();

    for (int i = 0; i < totalSize; i += batchSize) {
      parts.add(allRequests.subList(i, Math.min(totalSize, i + batchSize)));
    }
    return parts;
  }

  /**
   * Parse the returned JSON response into a Map.
   */
  protected Map<String, Object> parseBulkResponse(String response) throws IOException {

    return EJson.parseObject(response);
  }

  /**
   * Create a BulkElasticUpdate.
   */
  protected BulkElasticUpdate createBulkElasticUpdate(Writer writer) throws IOException {

    JsonGenerator gen = jsonFactory.createGenerator(writer);
    return new BulkElasticUpdate(gen);
  }
}
