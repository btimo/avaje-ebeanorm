package com.avaje.ebeanservice.elastic;

import com.avaje.ebean.DocStoreQueueEntry;
import com.avaje.ebean.DocumentStore;
import com.avaje.ebean.PersistenceIOException;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryEachConsumer;
import com.avaje.ebean.bean.EntityBean;
import com.avaje.ebean.plugin.SpiBeanType;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeanservice.api.DocStoreDeleteEvent;
import com.avaje.ebeanservice.api.DocStoreIndexEvent;
import com.avaje.ebeanservice.api.DocStoreQueryUpdate;
import com.avaje.ebeanservice.api.DocumentNotFoundException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * ElasticSearch based document store.
 */
public class ElasticDocumentStore implements DocumentStore {

  private final SpiEbeanServer server;

  private final ElasticUpdateProcessor updateProcessor;

  private final IndexMessageSender messageSender;

  private final JsonFactory jsonFactory;

  public ElasticDocumentStore(SpiEbeanServer server, ElasticUpdateProcessor updateProcessor, IndexMessageSender messageSender, JsonFactory jsonFactory) {
    this.server = server;
    this.updateProcessor = updateProcessor;
    this.messageSender = messageSender;
    this.jsonFactory = jsonFactory;
  }

  @Override
  public void process(List<DocStoreQueueEntry> entries) throws IOException {

    ElasticUpdateGroups groups = new ElasticUpdateGroups();
    groups.addAll(entries);

    ElasticBatchUpdate txn = updateProcessor.createBatchUpdate(0);

    try {
      for (ElasticUpdateGroup group : groups.groups()) {
        BeanDescriptor<?> desc = server.getBeanDescriptorByQueueId(group.getQueueId());
        processGroup(txn, group, desc);
      }

      txn.flush();

    } catch (IOException e) {
      throw new PersistenceIOException(e);
    }
  }

  private <T> void processGroup(ElasticBatchUpdate txn, ElasticUpdateGroup group, BeanDescriptor<T> desc) throws IOException {

    List<Object> deleteIds = group.getDeleteIds();
    for (Object id : deleteIds) {
      txn.addEvent(new DocStoreDeleteEvent(desc, id));
    }

    List<Object> indexIds = group.getIndexIds();
    if (!indexIds.isEmpty()) {
      Query<T> query = server.createQuery(desc.getBeanType());
      query.where().idIn(indexIds);
      indexUsingQuery(desc, query, txn);
    }

    Collection<ElasticUpdateGroup.Nested> values = group.getPathIds().values();
    for (ElasticUpdateGroup.Nested nested : values) {
      NestedDocUpdate nestedDocUpdate = new NestedDocUpdate(server, desc, txn, nested);
      nestedDocUpdate.process();
    }
  }

  @Override
  public <T> void indexByQuery(Query<T> query) {
    indexByQuery(query, 0);
  }

  @Override
  public <T> void indexByQuery(Query<T> query, int bulkBatchSize) {

    SpiQuery<T> spiQuery = (SpiQuery<T>) query;
    Class<T> beanType = spiQuery.getBeanType();

    SpiBeanType<T> beanDescriptor = server.getBeanDescriptor(beanType);
    if (beanDescriptor == null) {
      throw new IllegalArgumentException("Type [" + beanType + "] does not appear to be an entity bean type?");
    }

    try {
      DocStoreQueryUpdate update = updateProcessor.createQueryUpdate(beanDescriptor, bulkBatchSize);
      indexByQuery(beanDescriptor, query, update);
      update.flush();

    } catch (IOException e) {
      throw new PersistenceIOException(e);
    }
  }

  private <T> void indexUsingQuery(final BeanDescriptor<T> desc, Query<T> query, final ElasticBatchUpdate txn) throws IOException {

    desc.docStoreApplyPath(query);
    query.setLazyLoadBatchSize(100);
    query.findEach(new QueryEachConsumer<T>() {
      @Override
      public void accept(T bean) {
        Object idValue = desc.getBeanId(bean);
        try {
          txn.addEvent(new DocStoreIndexEvent(desc, idValue, (EntityBean)bean));
        } catch (Exception e) {
          throw new PersistenceIOException("Error performing query update to doc store", e);
        }
      }
    });
  }

  private <T> void indexByQuery(final SpiBeanType<T> desc, Query<T> query, final DocStoreQueryUpdate queryUpdate) throws IOException {

    desc.docStoreApplyPath(query);
    query.setLazyLoadBatchSize(100);
    query.findEach(new QueryEachConsumer<T>() {
      @Override
      public void accept(T bean) {
        Object idValue = desc.getBeanId(bean);
        try {
          queryUpdate.store(idValue, bean);
        } catch (Exception e) {
          throw new PersistenceIOException("Error performing query update to doc store", e);
        }
      }
    });
  }

  @Override
  public <T> T getById(Class<T> beanType, Object id) {

    SpiBeanType<T> beanDescriptor = server.getBeanDescriptor(beanType);
    if (beanDescriptor == null) {
      throw new IllegalArgumentException("Type [" + beanType + "] does not appear to be an entity bean type?");
    }

    try {
      JsonParser parser = getSource(beanDescriptor.getDocStoreIndexType(), beanDescriptor.getDocStoreIndexName(), id);
      T bean = beanDescriptor.jsonRead(parser, null, null);
      beanDescriptor.setBeanId(bean, id);

      return bean;

    } catch (DocumentNotFoundException e) {
      // this is treated like findUnique() so returning null
      return null;

    } catch (IOException e) {
      throw new PersistenceIOException(e);
    }
  }

  private JsonParser getSource(String indexType, String indexName, Object docId) throws IOException, DocumentNotFoundException {

    IndexMessageResponse response = messageSender.getDocSource(indexType, indexName, docId.toString());

    switch (response.getCode()) {
      case 404:
        throw new DocumentNotFoundException("indexType: " + indexType + " indexName:" + indexName + " id:" + docId + " not found");
      case 200:
        return jsonFactory.createParser(response.getBody());
      default:
    }

    throw new IOException("Response code:"+response.getCode()+" body:"+response.getBody());
  }

}
