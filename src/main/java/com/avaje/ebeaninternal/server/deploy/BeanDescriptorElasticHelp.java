package com.avaje.ebeaninternal.server.deploy;

import com.avaje.ebean.annotation.IndexEvent;
import com.avaje.ebean.text.PathProperties;
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

  final PathProperties pathProperties;

  final String queueId;

  final String indexType;

  final String indexName;

  final IndexEvent insert;
  final IndexEvent update;
  final IndexEvent delete;

  private final BeanProperty versionProperty;

  BeanDescriptorElasticHelp(BeanDescriptor<T> desc, DeployBeanDescriptor<T> deploy) {

    this.desc = desc;
    this.pathProperties = derivePathProperties(deploy.getElasticPathProperties());
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

  /**
   * Return the pathProperties which defines the JSON document to index.
   * This can add derived/embedded/nested parts to the document.
   */
  private PathProperties derivePathProperties(PathProperties deployPathProperties) {

    if (deployPathProperties != null) return deployPathProperties;

    // determine from annotations on the properties
    PathProperties prop = new PathProperties();

    BeanProperty[] properties = desc.propertiesNonTransient();

    for (int i = 0; i < properties.length ; i++) {
      if (!(properties[i] instanceof BeanPropertyAssocMany)) {
        prop.addToPath(null, properties[i].getName());
      }
    }

    return prop;
  }

  public String getQueueId() {
    return queueId;
  }

  public IndexEvent getIndexEvent(PersistRequest.Type persistType, IndexEvent txnMode) {

    if (txnMode == null) {
      return getIndexEvent(persistType);
    } else if (txnMode == IndexEvent.IGNORE) {
      return IndexEvent.IGNORE;
    }
    return (BeanElasticType.INDEX == elasticType) ? txnMode : getIndexEvent(persistType);
  }

  public IndexEvent getIndexEvent(PersistRequest.Type persistType) {
    switch (persistType) {
      case INSERT: return insert;
      case UPDATE: return update;
      case DELETE: return delete;
      default: return IndexEvent.IGNORE;
    }
  }

  /**
   * Return the supplied value or default to the bean name lower case.
   */
  private String derive(BeanDescriptor<T> desc, String suppliedValue) {
    return (suppliedValue != null && suppliedValue.length() > 0) ? suppliedValue : desc.getName().toLowerCase();
  }

  /**
   * Create a WriteJson given the generator and path properties.s
   */
  private WriteJson createWriteJson(JsonGenerator gen, PathProperties pathProperties) {
    return new WriteJson(server, gen, pathProperties);
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

    // use the pathProperties for 'index' requests
    WriteJson writeJson = createWriteJson(gen, pathProperties);
    desc.jsonWrite(writeJson, persistRequest.getEntityBean());
    gen.writeRaw("\n");
  }


  public void update(Object idValue, PersistRequestBean<T> persistRequest, BulkElasticUpdate txn) throws IOException {

    JsonGenerator gen = txn.gen();
    writeBulkHeader(gen, idValue, "update", persistRequest);

    // only the 'dirty' properties included in 'update' request
    WriteJson writeJson = createWriteJson(gen, null);
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
