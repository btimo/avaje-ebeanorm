package com.avaje.tests.elastic.queue;

import com.avaje.ebean.annotation.CreatedTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Placeholder bean to create the queue table.
 *
 * This bean is not intended to be used per say but currently just here to generate DDL for tests.
 */
@Entity
@Table(name="eb_elastic_queue")
public class ElasticQueueBean {

  @Id
  Long id;

  @CreatedTimestamp
  Timestamp whenQueued;

  @Column(length = 20, nullable = false)
  String queueId;

  @Column(length = 40, nullable = false)
  String docId;

  /**
   * 1 - Index, 2 - Delete.
   */
  @Column(nullable = false)
  int action;

  /**
   * 0 - Not processing, 1 - Processing.
   */
  @Column(nullable = false)
  int processing;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Timestamp getWhenQueued() {
    return whenQueued;
  }

  public void setWhenQueued(Timestamp whenQueued) {
    this.whenQueued = whenQueued;
  }

  public String getQueueId() {
    return queueId;
  }

  public void setQueueId(String queueId) {
    this.queueId = queueId;
  }

  public String getDocId() {
    return docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public int getProcessing() {
    return processing;
  }

  public void setProcessing(int processing) {
    this.processing = processing;
  }
}
