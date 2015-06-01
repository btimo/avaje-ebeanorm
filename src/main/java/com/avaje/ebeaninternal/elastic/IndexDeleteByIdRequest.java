package com.avaje.ebeaninternal.elastic;

import com.avaje.ebeaninternal.elastic.base.BulkElasticRequest;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;

import java.io.IOException;

/**
 * A 'Delete by Id' request that is send to the ElasticSearch Bulk API.
 */
public class IndexDeleteByIdRequest implements BulkElasticRequest {

  final BeanDescriptor<?> beanDescriptor;

  final Object idValue;

  public IndexDeleteByIdRequest(BeanDescriptor<?> beanDescriptor, Object idValue) {
    this.beanDescriptor = beanDescriptor;
    this.idValue = idValue;
  }

  /**
   * Add appropriate JSON content for sending to the ElasticSearch Bulk API.
   */
  public void elasticBulkUpdate(BulkElasticUpdate txn) throws IOException {

    beanDescriptor.elasticDeleteById(idValue, txn);
  }

  /**
   * Add this event to the queue entries in IndexUpdates.
   */
  @Override
  public void addToQueue(IndexUpdates indexUpdates) {
    indexUpdates.queueDelete(beanDescriptor.getElasticQueueId(), idValue);
  }
}
