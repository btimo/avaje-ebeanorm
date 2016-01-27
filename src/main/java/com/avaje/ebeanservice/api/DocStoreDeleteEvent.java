package com.avaje.ebeanservice.api;

import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;

import java.io.IOException;

/**
 * A 'Delete by Id' request that is send to the document store.
 */
public class DocStoreDeleteEvent implements ElasticUpdateAware {

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
  public void elasticBulkUpdate(BulkElasticUpdate txn) throws IOException {

    beanDescriptor.elasticDeleteById(idValue, txn);
  }

  /**
   * Add this event to the queue (for queue delayed processing).
   */
  @Override
  public void addToQueue(DocStoreUpdates docStoreUpdates) {
    docStoreUpdates.queueDelete(beanDescriptor.getElasticQueueId(), idValue);
  }
}
