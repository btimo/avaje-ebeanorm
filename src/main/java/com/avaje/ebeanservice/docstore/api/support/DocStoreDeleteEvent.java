package com.avaje.ebeanservice.docstore.api.support;

import com.avaje.ebean.plugin.SpiBeanType;
import com.avaje.ebeanservice.docstore.api.DocStoreUpdateContext;
import com.avaje.ebeanservice.docstore.api.DocStoreUpdateAware;
import com.avaje.ebeanservice.docstore.api.DocStoreUpdates;

import java.io.IOException;

/**
 * A 'Delete by Id' request that is send to the document store.
 */
public class DocStoreDeleteEvent implements DocStoreUpdateAware {

  final SpiBeanType<?> beanType;

  final Object idValue;

  public DocStoreDeleteEvent(SpiBeanType<?> beanType, Object idValue) {
    this.beanType = beanType;
    this.idValue = idValue;
  }

  /**
   * Add appropriate JSON content for sending to the ElasticSearch Bulk API.
   */
  @Override
  public void docStoreUpdate(DocStoreUpdateContext txn) throws IOException {
    beanType.docStoreDeleteById(idValue, txn);
  }

  /**
   * Add this event to the queue (for queue delayed processing).
   */
  @Override
  public void addToQueue(DocStoreUpdates docStoreUpdates) {
    docStoreUpdates.queueDelete(beanType.getDocStoreQueueId(), idValue);
  }
}
