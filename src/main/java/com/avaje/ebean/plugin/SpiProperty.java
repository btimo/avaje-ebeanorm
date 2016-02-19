package com.avaje.ebean.plugin;

/**
 * Property of a entity bean that can be read.
 */
public interface SpiProperty {

  /**
   * Return the value of the property on the given bean.
   */
  Object getVal(Object bean);

}
