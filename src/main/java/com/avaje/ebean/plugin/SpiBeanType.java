package com.avaje.ebean.plugin;

import com.avaje.ebean.Query;
import com.avaje.ebean.config.dbplatform.IdType;
import com.avaje.ebean.event.BeanFindController;
import com.avaje.ebean.event.BeanPersistController;
import com.avaje.ebean.event.BeanPersistListener;
import com.avaje.ebean.event.BeanQueryAdapter;
import com.avaje.ebean.text.json.JsonReadOptions;
import com.avaje.ebeanservice.api.BulkElasticUpdate;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * Information and methods on BeanDescriptors made available to plugins.
 */
public interface SpiBeanType<T> {

  /**
   * Return the class type this BeanDescriptor describes.
   */
  Class<T> getBeanType();

  /**
   * Return true if the property is a valid known property or path for the given bean type.
   */
  boolean isValidExpression(String property);

    /**
     * Return the base table this bean type maps to.
     */
  String getBaseTable();

  /**
   * Return the id value for the given bean.
   */
  Object getBeanId(T bean);

  /**
   * Set the id value to the bean.
   */
  void setBeanId(T bean, Object idValue);

  /**
   * Return the bean persist controller.
   */
  BeanPersistController getPersistController();

  /**
   * Return the bean persist listener.
   */
  BeanPersistListener getPersistListener();

  /**
   * Return the beanFinder. Usually null unless overriding the finder.
   */
  BeanFindController getFindController();

  /**
   * Return the BeanQueryAdapter or null if none is defined.
   */
  BeanQueryAdapter getQueryAdapter();

  /**
   * Return the identity generation type.
   */
  IdType getIdType();

  /**
   * Return the sequence name associated to this entity bean type (if there is one).
   */
  String getSequenceName();

  /**
   * Apply the appropriate fetch (PathProperties) to the query such that the query returns beans matching
   * the document store structure with the expected embedded properties.
   */
  void docStoreApplyPath(Query<T> spiQuery);

  /**
   * Store the bean in the elasticSearch index (assuming the bean is fetched with appropriate path properties
   * to match the expected document structure).
   */
  void elasticIndex(Object idValue, T bean, BulkElasticUpdate bulkElasticUpdate) throws IOException;

  String getElasticIndexType();

  String getElasticIndexName();

  T jsonRead(JsonParser parser, JsonReadOptions readOptions, Object objectMapper) throws IOException;
}
