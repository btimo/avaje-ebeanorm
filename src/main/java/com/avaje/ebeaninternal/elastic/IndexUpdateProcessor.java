package com.avaje.ebeaninternal.elastic;

/**
 * Processes index updates.
 * <p>
 *   This involves sending updates directly to ElasticSearch via it's Bulk API or
 *   queuing events for future processing.
 * </p>
 */
public interface IndexUpdateProcessor {

  /**
   * Process all the index updates.
   * <p>
   *   Typically this makes calls to the ElasticSearch Bulk API or simply adds IndexQueryEntry's
   *   to a queue for future processing.
   * </p>
   *
   * @param indexUpdates The 'Bulk' and 'Queue' updates to the indexes for the transaction.
   *
   * @param bulkBatchSize The batch size to use for Bulk API calls specified on the transaction.
   *                      If this is 0 then the default batch size is used.
   */
  void process(IndexUpdates indexUpdates, int bulkBatchSize);
}
