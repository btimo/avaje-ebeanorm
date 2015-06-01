package com.avaje.ebeaninternal.server.deploy;

import com.avaje.ebean.annotation.IndexEvent;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.elastic.BulkElasticUpdate;
import com.avaje.ebeaninternal.server.core.PersistRequest;
import com.avaje.ebeaninternal.server.core.PersistRequestBean;
import com.avaje.ebeaninternal.server.deploy.meta.DeployBeanDescriptor;
import com.avaje.ebeaninternal.server.text.json.WriteJson;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;


public class BeanDescriptorElasticHelp<T> {

  final SpiEbeanServer server;

  final BeanDescriptor<T> desc;

  final BeanElasticType elasticType;

  String queueId;

  final String indexType;

  final String indexName;

  final IndexEvent insert;
  final IndexEvent update;
  final IndexEvent delete;

  private final BeanProperty versionProperty;

  BeanDescriptorElasticHelp(BeanDescriptor<T> desc, DeployBeanDescriptor<T> deploy) {

    this.desc = desc;
    this.server = desc.getEbeanServer();
    this.elasticType = deploy.getElasticType();
    this.queueId = derive(desc, deploy.getElasticQueueId());
    this.indexName = derive(desc, deploy.getElasticIndexName());
    this.indexType = derive(desc, deploy.getElasticIndexType());

    this.insert = deploy.getElasticInsertEvent();
    this.update = deploy.getElasticUpdateEvent();
    this.delete = deploy.getElasticDeleteEvent();

    this.versionProperty = desc.getVersionProperty();
  }

  public String getQueueId() {
    return queueId;
  }

  public IndexEvent getIndexEvent(PersistRequest.Type persistType) {
    switch (persistType) {
      case INSERT: return insert;
      case UPDATE: return update;
      case DELETE: return delete;
      default: return IndexEvent.IGNORE;
    }
  }

  private String derive(BeanDescriptor<T> desc, String suppliedValue) {
    return suppliedValue != null ? suppliedValue : desc.getName().toLowerCase();
  }

  private WriteJson createWriteJson(JsonGenerator gen) {
    return new WriteJson(server, gen, null);
  }

  public void deleteById(Object idValue, BulkElasticUpdate txn) throws IOException {
    JsonGenerator gen = txn.gen();
    writeBulkHeader(gen, idValue, "delete", null);
  }

  public void delete(Object idValue, PersistRequestBean<T> persistRequest, BulkElasticUpdate txn) throws IOException {
    JsonGenerator gen = txn.gen();
    writeBulkHeader(gen, idValue, "delete", persistRequest);
  }

  public void insert(Object idValue, PersistRequestBean<T> persistRequest, BulkElasticUpdate txn) throws IOException {

    JsonGenerator gen = txn.gen();
    writeBulkHeader(gen, idValue, "index", persistRequest);

    WriteJson writeJson = createWriteJson(gen);
    desc.jsonWrite(writeJson, persistRequest.getEntityBean());
    gen.writeRaw("\n");
  }


  public void update(Object idValue, PersistRequestBean<T> persistRequest, BulkElasticUpdate txn) throws IOException {

    JsonGenerator gen = txn.gen();
    writeBulkHeader(gen, idValue, "update", persistRequest);

    WriteJson writeJson = createWriteJson(gen);
    gen.writeStartObject();
    gen.writeFieldName("doc");
    desc.jsonWriteDirty(writeJson, persistRequest.getEntityBean(), persistRequest.getDirtyProperties());
    gen.writeEndObject();
    gen.writeRaw("\n");
  }

  public void writeBulkHeader(JsonGenerator gen, Object idValue, String event, PersistRequestBean<T> persistRequest) throws IOException {

    gen.writeStartObject();
    gen.writeFieldName(event);
    gen.writeStartObject();
    gen.writeStringField("_id", idValue.toString());
    gen.writeStringField("_type", indexType);
    gen.writeStringField("_index", indexName);

//    if (versionProperty != null) {
//      Object value = persistRequest.getVersionPropertyValue();
//      Long version = BasicTypeConverter.toLong(value);
//      if (version == null) {
//        System.out.println("********* "+versionProperty.getFullBeanName()+" version is null?");
//      } else {
//        gen.writeFieldName("_version");
//        gen.writeNumber(version.longValue());
//        if (event.equals("index")) {
//          gen.writeStringField("_version_type", "external");
//        }
//      }
//    }

    gen.writeEndObject();
    gen.writeEndObject();
    gen.writeRaw("\n");
  }

}
