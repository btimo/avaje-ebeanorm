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
   */
  void process(IndexUpdates indexUpdates);
}
