package com.avaje.ebeaninternal.elastic;

import com.avaje.ebeaninternal.elastic.BulkElasticUpdate;
import com.avaje.ebeaninternal.elastic.IndexUpdates;

import java.io.IOException;

/**
 * A request that can be sent to the ElasticSearch Bulk API.
 */
public interface BulkElasticRequest {

  /**
   * Add this to the BulkElasticUpdate.
   * <p>
   * Typically this writes JSON for the ElasticSearch Bulk API with appropriate header and content.
   * </p>
   */
  void elasticBulkUpdate(BulkElasticUpdate bulk) throws IOException;

  /**
   * Convert this request to be a queue entry instead.
   * <p>
   *   This is typically done when the Bulk API fails (so we queue the request instead).
   * </p>
   */
  void addToQueue(IndexUpdates indexUpdates);
}
