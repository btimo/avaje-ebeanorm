package com.avaje.ebeanservice.api;

/**
 * Bean holding the queueId and beanId used to queueIndex a request to updateAdd an ElasticSearch
 * index.
 */
public class DocStoreQueueEntry {

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
    DELETE(2);

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

  final Object beanId;

  public DocStoreQueueEntry(Action type, String queueId, Object beanId) {
    this.type = type;
    this.queueId = queueId;
    this.beanId = beanId;
  }

  public Action getType() {
    return type;
  }

  public String getQueueId() {
    return queueId;
  }

  public Object getBeanId() {
    return beanId;
  }
}
