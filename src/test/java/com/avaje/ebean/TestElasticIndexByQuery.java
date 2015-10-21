package com.avaje.ebean;

import com.avaje.tests.model.basic.Order;
import com.avaje.tests.model.basic.ResetBasicData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestElasticIndexByQuery extends BaseTestCase {

  @Test
  public void test() throws InterruptedException {


    ResetBasicData.reset();

    Query<Order> query = Ebean.find(Order.class)
        .where().gt("id", 1).query();

    Ebean.getDefaultServer().docStore().indexByQuery(query);

    Thread.sleep(500);

    Order order = Ebean.getDefaultServer().docStore().getById(Order.class, 2);

    assertNotNull(order);
    assertEquals(Integer.valueOf(2), order.getId());


    // allow a bit of time for the background indexing task to occur
    Thread.sleep(2000);

  }


  public void testIndexGet() {

  }
}
