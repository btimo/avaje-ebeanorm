package com.avaje.ebeaninternal.server.deploy;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.plugin.SpiProperty;
import com.avaje.tests.model.basic.Order;
import org.junit.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanDescriptor_propertiesTest extends BaseTestCase {

  @Test
  public void allProperties() {

    BeanDescriptor<Order> desc = getBeanDescriptor(Order.class);
    Collection<? extends SpiProperty> props = desc.allProperties();

    assertThat(props).extracting("name").contains("id", "status", "orderDate", "shipDate");
  }

}