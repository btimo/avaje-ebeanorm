package com.avaje.ebeaninternal.server.expression;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.StringWriter;

public abstract class BaseElasticTest {

  public static JsonFactory factory = new JsonFactory();

  public ElasticExpressionContext context(StringWriter sb) throws IOException {

    JsonGenerator gen = factory.createGenerator(sb);
    return new ElasticExpressionContext(gen, null);
  }

}