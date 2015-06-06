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
  IndexEvent persist() default IndexEvent.DEFAULT;

  /**
   * Specify the behavior when bean Insert occurs.
   */
  IndexEvent insert() default IndexEvent.DEFAULT;

  /**
   * Specify the behavior when bean Update occurs.
   */
  IndexEvent update() default IndexEvent.DEFAULT;

  /**
   * Specify the behavior when bean Delete occurs.
   */
  IndexEvent delete() default IndexEvent.DEFAULT;

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
   * @ElasticIndex(path = "(*,customer(id,name,billingAddress(city)),details(id,orderQty,product(id,sku,name)))")
   * @Entity @Table(name = "o_order")
   * public class Order implements Serializable {
   *
   * }</pre>
   */
  String doc() default "";
}
