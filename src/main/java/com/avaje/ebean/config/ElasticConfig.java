package com.avaje.ebean.config;

import com.avaje.ebean.annotation.IndexEvent;

/**
 * Configuration for the ElasticSearch integration.
 */
public class ElasticConfig {

  IndexEvent persist = IndexEvent.UPDATE;

  /**
   * Return the default behavior for when Insert, Update and Delete events occur on beans that have an associated
   * ElasticSearch index.
   */
  public IndexEvent getPersist() {
    return persist;
  }

  /**
   * Set the default behavior for when Insert, Update and Delete events occur on beans that have an associated
   * ElasticSearch index.
   */
  public void setPersist(IndexEvent persist) {
    this.persist = persist;
  }

  /**
   * Load settings specified in properties files.
   */
  public void loadSettings(PropertiesWrapper properties) {

    persist = properties.getEnum(IndexEvent.class, "elastic.persist", persist);
  }
}
