package com.avaje.ebeanservice.api;

import com.avaje.ebean.bean.EntityBean;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;

import java.io.IOException;

/**
 * A 'Delete by Id' request that is send to the document store.
 */
public class DocStoreIndexEvent implements DocStoreUpdateAware {

  final BeanDescriptor<?> beanDescriptor;

  final Object idValue;

  final EntityBean bean;

  public DocStoreIndexEvent(BeanDescriptor<?> beanDescriptor, Object idValue, EntityBean bean) {
    this.beanDescriptor = beanDescriptor;
    this.idValue = idValue;
    this.bean = bean;
  }

  /**
   * Add appropriate JSON content for sending to the ElasticSearch Bulk API.
   */
  @Override
  public void docStoreBulkUpdate(DocStoreBulkUpdate txn) throws IOException {

    beanDescriptor.docStoreInsert(idValue, bean, txn);
  }

  /**
   * Add this event to the queue (for queue delayed processing).
   */
  @Override
  public void addToQueue(DocStoreUpdates docStoreUpdates) {
    docStoreUpdates.queueDelete(beanDescriptor.getDocStoreQueueId(), idValue);
  }
}
