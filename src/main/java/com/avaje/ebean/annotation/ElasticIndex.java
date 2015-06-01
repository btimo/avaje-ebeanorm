package com.avaje.ebean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the entity type maps to an ElasticSearch index.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticIndex {

  /**
   * A unique Id used when queuing reindex events.
   */
  String queueId();

  /**
   * The ElasticSearch index name. If left unspecified the short name of the bean type is used.
   */
  String indexName();

  /**
   * The ElasticSearch index type. If left unspecified the short name of the bean type is used.
   */
  String indexType();

  /**
   * Specify the behavior when bean Insert, Update, Delete events occur.
   */
  IndexEvent persist();

  /**
   * Specify the behavior when bean Insert occurs.
   */
  IndexEvent insert();

  /**
   * Specify the behavior when bean Update occurs.
   */
  IndexEvent update();

  /**
   * Specify the behavior when bean Delete occurs.
   */
  IndexEvent delete();

}
