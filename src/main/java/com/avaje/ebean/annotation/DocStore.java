package com.avaje.ebean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the entity type maps to a document store (like ElasticSearch).
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DocStore {

  /**
   * A unique Id used when queuing reindex events.
   */
  String queueId() default "";

  /**
   * The ElasticSearch index name. If left unspecified the short name of the bean type is used.
   */
  String indexName() default "";

  /**
   * The ElasticSearch index type. If left unspecified the short name of the bean type is used.
   */
  String indexType() default "";

  /**
   * Specify the behavior when bean Insert, Update, Delete events occur.
   */
  DocStoreEvent persist() default DocStoreEvent.DEFAULT;

  /**
   * Specify the behavior when bean Insert occurs.
   */
  DocStoreEvent insert() default DocStoreEvent.DEFAULT;

  /**
   * Specify the behavior when bean Update occurs.
   */
  DocStoreEvent update() default DocStoreEvent.DEFAULT;

  /**
   * Specify the behavior when bean Delete occurs.
   */
  DocStoreEvent delete() default DocStoreEvent.DEFAULT;

  /**
   * Specify the document structure to index using 'PathProperties'.
   *
   * <h3>Example:</h3>
   * <pre>{@code
   *
   * // order document to index includes:
   * //
   * //   - * ... all the properties of order
   * //
   * //   - customer(id,name,billingAddress(*)) ... embed the customer including
   * //                                    the customers id, name and billingAddress
   * //
   * //   - details(*,product(id,sku)) ... embedding orderDetails with all its properties
   * //                                    for each orderDetail product embed the
   * //                                    product id, sku and name
   *
   *
   * @DocStore(doc = "(*,customer(id,name,billingAddress(city)),details(id,orderQty,product(id,sku,name)))")
   * @Entity @Table(name = "o_order")
   * public class Order {
   *
   * }</pre>
   */
  String doc() default "";
}
