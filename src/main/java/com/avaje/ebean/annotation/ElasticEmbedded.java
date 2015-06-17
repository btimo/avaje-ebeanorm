package com.avaje.ebean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the property is included in the parent ElasticSearch index.
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticEmbedded {

  /**
   * The properties on the embedded bean to include in the index.
   */
  String doc() default "";

  /**
   * Set to true if you want a OneToMany to NOT be 'Nested'.
   * <p>
   *   Not aware of any case to turn this on yet.
   * </p>
   */
  boolean flatten() default false;
}
