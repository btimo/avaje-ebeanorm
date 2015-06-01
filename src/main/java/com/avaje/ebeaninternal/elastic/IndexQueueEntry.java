package com.avaje.ebeaninternal.elastic;

/**
 * Bean holding the queueId and beanId used to queueIndex a request to updateAdd an ElasticSearch
 * index.
 */
public class IndexQueueEntry {

  /**
   * Action to either update a document in the index or delete a document from the index.
   */
  public enum Action {
    /**
     * Action is to update the index entry.
     */
    INDEX("I"),

    /**
     * Action is to update the index entry.
     */
    DELETE("D");

    String dbValue;

    Action(String dbValue) {
      this.dbValue = dbValue;
    }

    public String getDbValue() {
      return dbValue;
    }
  }

  final Action type;

  final String queueId;

  final Object beanId;

  public IndexQueueEntry(Action type, String queueId, Object beanId) {
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
