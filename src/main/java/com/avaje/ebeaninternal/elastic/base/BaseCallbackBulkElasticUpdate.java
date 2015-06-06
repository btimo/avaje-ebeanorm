package com.avaje.ebeaninternal.elastic.base;

import com.avaje.ebeaninternal.elastic.BulkElasticUpdate;
import com.avaje.ebeaninternal.elastic.CallbackBulkElasticUpdate;
import com.avaje.ebeaninternal.elastic.IndexUpdateProcessor;

import java.io.IOException;
import java.util.Map;

/**
 * Base implementation of CallbackBulkElasticUpdate.
 */
public class BaseCallbackBulkElasticUpdate implements CallbackBulkElasticUpdate {

  final IndexUpdateProcessor indexUpdateProcessor;

  final int batchSize;

  int count;

  BulkElasticUpdate current;

  public BaseCallbackBulkElasticUpdate(IndexUpdateProcessor indexUpdateProcessor, int batchSize) throws IOException {
    this.indexUpdateProcessor = indexUpdateProcessor;
    this.batchSize = batchSize;
    current = indexUpdateProcessor.createBulkElasticUpdate();
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
  public BulkElasticUpdate obtain() throws IOException {

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
