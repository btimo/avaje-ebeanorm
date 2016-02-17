package com.avaje.ebeaninternal.server.deploy;

import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.DocStoreEvent;
import com.avaje.ebean.bean.EntityBean;
import com.avaje.ebean.text.PathProperties;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.core.PersistRequest;
import com.avaje.ebeaninternal.server.core.PersistRequestBean;
import com.avaje.ebeaninternal.server.deploy.meta.DeployBeanDescriptor;
import com.avaje.ebeaninternal.server.text.json.WriteJson;
import com.avaje.ebeanservice.api.DocStoreBulkUpdate;
import com.avaje.ebeanservice.api.DocStoreUpdates;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Helper for BeanDescriptor to handle the ElasticSearch features.
 */
public class BeanDescriptorElasticHelp<T> {

  private final SpiEbeanServer server;

  /**
   * The associated BeanDescriptor.
   */
  private final BeanDescriptor<T> desc;

  /**
   * The type of index.
   */
  private final BeanDocStoreType elasticType;

  /**
   * Nested path properties defining the doc structure for indexing.
   */
  private final DocStructure docStructure;

  /**
   * Identifier used in the queue system to identify the index.
   */
  private final String queueId;

  /**
   * ElasticSearch index type.
   */
  private final String indexType;

  /**
   * ElasticSearch index name.
   */
  private final String indexName;

  /**
   * Behavior on insert.
   */
  private final DocStoreEvent insert;

  /**
   * Behavior on update.
   */
  private final DocStoreEvent update;

  /**
   * Behavior on delete.
   */
  private final DocStoreEvent delete;

  /**
   * List of embedded paths from other documents that include this document type.
   * As such an update to this doc type means that those embedded documents need to be updated.
   */
  private final List<DsEmbeddedInvalidation> embeddedInvalidation = new ArrayList<DsEmbeddedInvalidation>();


  BeanDescriptorElasticHelp(BeanDescriptor<T> desc, DeployBeanDescriptor<T> deploy) {

    this.desc = desc;
    this.server = desc.getEbeanServer();
    this.elasticType = deploy.getDocStoreBeanType();
    this.docStructure = derivePathProperties(deploy);
    this.queueId = derive(desc, deploy.getDocStoreQueueId());
    this.indexName = derive(desc, deploy.getDocStoreIndexName());
    this.indexType = derive(desc, deploy.getDocStoreIndexType());
    this.insert = deploy.getDocStoreInsertEvent();
    this.update = deploy.getDocStoreUpdateEvent();
    this.delete = deploy.getDocStoreDeleteEvent();
  }

  /**
   * Register invalidation paths for embedded documents.
   */
  public void registerPaths() {
    if (elasticType == BeanDocStoreType.INDEX) {
      Collection<PathProperties.Props> pathProps = docStructure.doc().getPathProps();
      for (PathProperties.Props pathProp : pathProps) {
        String path = pathProp.getPath();
        if (path != null) {
          BeanDescriptor<?> targetDesc = desc.getBeanDescriptor(path);
          targetDesc.registerDocStoreInvalidationPath(desc.getDocStoreQueueId(), path, pathProp.getProperties());
        }
      }
    }
  }

  /**
   * Register a doc store invalidation listener for the given bean type, path and properties.
   */
  public void registerDocStoreInvalidationPath(String queueId, String path, Set<String> properties) {

    embeddedInvalidation.add(getEmbeddedInvalidation(queueId, path, properties));
  }

  /**
   * Return the DsInvalidationListener based on the properties, path.
   */
  private DsEmbeddedInvalidation getEmbeddedInvalidation(String queueId, String path, Set<String> properties) {

    if (properties.contains("*")) {
      return new DsEmbeddedInvalidation(queueId, path);

    } else {
      return new DsEmbeddedInvalidationProperties(queueId, path, getPropertyPositions(properties));
    }
  }

  /**
   * Return the property names as property index positions.
   */
  private int[] getPropertyPositions(Set<String> properties) {
    List<Integer> posList = new ArrayList<Integer>();
    for (String property : properties) {
      BeanProperty prop = desc.getBeanProperty(property);
      if (prop != null) {
        posList.add(prop.getPropertyIndex());
      }
    }
    int[] pos = new int[posList.size()];
    for (int i = 0; i <pos.length; i++) {
      pos[i] = posList.get(i);
    }
    return pos;
  }

  public void docStoreEmbeddedUpdate(PersistRequestBean<T> request, DocStoreUpdates docStoreUpdates) {
    for (int i = 0; i < embeddedInvalidation.size(); i++) {
      embeddedInvalidation.get(i).embeddedInvalidate(request, docStoreUpdates);
    }
  }

  /**
   * Return the pathProperties which defines the JSON document to index.
   * This can add derived/embedded/nested parts to the document.
   */
  private DocStructure derivePathProperties(DeployBeanDescriptor<T> deploy) {

    if (!BeanDocStoreType.INDEX.equals(deploy.getDocStoreBeanType())) {
      return null;
    }

    PathProperties pathProps = deploy.getDocStorePathProperties();
    boolean includeByDefault = isIncludeDefault(pathProps);
    if (pathProps  == null) {
      pathProps = new PathProperties();
    }
    DocStructure docStructure = new DocStructure(pathProps);

    BeanProperty[] properties = desc.propertiesNonTransient();
    for (int i = 0; i < properties.length; i++) {
      properties[i].docStoreInclude(includeByDefault, docStructure);
    }
    return docStructure;
  }

  private boolean isIncludeDefault(PathProperties pathProps) {
    return pathProps == null;
  }

  public PathProperties docStoreNested(String path) {
    return docStructure.getNested(path);
  }

  public void docStoreApplyPath(Query<T> query) {
    query.apply(docStructure.doc());
  }

  public boolean isDocStoreIndex() {
    return BeanDocStoreType.INDEX == elasticType;
  }

  public String getQueueId() {
    return queueId;
  }

  public DocStoreEvent getDocStoreEvent(PersistRequest.Type persistType, DocStoreEvent txnMode) {

    if (txnMode == null) {
      return getDocStoreEvent(persistType);
    } else if (txnMode == DocStoreEvent.IGNORE) {
      return DocStoreEvent.IGNORE;
    }
    return (BeanDocStoreType.INDEX == elasticType) ? txnMode : getDocStoreEvent(persistType);
  }

  private DocStoreEvent getDocStoreEvent(PersistRequest.Type persistType) {
    switch (persistType) {
      case INSERT:
        return insert;
      case UPDATE:
        return update;
      case DELETE:
        return delete;
      default:
        return DocStoreEvent.IGNORE;
    }
  }

  /**
   * Return the supplied value or default to the bean name lower case.
   */
  private String derive(BeanDescriptor<T> desc, String suppliedValue) {
    return (suppliedValue != null && suppliedValue.length() > 0) ? suppliedValue : desc.getName().toLowerCase();
  }

  public String getIndexType() {
    return indexType;
  }

  public String getIndexName() {
    return indexName;
  }

  public void deleteById(Object idValue, DocStoreBulkUpdate txn) throws IOException {
    JsonGenerator gen = txn.gen();
    writeBulkHeader(gen, idValue, "delete");
  }

  public void insert(Object idValue, EntityBean entityBean, DocStoreBulkUpdate txn) throws IOException {

    writeIndexJson(idValue, entityBean, txn);
  }

  protected void writeIndexJson(Object idValue, EntityBean entityBean, DocStoreBulkUpdate txn) throws IOException {

    JsonGenerator gen = txn.gen();
    writeBulkHeader(gen, idValue, "index");

    // use the pathProperties for 'index' requests
    WriteJson writeJson = txn.createWriteJson(server, gen, docStructure.doc());
    desc.jsonWrite(writeJson, entityBean);
    gen.writeRaw("\n");
  }


  public void update(Object idValue, PersistRequestBean<T> persistRequest, DocStoreBulkUpdate txn) throws IOException {

    JsonGenerator gen = txn.gen();
    writeBulkHeader(gen, idValue, "update");

    gen.writeStartObject();
    gen.writeFieldName("doc");
    // only the 'dirty' properties included in 'update' request
    WriteJson writeJson = txn.createWriteJson(server, gen, null);
    desc.jsonWriteDirty(writeJson, persistRequest.getEntityBean(), persistRequest.getDirtyProperties());
    gen.writeEndObject();
    gen.writeRaw("\n");
  }

  public void update(Object idValue, String embeddedProperty, String embeddedRawContent, DocStoreBulkUpdate txn) throws IOException {

    JsonGenerator gen = txn.gen();
    writeBulkHeader(gen, idValue, "update");

    gen.writeStartObject();
      gen.writeFieldName("doc");
      gen.writeStartObject();
        gen.writeFieldName(embeddedProperty);
        gen.writeRaw(":");
        gen.writeRaw(embeddedRawContent);
      gen.writeEndObject();
    gen.writeEndObject();
    gen.writeRaw("\n");
  }

  private void writeBulkHeader(JsonGenerator gen, Object idValue, String event) throws IOException {

    gen.writeStartObject();
    gen.writeFieldName(event);
    gen.writeStartObject();
    gen.writeStringField("_id", idValue.toString());
    gen.writeStringField("_type", indexType);
    gen.writeStringField("_index", indexName);
    gen.writeEndObject();
    gen.writeEndObject();
    gen.writeRaw("\n");
  }

}
