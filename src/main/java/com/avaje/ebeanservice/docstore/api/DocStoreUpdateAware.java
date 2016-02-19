package com.avaje.ebeanservice.docstore.api;

import java.io.IOException;

/**
 * For persist events that know how to publish or queue their change to the Document store.
 */
public interface DocStoreUpdateAware {

  /**
   * Add the event to the doc store bulk update.
   */
  void docStoreUpdate(DocStoreUpdateContext txn) throws IOException;

  /**
   * Add to the queue.
   */
  void addToQueue(DocStoreUpdates docStoreUpdates);
}
