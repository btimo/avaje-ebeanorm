package com.avaje.ebeanservice.docstore.api.support;

import com.avaje.ebean.DocStoreQueueEntry;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.plugin.SpiBeanType;
import com.avaje.ebeanservice.docstore.api.DocStoreUpdates;
import com.avaje.tests.model.basic.Order;
import org.assertj.core.api.StrictAssertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DocStoreDeleteEventTest {

  static EbeanServer server = Ebean.getDefaultServer();

  <T> SpiBeanType<T> beanType(Class<T> cls) {
    return server.getPluginApi().getBeanType(cls);
  }

  SpiBeanType<Order> orderType() {
    return beanType(Order.class);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void docStoreUpdate() throws Exception {

    SpiBeanType<Order> mock = (SpiBeanType<Order>) Mockito.mock(SpiBeanType.class);
    DocStoreDeleteEvent event = new DocStoreDeleteEvent(mock, 42);

    event.docStoreUpdate(null);

    verify(mock, times(1)).docStoreDeleteById(42, null);
  }

  @Test
  public void addToQueue() throws Exception {

    DocStoreDeleteEvent event = new DocStoreDeleteEvent(orderType(), 42);

    DocStoreUpdates updates = new DocStoreUpdates();
    event.addToQueue(updates);

    List<DocStoreQueueEntry> queueEntries = updates.getQueueEntries();
    assertThat(queueEntries).hasSize(1);

    DocStoreQueueEntry entry = queueEntries.get(0);
    StrictAssertions.assertThat(entry.getBeanId()).isEqualTo(42);
    StrictAssertions.assertThat(entry.getQueueId()).isEqualTo("order");
    StrictAssertions.assertThat(entry.getPath()).isNull();
    assertThat(entry.getType()).isEqualTo(DocStoreQueueEntry.Action.DELETE);
  }
}