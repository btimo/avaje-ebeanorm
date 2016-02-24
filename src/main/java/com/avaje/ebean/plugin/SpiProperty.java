package com.avaje.ebean.plugin;

/**
 * Property of a entity bean that can be read.
 */
public interface SpiProperty {

  /**
   * Return the name of the property.
   */
  String getName();

  /**
   * Return the value of the property on the given bean.
   */
  Object getVal(Object bean);

}
