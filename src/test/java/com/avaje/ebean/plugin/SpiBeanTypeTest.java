package com.avaje.ebean.plugin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.text.PathProperties;
import com.avaje.ebean.FetchPath;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.ebeaninternal.server.querydefn.OrmQueryDetail;
import com.avaje.tests.model.basic.Customer;
import com.avaje.tests.model.basic.Order;
import com.avaje.tests.model.basic.OrderDetail;
import com.avaje.tests.model.basic.Person;
import com.avaje.tests.model.basic.Product;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SpiBeanTypeTest {

  static EbeanServer server = Ebean.getDefaultServer();

  <T> BeanType<T> beanType(Class<T> cls) {
    return server.getPluginApi().getBeanType(cls);
  }

  @Test
  public void getBeanType() throws Exception {
    assertThat(beanType(Order.class).getBeanType()).isEqualTo(Order.class);
  }

  @Test
  public void getTypeAtPath_when_ManyToOne() throws Exception {
    BeanType<Order> orderType = beanType(Order.class);
    BeanType<?> customerType = orderType.getBeanTypeAtPath("customer");
    assertThat(customerType.getBeanType()).isEqualTo(Customer.class);
  }

  @Test
  public void getTypeAtPath_when_OneToMany() throws Exception {
    BeanType<Order> orderType = beanType(Order.class);
    BeanType<?> detailsType = orderType.getBeanTypeAtPath("details");
    assertThat(detailsType.getBeanType()).isEqualTo(OrderDetail.class);
  }

  @Test
  public void getTypeAtPath_when_nested() throws Exception {
    BeanType<Order> orderType = beanType(Order.class);
    BeanType<?> productType = orderType.getBeanTypeAtPath("details.product");
    assertThat(productType.getBeanType()).isEqualTo(Product.class);
  }

  @Test(expected = RuntimeException.class)
  public void getTypeAtPath_when_simpleType() throws Exception {

    beanType(Order.class).getBeanTypeAtPath("status");
  }

  @Test
  public void property() throws Exception {

    Order order = new Order();
    order.setStatus(Order.Status.APPROVED);
    Property statusProperty = beanType(Order.class).getProperty("status");

    assertThat(statusProperty.getVal(order)).isEqualTo(order.getStatus());
  }

  @Test
  public void getBaseTable() throws Exception {

    assertThat(beanType(Order.class).getBaseTable()).isEqualTo("o_order");
  }

  @Test
  public void beanId_and_getBeanId() throws Exception {

    Order order = new Order();
    order.setId(42);

    Object id1 = beanType(Order.class).beanId(order);
    Object id2 = beanType(Order.class).getBeanId(order);

    assertThat(id1).isEqualTo(order.getId());
    assertThat(id2).isEqualTo(order.getId());
  }

  @Test
  public void setBeanId() throws Exception {

    Order order = new Order();
    beanType(Order.class).setBeanId(order, 42);

    assertThat(42).isEqualTo(order.getId());
  }

  @Test
  public void isDocStoreIndex() throws Exception {

    assertThat(beanType(Order.class).isDocStoreMapped()).isTrue();
    assertThat(beanType(Person.class).isDocStoreMapped()).isFalse();
  }

  @Test
  public void getDocStoreQueueId() throws Exception {

    assertThat(beanType(Order.class).getDocStoreQueueId()).isEqualTo("order");
    assertThat(beanType(Customer.class).getDocStoreQueueId()).isEqualTo("customer");
  }

  @Test
  public void getDocStoreIndexType() throws Exception {

    assertThat(beanType(Order.class).docStore().getIndexType()).isEqualTo("order");
    assertThat(beanType(Customer.class).docStore().getIndexType()).isEqualTo("customer");
  }

  @Test
  public void getDocStoreIndexName() throws Exception {

    assertThat(beanType(Order.class).docStore().getIndexType()).isEqualTo("order");
    assertThat(beanType(Customer.class).docStore().getIndexType()).isEqualTo("customer");
  }

  @Test
  public void docStoreNested() throws Exception {

    FetchPath parse = PathProperties.parse("id,name");

    FetchPath nestedCustomer = beanType(Order.class).docStore().getEmbedded("customer");
    assertThat(nestedCustomer.toString()).isEqualTo(parse.toString());
  }

  @Test
  public void docStoreApplyPath() throws Exception {

    SpiQuery<Order> orderQuery = (SpiQuery<Order>)server.find(Order.class);
    beanType(Order.class).docStore().applyPath(orderQuery);

    OrmQueryDetail detail = orderQuery.getDetail();
    assertThat(detail.getChunk("customer", false).getSelectProperties())
        .containsExactly("id", "name");
  }


  @Test(expected = IllegalStateException.class)
  public void docStoreIndex() throws Exception {
    beanType(Order.class).docStore().index(1, new Order(), null);
  }

  @Test(expected = IllegalStateException.class)
  public void docStoreDeleteById() throws Exception {
    beanType(Order.class).docStore().deleteById(1, null);
  }

  @Test(expected = IllegalStateException.class)
  public void docStoreUpdateEmbedded() throws Exception {
    beanType(Order.class).docStore().updateEmbedded(1, "customer", "someJson", null);
  }
}