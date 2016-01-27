package com.avaje.ebeanservice.elastic;

import java.io.IOException;

/**
 * Sends the JSON to the ElasticSearch Bulk API.
 */
public interface IndexMessageSender {

  /**
   * Send the JSON to the ElasticSearch Bulk API.
   */
  String postBulk(String json) throws IOException;

  /**
   * Get the document source for a specific document.
   */
  IndexMessageSenderResponse getDocSource(String indexType, String indexName, String docId) throws IOException;

}
