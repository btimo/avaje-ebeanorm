package com.avaje.ebeaninternal.server.deploy;

/**
 * Types of indexing for a given entity bean type.
 */
public enum BeanElasticType {

  /**
   * Not associated to an ElasticSearch index.
   */
  NONE,

  /**
   * Top level bean associated to an ElasticSearch index.
   */
  INDEX,

  /**
   * Child bean that is part of the content indexed by a parent bean.
   * For example, OrderDetail beans might be CHILD and included in the index of a parent Order bean -
   * in this case Order is INDEX and OrderDetail is EMBEDDED.
   */
  EMBEDDED
}
