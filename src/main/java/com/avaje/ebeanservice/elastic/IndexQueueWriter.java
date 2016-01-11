package com.avaje.ebeanservice.elastic;

import com.avaje.ebeanservice.api.DocStoreQueueEntry;

import java.util.List;

/**
 * Pushes queue entries onto a queue for future processing.
 */
public interface IndexQueueWriter {

  /**
   * Push all the queue entries onto the queue.
   */
  void queue(List<DocStoreQueueEntry> queueEntries);
}
