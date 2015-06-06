package com.avaje.ebeaninternal.elastic;

import com.avaje.ebeaninternal.elastic.IndexQueueEntry.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of ElasticSearch index updates that are either queued for future processing
 * or sent to the ElasticSearch Bulk API.
 */
public class IndexUpdates {

  /**
   * Entries sent to the ElasticSearch Bulk API.
   */
  List<BulkElasticRequest> bulkEntries = new ArrayList<BulkElasticRequest>();

  /**
   * Entries sent to the queue for later processing.
   */
  List<IndexQueueEntry> queueEntries = new ArrayList<IndexQueueEntry>();

  /**
   * Return true if there are no index events to process.
   */
  public boolean isEmpty() {
    return bulkEntries.isEmpty() && queueEntries.isEmpty();
  }

  /**
   * Return true if there are events to send to the ElasticSearch Bulk API.
   */
  public boolean hasBulkEvents() {
    return !bulkEntries.isEmpty();
  }

  /**
   * Add a request for processing via ElasticSearch Bulk API.
   */
  public void add(BulkElasticRequest bulkRequest) {
    bulkEntries.add(bulkRequest);
  }

  /**
   * Add a 'queue index update' request.
   * This is for submitting to a queue for later processing.
   */
  public void queueIndex(String queueId, Object beanId) {
    queueEntries.add(new IndexQueueEntry(Action.INDEX, queueId, beanId));
  }

  /**
   * Add a 'queue index delete' request.
   * This is for submitting to a queue for later processing.
   */
  public void queueDelete(String queueId, Object beanId) {
    queueEntries.add(new IndexQueueEntry(Action.DELETE, queueId, beanId));
  }

  /**
   * Return the entries for sending to ElasticSearch Bulk API.
   */
  public List<BulkElasticRequest> getBulkEntries() {
    return bulkEntries;
  }

  /**
   * Return the entries for sending to the queue.
   */
  public List<IndexQueueEntry> getQueueEntries() {
    return queueEntries;
  }

}
