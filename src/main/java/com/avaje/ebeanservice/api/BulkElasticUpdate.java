package com.avaje.ebeanservice.api;

import com.avaje.ebean.config.JsonConfig;
import com.avaje.ebean.text.PathProperties;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.text.json.WriteJson;
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

  private final JsonGenerator generator;

  private final Writer writer;

  private final Object defaultObjectMapper;

  private final JsonConfig.Include defaultInclude;

  private int count;

  public BulkElasticUpdate(JsonGenerator generator, Writer writer, Object defaultObjectMapper, JsonConfig.Include defaultInclude) {
    this.generator = generator;
    this.writer = writer;
    this.defaultObjectMapper = defaultObjectMapper;
    this.defaultInclude = defaultInclude;
  }

  public WriteJson createWriteJson(SpiEbeanServer server, JsonGenerator gen, PathProperties pathProperties) {
    return new WriteJson(server, gen, pathProperties, null, defaultObjectMapper, defaultInclude);
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
