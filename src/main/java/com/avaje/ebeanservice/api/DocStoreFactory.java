package com.avaje.ebeanservice.api;

import com.avaje.ebean.DocumentStore;
import com.avaje.ebean.plugin.SpiServer;

/**
 * Created by rob on 8/10/15.
 */
public interface DocStoreFactory {

  DocStoreUpdateProcessor createUpdateProcessor(SpiServer server);

  DocumentStore createDocumentStore(SpiServer server, DocStoreUpdateProcessor updateProcessor);

}
