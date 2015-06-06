package com.avaje.ebeaninternal.elastic;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.Writer;

/**
 * For ElasticSearch Bulk API processing this holds the JsonGenerator and associated data.
 * <p>
 *   This is used to build requests to be sent to the ElasticSearch Bulk API.
 * </p>
 */
public class BulkElasticUpdate {

  final JsonGenerator generator;

  final Writer writer;

  int count;

  public BulkElasticUpdate(JsonGenerator generator, Writer writer) {
    this.generator = generator;
    this.writer = writer;
  }

  public String getBuffer() {
    return writer.toString();
  }

  public int size() {
    return count;
  }

  public int increment() {
    return count++;
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
