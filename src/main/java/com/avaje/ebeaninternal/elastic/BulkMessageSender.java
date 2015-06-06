package com.avaje.ebeaninternal.elastic;

import java.io.IOException;

/**
 * Sends the JSON to the ElasticSearch Bulk API.
 */
public interface BulkMessageSender {

  /**
   * Send the JSON to the ElasticSearch Bulk API.
   */
  String post(String json) throws IOException;

}
