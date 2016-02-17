package com.avaje.ebeanservice.elastic;

import com.avaje.ebean.DocStoreQueueEntry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Collect and organise elastic updates grouping by queueId.
 */
public class ElasticUpdateGroups {

  /**
   * Entries organised by queueId.
   */
  private final Map<String, ElasticUpdateGroup> byQueue = new LinkedHashMap<String, ElasticUpdateGroup>();

  /**
   * Add all the entries organising them by queueId and type.
   */
  public void addAll(List<DocStoreQueueEntry> queueEntries) {

    for (DocStoreQueueEntry entry : queueEntries) {
      getQueue(entry.getQueueId()).addEntry(entry);
    }
  }

  public Collection<ElasticUpdateGroup> groups() {
    return byQueue.values();
  }

  private ElasticUpdateGroup getQueue(String queueId) {
    ElasticUpdateGroup queue = byQueue.get(queueId);
    if (queue == null) {
      queue = new ElasticUpdateGroup(queueId);
      byQueue.put(queueId, queue);
    }
    return queue;
  }

}
