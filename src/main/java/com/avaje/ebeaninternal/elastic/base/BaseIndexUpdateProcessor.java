package com.avaje.ebeaninternal.elastic.base;

import com.avaje.ebeaninternal.elastic.BulkElasticUpdate;
import com.avaje.ebeaninternal.elastic.IndexQueue;
import com.avaje.ebeaninternal.elastic.IndexUpdateProcessor;
import com.avaje.ebeaninternal.elastic.IndexUpdates;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Base implementation of the IndexUpdateProcessor.
 */
public class BaseIndexUpdateProcessor implements IndexUpdateProcessor {

  protected final Logger logger = LoggerFactory.getLogger(BaseIndexUpdateProcessor.class);

  protected final JsonFactory jsonFactory;

  protected final IndexQueue queue;

  public BaseIndexUpdateProcessor(IndexQueue queue, JsonFactory jsonFactory) {
    this.queue = queue;
    this.jsonFactory = jsonFactory;
  }

  @Override
  public void process(IndexUpdates indexUpdates) {

    sendBulkUpdate(indexUpdates, true);
    sendQueueEvents(indexUpdates);
  }

  protected void sendQueueEvents(IndexUpdates indexUpdates) {

    queue.queue(indexUpdates.getQueueEntries());
  }

  protected void sendBulkUpdate(IndexUpdates indexUpdates, boolean addToQueueOnFailure) {

    if (!indexUpdates.hasBulkEvents()) {
      return;
    }

    List<BulkElasticRequest> bulkEntries = indexUpdates.getBulkEntries();
    try {

      StringWriter writer = new StringWriter();
      BulkElasticUpdate bulk = createBulkElasticUpdate(writer);

      for (BulkElasticRequest bulkEntry : bulkEntries) {
        bulkEntry.elasticBulkUpdate(bulk);
      }

      bulk.flush();
      System.out.println("----");
      System.out.println(writer.toString());
      System.out.println("----");

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

  protected BulkElasticUpdate createBulkElasticUpdate(Writer writer) throws IOException {

    JsonGenerator gen = jsonFactory.createGenerator(writer);
    return new BulkElasticUpdate(gen);
  }
}
