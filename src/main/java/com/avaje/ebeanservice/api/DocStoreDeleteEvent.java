package com.avaje.ebeanservice.api;

import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;

import java.io.IOException;

/**
 * A 'Delete by Id' request that is send to the document store.
 */
public class DocStoreDeleteEvent implements DocStoreUpdateAware {

  final BeanDescriptor<?> beanDescriptor;

  final Object idValue;

  public DocStoreDeleteEvent(BeanDescriptor<?> beanDescriptor, Object idValue) {
    this.beanDescriptor = beanDescriptor;
    this.idValue = idValue;
  }

  /**
   * Add appropriate JSON content for sending to the ElasticSearch Bulk API.
   */
  @Override
  public void docStoreBulkUpdate(DocStoreBulkUpdate txn) throws IOException {

    beanDescriptor.docStoreDeleteById(idValue, txn);
  }

  /**
   * Add this event to the queue (for queue delayed processing).
   */
  @Override
  public void addToQueue(DocStoreUpdates docStoreUpdates) {
    docStoreUpdates.queueDelete(beanDescriptor.getDocStoreQueueId(), idValue);
  }
}
