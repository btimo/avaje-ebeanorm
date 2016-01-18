package com.avaje.ebeanservice.elastic;

import com.avaje.ebean.DocumentStore;
import com.avaje.ebean.config.DocStoreConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.plugin.SpiServer;
import com.avaje.ebeanservice.api.DocStoreFactory;
import com.avaje.ebeanservice.api.DocStoreIntegration;
import com.avaje.ebeanservice.api.DocStoreUpdateProcessor;
import com.fasterxml.jackson.core.JsonFactory;

/**
 * Factory that creates the document store integration components.
 */
public class ElasticDocStoreFactory implements DocStoreFactory {

  @Override
  public DocStoreIntegration create(SpiServer server) {

    ServerConfig serverConfig = server.getServerConfig();

    Object objectMapper = serverConfig.getObjectMapper();

    DocStoreConfig docStoreConfig = serverConfig.getDocStoreConfig();
    JsonFactory jsonFactory = new JsonFactory();
    IndexQueueWriter indexQueueWriter = new BaseIndexQueueWriter(server, "eb_elastic_queue");
    IndexMessageSender messageSender = new BaseHttpMessageSender(docStoreConfig.getUrl());

    DocStoreUpdateProcessor updateProcessor = new ElasticUpdateProcessor(indexQueueWriter, jsonFactory, objectMapper, messageSender, docStoreConfig.getBulkBatchSize());

    DocumentStore docStore = new ElasticDocumentStore(server, updateProcessor, messageSender, jsonFactory);

    return new Components(updateProcessor, docStore);
  }


  static class Components implements DocStoreIntegration {

    final DocStoreUpdateProcessor updateProcessor;
    final DocumentStore documentStore;

    Components(DocStoreUpdateProcessor updateProcessor, DocumentStore documentStore) {
      this.updateProcessor = updateProcessor;
      this.documentStore = documentStore;
    }

    @Override
    public DocStoreUpdateProcessor updateProcessor() {
      return updateProcessor;
    }

    @Override
    public DocumentStore documentStore() {
      return documentStore;
    }
  }
}
