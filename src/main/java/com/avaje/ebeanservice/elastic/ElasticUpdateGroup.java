package com.avaje.ebeanservice.elastic;

import com.avaje.ebean.DocStoreQueueEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rob on 2/02/16.
 */
public class ElasticUpdateGroup {

  private final String queueId;

  private final List<Object> deleteIds = new ArrayList<Object>();

  private final List<Object> indexIds = new ArrayList<Object>();

  private final Map<String, Nested> pathIds = new LinkedHashMap<String, Nested>();

  protected ElasticUpdateGroup(String queueId) {
    this.queueId = queueId;
  }

  public String getQueueId() {
    return queueId;
  }

  public List<Object> getDeleteIds() {
    return deleteIds;
  }

  public List<Object> getIndexIds() {
    return indexIds;
  }

  public Map<String, Nested> getPathIds() {
    return pathIds;
  }

  private void addIndex(Object id) {
    indexIds.add(id);
  }

  private void addDelete(Object id) {
    deleteIds.add(id);
  }

  private void addNested(String path, Object beanId) {
    Nested nested = pathIds.get(path);
    if (nested == null) {
      nested = new Nested(path);
      pathIds.put(path, nested);
    }
    nested.add(beanId);
  }

  protected void addEntry(DocStoreQueueEntry entry) {

    DocStoreQueueEntry.Action type = entry.getType();
    switch (type) {
      case DELETE:
        addDelete(entry.getBeanId());
        break;
      case INDEX:
        addIndex(entry.getBeanId());
        break;
      case NESTED:
        addNested(entry.getPath(), entry.getBeanId());
        break;
      default:
        throw new IllegalArgumentException("type " + type + " not handled");
    }

  }

  public static class Nested {

    private final String path;

    private final List<Object> ids = new ArrayList<Object>();

    private Nested(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }

    public List<Object> getIds() {
      return ids;
    }

    private void add(Object id) {
      ids.add(id);
    }
  }
}
