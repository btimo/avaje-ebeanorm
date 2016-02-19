package com.avaje.ebeaninternal.server.expression;

import com.avaje.ebean.event.BeanQueryRequest;
import com.avaje.ebeaninternal.api.SpiExpression;

import java.io.IOException;

/**
 * Base abstract expression that does nothing for prepareExpression().
 */
abstract class NonPrepareExpression implements SpiExpression {

  @Override
  public void writeElastic(ElasticExpressionContext context) throws IOException {
    throw new IllegalStateException("Not supported");
  }

  @Override
  public void prepareExpression(BeanQueryRequest<?> request) {
    // do nothing
  }

  @Override
  public SpiExpression copyForPlanKey() {
    return this;
  }
}
