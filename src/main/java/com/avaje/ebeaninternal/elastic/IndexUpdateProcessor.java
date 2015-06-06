package com.avaje.ebeaninternal.elastic;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.Map;

/**
 * Processes index updates.
 * <p>
 * This involves sending updates directly to ElasticSearch via it's Bulk API or
 * queuing events for future processing.
 * </p>
 */
public interface IndexUpdateProcessor {

  /**
   * Process all the index updates.
   * <p>
   * Typically this makes calls to the ElasticSearch Bulk API or simply adds IndexQueryEntry's
   * to a queue for future processing.
   * </p>
   *
   * @param indexUpdates  The 'Bulk' and 'Queue' updates to the indexes for the transaction.
   * @param bulkBatchSize The batch size to use for Bulk API calls specified on the transaction.
   *                      If this is 0 then the default batch size is used.
   */
  void process(IndexUpdates indexUpdates, int bulkBatchSize);

  /**
   * Create a CallbackBulkElasticUpdate for processing potentially a large number of
   * documents and sending them to the Bulk API.
   * <p>
   * This provides a mechanism such that when the bulkBatchSize is reached the buffer
   * is sent to ElasticSearch.
   * </p>
   */
  CallbackBulkElasticUpdate createCallbackBulkElasticUpdate(int bulkBatchSize) throws IOException;

  /**
   * Send the BulkElasticUpdate to ElasticSearch Bulk API.
   */
  Map<String, Object> sendBulk(BulkElasticUpdate bulk) throws IOException;

  /**
   * Create a BulkElasticUpdate buffer.
   */
  BulkElasticUpdate createBulkElasticUpdate() throws IOException;

  /**
   * Return the document source for a specific document.
   */
  JsonParser getDocSource(String indexType, String indexName, Object docId) throws IOException;


}
