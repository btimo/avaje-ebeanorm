package com.avaje.ebeanservice.api;

/**
 * Bean holding the queueId and beanId used to queueIndex a request to updateAdd an ElasticSearch
 * index.
 */
public final class DocStoreQueueEntry {

  /**
   * Action to either update or delete a document from the index.
   */
  public enum Action {

    /**
     * Action is to update the index entry.
     */
    INDEX(1),

    /**
     * Action is to delete the index entry.
     */
    DELETE(2),

    /**
     * An update is required based on an update in a nested/embedded object at a given path.
     */
    NESTED(3);

    int dbValue;

    Action(int dbValue) {
      this.dbValue = dbValue;
    }

    public int getDbValue() {
      return dbValue;
    }
  }

  final Action type;

  final String queueId;

  final String path;

  final Object beanId;

  /**
   * Construct for an INDEX or DELETE action.
   */
  public DocStoreQueueEntry(Action type, String queueId, Object beanId) {
    this(type, queueId, null, beanId);
  }

  /**
   * Construct for an NESTED/embedded path invalidation action.
   */
  public DocStoreQueueEntry(Action type, String queueId, String path, Object beanId) {
    this.type = type;
    this.queueId = queueId;
    this.path = path;
    this.beanId = beanId;
  }

  public Action getType() {
    return type;
  }

  public String getQueueId() {
    return queueId;
  }

  public String getPath() {
    return path;
  }

  public Object getBeanId() {
    return beanId;
  }
}
