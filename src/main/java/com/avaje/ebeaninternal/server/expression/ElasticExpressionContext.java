package com.avaje.ebeaninternal.server.expression;

import com.avaje.ebean.plugin.SpiBeanType;
import com.avaje.ebean.plugin.SpiExpressionPath;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 * Context for writing elastic search expressions.
 */
public class ElasticExpressionContext {

  final JsonGenerator json;

  final SpiBeanType<?> desc;

  public ElasticExpressionContext(JsonGenerator json, SpiBeanType<?> desc) {
    this.json = json;
    this.desc = desc;
  }

  /**
   * Return the JsonGenerator.
   */
  public JsonGenerator json() {
    return json;
  }

  /**
   * Flush the JsonGenerator buffer.
   */
  public void flush() throws IOException {
    json.flush();
  }

  /**
   * Return true if the path contains a many.
   */
  public boolean containsMany(String path) {
    SpiExpressionPath elPath = desc.expressionPath(path);
    return elPath == null || elPath.containsMany();
  }
}
