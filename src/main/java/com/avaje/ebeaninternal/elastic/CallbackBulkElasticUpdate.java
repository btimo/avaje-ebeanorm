package com.avaje.ebeaninternal.elastic;

import java.io.IOException;

/**
 * Used when iterating potentially large queries sending batches of index requests
 * to ElasticSearch Bulk API.
 * <p>
 *   When obtain() is called the buffer size is checked and the buffer flushed if necessary.
 * </p>
 */
public interface CallbackBulkElasticUpdate {


  /**
   * Obtain a BulkElasticUpdate to write bulk requests to.
   */
  BulkElasticUpdate obtain() throws IOException;

  void flush() throws IOException;
}
