package com.avaje.ebeaninternal.server.transaction;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.annotation.IndexEvent;
import com.avaje.ebeaninternal.elastic.IndexDeleteByIdRequest;
import com.avaje.ebeaninternal.elastic.IndexUpdates;
import com.avaje.ebeaninternal.server.core.PersistRequest;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;

/**
 * Beans deleted by Id used for updating L2 Cache.
 */
public final class DeleteByIdMap {

  private final Map<String, BeanPersistIds> beanMap = new LinkedHashMap<String, BeanPersistIds>();

  public String toString() {
    return beanMap.toString();
  }

  public void notifyCache() {
    for (BeanPersistIds deleteIds : beanMap.values()) {
      BeanDescriptor<?> d = deleteIds.getBeanDescriptor();
      List<Serializable> idValues = deleteIds.getDeleteIds();
      if (idValues != null) {
        d.queryCacheClear();
        for (int i = 0; i < idValues.size(); i++) {
          d.cacheBeanRemove(idValues.get(i));
        }
      }
    }

  }

  public boolean isEmpty() {
    return beanMap.isEmpty();
  }

  public Collection<BeanPersistIds> values() {
    return beanMap.values();
  }

  /**
   * Add a Insert Update or Delete payload.
   */
  public void add(BeanDescriptor<?> desc, Object id) {

    BeanPersistIds r = getPersistIds(desc);
    r.addId(PersistRequest.Type.DELETE, (Serializable) id);
  }

  /**
   * Add a List of Insert Update or Delete Id's.
   */
  public void addList(BeanDescriptor<?> desc, List<Object> idList) {

    BeanPersistIds r = getPersistIds(desc);
    for (int i = 0; i < idList.size(); i++) {
      r.addId(PersistRequest.Type.DELETE, (Serializable) idList.get(i));
    }
  }

  private BeanPersistIds getPersistIds(BeanDescriptor<?> desc) {
    String beanType = desc.getFullName();
    BeanPersistIds r = beanMap.get(beanType);
    if (r == null) {
      r = new BeanPersistIds(desc);
      beanMap.put(beanType, r);
    }
    return r;
  }


  /**
   * Add to the ElasticSearch IndexUpdates.
   */
  public void addToIndexUpdates(IndexUpdates indexUpdates) {
    for (BeanPersistIds deleteIds : beanMap.values()) {
      BeanDescriptor<?> desc = deleteIds.getBeanDescriptor();
      IndexEvent indexEvent = desc.getIndexEvent(PersistRequest.Type.DELETE);
      if (IndexEvent.IGNORE != indexEvent) {
        // Add to queue or bulk update entries
        boolean queue = (IndexEvent.QUEUE == indexEvent);
        String queueId = desc.getElasticQueueId();
        List<Serializable> idValues = deleteIds.getDeleteIds();
        if (idValues != null) {
          for (int i = 0; i < idValues.size(); i++) {
            if (queue) {
              indexUpdates.queueDelete(queueId, idValues.get(i));
            } else {
              indexUpdates.add(new IndexDeleteByIdRequest(desc, idValues.get(i)));
            }
          }
        }
      }
    }
  }
}
