package com.avaje.ebeanservice.docstore.api.support;

import com.avaje.ebean.plugin.BeanType;
import com.avaje.ebeanservice.docstore.api.DocStoreUpdateContext;
import com.avaje.ebeanservice.docstore.api.DocStoreUpdate;
import com.avaje.ebeanservice.docstore.api.DocStoreUpdates;

import java.io.IOException;

/**
 * A 'Delete by Id' request that is send to the document store.
 */
public class DocStoreIndexEvent<T> implements DocStoreUpdate {

  final BeanType<T> beanType;

  final Object idValue;

  final T bean;

  public DocStoreIndexEvent(BeanType<T> beanType, Object idValue, T bean) {
    this.beanType = beanType;
    this.idValue = idValue;
    this.bean = bean;
  }

  /**
   * Add appropriate JSON content for sending to the ElasticSearch Bulk API.
   */
  @Override
  public void docStoreUpdate(DocStoreUpdateContext txn) throws IOException {
    beanType.docStore().index(idValue, bean, txn);
  }

  /**
   * Add this event to the queue (for queue delayed processing).
   */
  @Override
  public void addToQueue(DocStoreUpdates docStoreUpdates) {
    docStoreUpdates.queueIndex(beanType.getDocStoreQueueId(), idValue);
  }
}
