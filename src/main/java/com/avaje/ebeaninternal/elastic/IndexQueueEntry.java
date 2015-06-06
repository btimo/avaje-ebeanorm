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
    INDEX(1),

    /**
     * Action is to update the index entry.
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
