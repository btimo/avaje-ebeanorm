package com.avaje.ebean.plugin;

import com.avaje.ebean.Query;
import com.avaje.ebean.config.dbplatform.IdType;
import com.avaje.ebean.event.BeanFindController;
import com.avaje.ebean.event.BeanPersistController;
import com.avaje.ebean.event.BeanPersistListener;
import com.avaje.ebean.event.BeanQueryAdapter;
import com.avaje.ebean.text.PathProperties;
import com.avaje.ebean.text.json.JsonReadOptions;
import com.avaje.ebeanservice.docstore.api.DocStoreUpdateContext;
import com.avaje.ebeanservice.docstore.api.mapping.DocumentMapping;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.Collection;

/**
 * Information and methods on BeanDescriptors made available to plugins.
 */
public interface SpiBeanType<T> {

  /**
   * Return the class type this BeanDescriptor describes.
   */
  Class<T> getBeanType();

  /**
   * Return the type bean for an OneToMany or ManyToOne or ManyToMany property.
   */
  SpiBeanType<?> getBeanTypeAtPath(String propertyName);

  /**
   * Return all the properties for this bean type.
   */
  Collection<? extends SpiProperty> allProperties();

  /**
   * Return the SpiProperty for a property to read values from a bean.
   */
  SpiProperty property(String propertyName);

  /**
   * Return the SpiExpressionPath for a given property path.
   * <p>
   * This can return a property or nested property path.
   * </p>
   */
  SpiExpressionPath expressionPath(String path);

  /**
   * Return true if the property is a valid known property or path for the given bean type.
   */
  boolean isValidExpression(String property);

  /**
   * Return the base table this bean type maps to.
   */
  String getBaseTable();

  /**
   * Create a new instance of the bean.
   */
  T createBean();

  /**
   * Return the bean id. This is the same as getBeanId() but without the generic type.
   */
  Object beanId(Object bean);

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
   * Return true if this bean type has doc store backing.
   */
  boolean isDocStoreMapped();

  /**
   * Return the DocumentMapping for this bean type.
   * <p>
   * This is the document structure and mapping options for how this bean type is mapped
   * for the document store.
   * </p>
   */
  DocumentMapping getDocMapping();

  /**
   * Return the doc store queueId for this bean type.
   */
  String getDocStoreQueueId();

  /**
   * Return the doc store index type for this bean type.
   */
  String getDocStoreIndexType();

  /**
   * Return the doc store index name for this bean type.
   */
  String getDocStoreIndexName();

  /**
   * Apply the appropriate fetch (PathProperties) to the query such that the query returns beans matching
   * the document store structure with the expected embedded properties.
   */
  void docStoreApplyPath(Query<T> spiQuery);

  /**
   * Return the document structure of a nested/embedded document.
   */
  PathProperties docStoreNested(String path);

  /**
   * Store the bean in the doc store index.
   * <p>
   * This somewhat assumes the bean is fetched with appropriate path properties
   * to match the expected document structure.
   */
  void docStoreIndex(Object idValue, T bean, DocStoreUpdateContext txn) throws IOException;

  /**
   * Add a delete by Id to the doc store.
   */
  void docStoreDeleteById(Object idValue, DocStoreUpdateContext txn) throws IOException;

  /**
   * Add a embedded document update to the doc store.
   *
   * @param idValue            the Id value of the bean holding the embedded document
   * @param embeddedProperty   the embedded property
   * @param embeddedRawContent the content of the embedded document in JSON form
   * @param txn                the doc store transaction to add the update to
   */
  void docStoreUpdateEmbedded(Object idValue, String embeddedProperty, String embeddedRawContent, DocStoreUpdateContext txn) throws IOException;

  /**
   * Read the JSON content returning the bean.
   */
  T jsonRead(JsonParser parser, JsonReadOptions readOptions, Object objectMapper) throws IOException;
}
