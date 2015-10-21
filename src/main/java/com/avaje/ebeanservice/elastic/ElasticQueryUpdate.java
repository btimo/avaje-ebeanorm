package com.avaje.ebeanservice.elastic;

import com.avaje.ebean.plugin.SpiBeanType;
import com.avaje.ebeanservice.api.BulkElasticUpdate;
import com.avaje.ebeanservice.api.DocStoreQueryUpdate;

import java.io.IOException;
import java.util.Map;

/**
 * ElasticSearch implementation of DocStoreQueryUpdate.
 * <p>
 * Uses the ElasticSearch BULK API.
 * </p>
 */
public class ElasticQueryUpdate<T> implements DocStoreQueryUpdate<T> {

  final ElasticUpdateProcessor indexUpdateProcessor;

  final SpiBeanType<T> beanType;

  final int batchSize;

  int count;

  BulkElasticUpdate current;

  public ElasticQueryUpdate(ElasticUpdateProcessor indexUpdateProcessor, int batchSize, SpiBeanType<T> beanType) throws IOException {
    this.indexUpdateProcessor = indexUpdateProcessor;
    this.batchSize = batchSize;
    this.beanType = beanType;
    current = indexUpdateProcessor.createBulkElasticUpdate();
  }

  @Override
  public void store(Object idValue, T bean) throws IOException {
    BulkElasticUpdate obtain = obtain();
    beanType.elasticIndex(idValue, bean, obtain);
  }

  /**
   * Flush the current buffer sending the Bulk API request to ElasticSearch.
   */
  @Override
  public void flush() throws IOException {

    // send the current buffer and collect any errors
    Map<String, Object> response = indexUpdateProcessor.sendBulk(current);
    collectErrors(response);

    // create a new buffer and reset count to 0
    current = indexUpdateProcessor.createBulkElasticUpdate();
    count = 0;
  }

  /**
   * Obtain a BulkElasticUpdate for writing bulk requests to.
   */
  private BulkElasticUpdate obtain() throws IOException {
    if (count++ > batchSize) {
      flush();
    }
    return current;
  }

  /**
   * Collect all the error responses for reporting back on completion.
   */
  protected void collectErrors(Map<String, Object> response) {

  }

}
