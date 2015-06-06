package com.avaje.ebeaninternal.elastic.base;

import com.avaje.ebean.Ebean;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.elastic.*;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.tests.model.basic.Contact;
import com.fasterxml.jackson.core.JsonFactory;
import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class BaseIndexUpdateProcessorTest {

  SpiEbeanServer server = (SpiEbeanServer)Ebean.getServer(null);

  TDIndexQueue indexQueue = new TDIndexQueue();

  JsonFactory jsonFactory = new JsonFactory();

  BaseIndexUpdateProcessor processor = create();

  BeanDescriptor<Contact> contactBeanDescriptor = server.getBeanDescriptor(Contact.class);

  private BaseIndexUpdateProcessor create() {

    BulkMessageSender messageSender = new BaseHttpMessageSender("http://localhost:9200/_bulk");

    return new BaseIndexUpdateProcessor(indexQueue, jsonFactory, messageSender, 1000);
  }

  @Test
  public void testProcess() throws Exception {

    IndexDeleteByIdRequest bulkReq0 = createDeleteContactById(1300);
    IndexDeleteByIdRequest bulkReq1 = createDeleteContactById(1301);
    IndexDeleteByIdRequest bulkReq2 = createDeleteContactById(1302);

    IndexUpdates updates = new IndexUpdates();
    updates.queueIndex("contact", 1);
    updates.queueIndex("contact", 2);
    updates.add(bulkReq0);
    updates.add(bulkReq1);
    updates.add(bulkReq2);

    processor.process(updates, 1000);

    assertEquals(2, indexQueue.theQueue.size());
  }

  @Test
  public void testCreateBulkElasticUpdate() throws Exception {

    assertNotNull(processor.createBulkElasticUpdate(new StringWriter()));
  }

  @Test
  public void testCreateBatches() {


    List<BulkElasticRequest> allRequests = new ArrayList<BulkElasticRequest>();

    for (int i = 0; i < 33; i++) {
      allRequests.add(createDeleteContactById(i));
    }
    List<List<BulkElasticRequest>> batches = processor.createBatches(allRequests, 10);

    assertEquals(4, batches.size());
    assertEquals(10, batches.get(0).size());
    assertEquals(10, batches.get(1).size());
    assertEquals(10, batches.get(2).size());
    assertEquals(3, batches.get(3).size());
  }

  @Test
  public void testCreateBatchesSingle() {

    List<BulkElasticRequest> allRequests = new ArrayList<BulkElasticRequest>();

    for (int i = 0; i < 33; i++) {
      allRequests.add(createDeleteContactById(i));
    }
    List<List<BulkElasticRequest>> batches = processor.createBatches(allRequests, 100);

    assertEquals(1, batches.size());
    assertEquals(33, batches.get(0).size());
  }

  @Test
  public void testCreateBatchesBoundary() {

    List<BulkElasticRequest> allRequests = new ArrayList<BulkElasticRequest>();

    for (int i = 0; i < 33; i++) {
      allRequests.add(createDeleteContactById(i));
    }
    List<List<BulkElasticRequest>> batches = processor.createBatches(allRequests, 11);

    assertEquals(3, batches.size());
    assertEquals(11, batches.get(0).size());
    assertEquals(11, batches.get(1).size());
    assertEquals(11, batches.get(2).size());
  }

  @Test
  public void testCreateBatchesUnderBoundary() {

    List<BulkElasticRequest> allRequests = new ArrayList<BulkElasticRequest>();

    for (int i = 0; i < 29; i++) {
      allRequests.add(createDeleteContactById(i));
    }
    List<List<BulkElasticRequest>> batches = processor.createBatches(allRequests, 10);

    assertEquals(3, batches.size());
    assertEquals(10, batches.get(0).size());
    assertEquals(10, batches.get(1).size());
    assertEquals(9, batches.get(2).size());
  }

  private IndexDeleteByIdRequest createDeleteContactById(long id) {
    return new IndexDeleteByIdRequest(contactBeanDescriptor, id);
  }

  class TDIndexQueue implements IndexQueue {

    List<IndexQueueEntry> theQueue = new ArrayList<IndexQueueEntry>();

    @Override
    public void queue(List<IndexQueueEntry> queueEntries) {

      theQueue.addAll(queueEntries);
    }
  }
}