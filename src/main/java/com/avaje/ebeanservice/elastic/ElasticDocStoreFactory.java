package com.avaje.ebeanservice.elastic;

import com.avaje.ebean.DocumentStore;
import com.avaje.ebean.config.DocStoreConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.plugin.SpiServer;
import com.avaje.ebeanservice.api.DocStoreFactory;
import com.avaje.ebeanservice.api.DocStoreUpdateProcessor;
import com.fasterxml.jackson.core.JsonFactory;

/**
 * Created by rob on 8/10/15.
 */
public class ElasticDocStoreFactory implements DocStoreFactory {

   @Override
  public DocStoreUpdateProcessor createUpdateProcessor(SpiServer server) {

    ServerConfig serverConfig = server.getServerConfig();

    Object objectMapper = serverConfig.getObjectMapper();

    DocStoreConfig docStoreConfig = serverConfig.getDocStoreConfig();
    JsonFactory jsonFactory = new JsonFactory();
    IndexQueueWriter indexQueueWriter = new BaseIndexQueueWriter(server, "eb_elastic_queue");
    IndexMessageSender messageSender = new BaseHttpMessageSender(docStoreConfig.getUrl());

    return new ElasticUpdateProcessor(indexQueueWriter, jsonFactory, objectMapper, messageSender, docStoreConfig.getBulkBatchSize());
  }

  public DocumentStore createDocumentStore(SpiServer server, DocStoreUpdateProcessor updateProcessor) {
   return new  ElasticDocumentStore(server, updateProcessor);
  }
}
