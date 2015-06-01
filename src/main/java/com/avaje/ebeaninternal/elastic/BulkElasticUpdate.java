package com.avaje.ebeaninternal.elastic;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 * For ElasticSearch Bulk API processing this holds the JsonGenerator and associated data.
 * <p>
 *   This is used to build requests to be sent to the ElasticSearch Bulk API.
 * </p>
 */
public class BulkElasticUpdate {

  final JsonGenerator generator;

  public BulkElasticUpdate(JsonGenerator generator) {
    this.generator = generator;
  }

  /**
   * Return the JsonGenerator to write the JSON content to.
   */
  public JsonGenerator gen() {
    return generator;
  }

  /**
   * Flush and close.
   */
  public void flush() throws IOException {
    generator.flush();
    generator.close();
  }
}
