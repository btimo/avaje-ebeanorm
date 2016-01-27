package com.avaje.ebeanservice.elastic;

import com.avaje.ebean.DocumentStore;
import com.avaje.ebean.PersistenceIOException;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryEachConsumer;
import com.avaje.ebean.plugin.SpiBeanType;
import com.avaje.ebean.plugin.SpiServer;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.ebeanservice.api.DocStoreQueryUpdate;
import com.avaje.ebeanservice.api.DocStoreUpdateProcessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * ElasticSearch based document store.
 */
public class ElasticDocumentStore implements DocumentStore {

  private final SpiServer server;

  private final DocStoreUpdateProcessor updateProcessor;

  private final IndexMessageSender messageSender;

  private final JsonFactory jsonFactory;

  public ElasticDocumentStore(SpiServer server, DocStoreUpdateProcessor updateProcessor, IndexMessageSender messageSender, JsonFactory jsonFactory) {
    this.server = server;
    this.updateProcessor = updateProcessor;
    this.messageSender = messageSender;
    this.jsonFactory = jsonFactory;
  }

  @Override
  public <T> void indexByQuery(Query<T> query) {
    indexByQuery(query, 0);
  }

  @Override
  public <T> void indexByQuery(Query<T> query, int bulkBatchSize) {

    SpiQuery<T> spiQuery = (SpiQuery<T>) query;
    Class<T> beanType = spiQuery.getBeanType();

    SpiBeanType<T> beanDescriptor = server.getBeanType(beanType);
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

  private <T> void indexByQuery(final SpiBeanType<T> desc, Query<T> query, final DocStoreQueryUpdate queryUpdate) throws IOException {

    desc.docStoreApplyPath(query);
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

    SpiBeanType<T> beanDescriptor = server.getBeanType(beanType);
    if (beanDescriptor == null) {
      throw new IllegalArgumentException("Type [" + beanType + "] does not appear to be an entity bean type?");
    }

    try {
      JsonParser parser = getSource(beanDescriptor.getElasticIndexType(), beanDescriptor.getElasticIndexName(), id);
      T bean = beanDescriptor.jsonRead(parser, null, null);
      beanDescriptor.setBeanId(bean, id);

      return bean;

    } catch (IOException e) {
      throw new PersistenceIOException(e);
    }
  }

  private JsonParser getSource(String indexType, String indexName, Object docId) throws IOException {

    IndexMessageSenderResponse response = messageSender.getDocSource(indexType, indexName, docId.toString());

    if (response.getCode() == 200) {
      return jsonFactory.createParser(response.getBody());
    }

    throw new IOException("Response code:"+response.getCode()+" body:"+response.getBody());
  }

}
