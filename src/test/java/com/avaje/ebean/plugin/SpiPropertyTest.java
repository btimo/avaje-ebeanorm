package com.avaje.ebean.plugin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.tests.model.basic.Customer;
import com.avaje.tests.model.basic.Order;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 */
public class SpiPropertyTest {

  static EbeanServer server = Ebean.getDefaultServer();

  <T> SpiBeanType<T> beanType(Class<T> cls) {
    return server.getPluginApi().getBeanType(cls);
  }

  @Test
  public void getVal() throws Exception {

    Customer customer = new Customer();

    Order order = new Order();
    order.setCustomer(customer);
    order.setStatus(Order.Status.APPROVED);

    SpiProperty statusProperty = beanType(Order.class).property("status");
    assertThat(statusProperty.getVal(order)).isEqualTo(order.getStatus());

    SpiProperty customerProperty = beanType(Order.class).property("customer");
    assertThat(customerProperty.getVal(order)).isEqualTo(customer);
  }
}