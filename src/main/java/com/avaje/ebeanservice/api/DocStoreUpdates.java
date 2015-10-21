package com.avaje.ebeanservice.api;

import com.avaje.ebeaninternal.server.core.PersistRequestBean;
import com.avaje.ebeanservice.api.DocStoreQueueEntry.Action;

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
  private final List<PersistRequestBean<?>> persistEvents = new ArrayList<PersistRequestBean<?>>();

  /**
   * Delete by Id.
   */
  private final List<DocStoreDeleteEvent> deleteEvents = new ArrayList<DocStoreDeleteEvent>();

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
  public void addPersist(PersistRequestBean<?> bulkRequest) {
    persistEvents.add(bulkRequest);
  }

  /**
   * Add a request for processing via ElasticSearch Bulk API.
   */
  public void addDelete(DocStoreDeleteEvent bulkRequest) {
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
   * Return the persist insert and update requests to be sent to the document store.
   */
  public List<PersistRequestBean<?>> getPersistEvents() {
    return persistEvents;
  }

  /**
   * Return delete events.
   */
  public List<DocStoreDeleteEvent> getDeleteEvents() {
    return deleteEvents;
  }

  /**
   * Return the entries for sending to the queue.
   */
  public List<DocStoreQueueEntry> getQueueEntries() {
    return queueEntries;
  }

}
