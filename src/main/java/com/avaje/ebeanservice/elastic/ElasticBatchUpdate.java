package com.avaje.ebeanservice.elastic;

import com.avaje.ebeanservice.api.DocStoreBulkUpdate;
import com.avaje.ebeanservice.api.DocStoreUpdateAware;

import java.io.IOException;
import java.util.Map;

/**
 * Batches calls to the BULK API based on batch size.
 */
public class ElasticBatchUpdate {

  final ElasticUpdateProcessor indexUpdateProcessor;

  final int batchSize;

  int count;

  ElasticBulkUpdate current;

  public ElasticBatchUpdate(ElasticUpdateProcessor indexUpdateProcessor, int batchSize) throws IOException {
    this.indexUpdateProcessor = indexUpdateProcessor;
    this.batchSize = batchSize;
    current = indexUpdateProcessor.createBulkElasticUpdate();
  }

  public void addEvent(DocStoreUpdateAware event) throws IOException {
    DocStoreBulkUpdate obtain = obtain();
    event.docStoreBulkUpdate(obtain);
  }

  /**
   * Flush the current buffer sending the Bulk API request to ElasticSearch.
   */
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
  private DocStoreBulkUpdate obtain() throws IOException {
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
