package com.avaje.ebeanservice.api;

import com.avaje.ebean.DocStoreQueueEntry;
import com.avaje.ebean.DocStoreQueueEntry.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of document store updates that are either queued for future processing
 * or sent to the document store.
 */
public class DocStoreUpdates {

  /**
   * Persist inserts and updates.
   */
  private final List<DocStoreUpdateAware> persistEvents = new ArrayList<DocStoreUpdateAware>();

  /**
   * Delete by Id.
   */
  private final List<DocStoreUpdateAware> deleteEvents = new ArrayList<DocStoreUpdateAware>();

  /**
   * Entries sent to the queue for later processing.
   */
  private final List<DocStoreQueueEntry> queueEntries = new ArrayList<DocStoreQueueEntry>();


  public DocStoreUpdates() {
  }

  /**
   * Return true if there are no events to process.
   */
  public boolean isEmpty() {
    return persistEvents.isEmpty() && deleteEvents.isEmpty() && queueEntries.isEmpty();
  }

  /**
   * Add a request for processing via ElasticSearch Bulk API.
   */
  public void addPersist(DocStoreUpdateAware bulkRequest) {
    persistEvents.add(bulkRequest);
  }

  /**
   * Add a request for processing via ElasticSearch Bulk API.
   */
  public void addDelete(DocStoreUpdateAware bulkRequest) {
    deleteEvents.add(bulkRequest);
  }

  /**
   * Add a 'queue index update' request.
   * This is for submitting to a queue for later processing.
   */
  public void queueIndex(String queueId, Object beanId) {
    queueEntries.add(new DocStoreQueueEntry(Action.INDEX, queueId, beanId));
  }

  /**
   * Add a 'queue index delete' request.
   * This is for submitting to a queue for later processing.
   */
  public void queueDelete(String queueId, Object beanId) {
    queueEntries.add(new DocStoreQueueEntry(Action.DELETE, queueId, beanId));
  }

  /**
   * Add a queue entry for an invalidation due to an update to a nested/embedded object.
   */
  public void queueNested(String queueId, String path, Object beanId) {
    queueEntries.add(new DocStoreQueueEntry(Action.NESTED, queueId, path, beanId));
  }

  /**
   * Return the persist insert and update requests to be sent to the document store.
   */
  public List<DocStoreUpdateAware> getPersistEvents() {
    return persistEvents;
  }

  /**
   * Return delete events.
   */
  public List<DocStoreUpdateAware> getDeleteEvents() {
    return deleteEvents;
  }

  /**
   * Return the entries for sending to the queue.
   */
  public List<DocStoreQueueEntry> getQueueEntries() {
    return queueEntries;
  }

}
