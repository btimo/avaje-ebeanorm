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

  <T> BeanType<T> beanType(Class<T> cls) {
    return server.getPluginApi().getBeanType(cls);
  }

  @Test
  public void getVal() throws Exception {

    Customer customer = new Customer();

    Order order = new Order();
    order.setCustomer(customer);
    order.setStatus(Order.Status.APPROVED);

    Property statusProperty = beanType(Order.class).getProperty("status");
    assertThat(statusProperty.getVal(order)).isEqualTo(order.getStatus());

    Property customerProperty = beanType(Order.class).getProperty("customer");
    assertThat(customerProperty.getVal(order)).isEqualTo(customer);
  }
}