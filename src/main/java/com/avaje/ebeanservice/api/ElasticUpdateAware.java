package com.avaje.ebeanservice.api;

import java.io.IOException;

/**
 * Created by rob on 13/10/15.
 */
public interface ElasticUpdateAware {
  void elasticBulkUpdate(BulkElasticUpdate txn) throws IOException;

  void addToQueue(DocStoreUpdates docStoreUpdates);
}
