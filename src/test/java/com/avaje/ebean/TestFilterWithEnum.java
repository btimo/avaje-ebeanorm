package com.avaje.ebean;

import java.util.List;

import com.avaje.tests.model.basic.*;
import org.junit.Assert;
import org.junit.Test;

public class TestFilterWithEnum extends BaseTestCase {

  @Test
  public void test() throws InterruptedException {

//    EBasic bean = new EBasic();
//    bean.setName("asdasd");
//    bean.setStatus(EBasic.Status.ACTIVE);
//    Ebean.save(bean);
//
//    String description = bean.getDescription();
//    bean.getId();

    ResetBasicData.reset();

////    List<Order> allOrders = Ebean.find(Order.class).findList();
////
////    Filter<Order> filter = Ebean.filter(Order.class);
////    List<Order> newOrders = filter.eq("status", Order.Status.NEW).filter(allOrders);
////
////    Assert.assertNotNull(newOrders);
//
//    Contact contact = new Contact();
//    contact.setCustomer(Ebean.getReference(Customer.class,1));
//    contact.setFirstName("extra");
//
//    Ebean.save(contact);
//
//    String phone = contact.getPhone();
//    System.out.println("");

    Thread.sleep(5000);

  }
  
}
