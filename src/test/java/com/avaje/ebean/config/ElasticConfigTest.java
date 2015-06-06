package com.avaje.ebean.config;

import com.avaje.ebean.annotation.IndexEvent;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class ElasticConfigTest {

  @Test
  public void testLoadSettings() throws Exception {

    ElasticConfig config = new ElasticConfig();

    Properties properties = new Properties();
    properties.setProperty("ebean.elastic.active", "true");
    properties.setProperty("ebean.elastic.bulkBatchSize", "99");
    properties.setProperty("ebean.elastic.url", "http://foo:9800");
    properties.setProperty("ebean.elastic.persist", "IGNORE");

    PropertiesWrapper wrapper = new PropertiesWrapper("ebean", null, properties);

    config.loadSettings(wrapper);

    assertTrue(config.isActive());
    assertEquals("http://foo:9800", config.getUrl());
    assertEquals(IndexEvent.IGNORE, config.getPersist());
    assertEquals(99, config.getBulkBatchSize());

  }
}