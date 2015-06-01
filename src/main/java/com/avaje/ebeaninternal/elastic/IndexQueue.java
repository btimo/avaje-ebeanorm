package com.avaje.ebeaninternal.elastic;

import java.util.List;

/**
 * Pushes queue entries onto a queue for future processing.
 */
public interface IndexQueue {

  /**
   * Push all the queue entries onto the queue.
   */
  void queue(List<IndexQueueEntry> queueEntries);
}
