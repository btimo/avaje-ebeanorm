package com.avaje.ebeanservice.docstore.none;

import com.avaje.ebean.DocStoreQueueEntry;
import com.avaje.ebean.DocumentStore;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryEachConsumer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * DocumentStore that barfs it is used.
 */
public class NoneDocStore implements DocumentStore {

  public static IllegalStateException implementationNotInClassPath() {
    throw new IllegalStateException("DocStore implementation not included in the classPath. You need to add the maven dependency for avaje-ebeanorm-elastic");
  }
  
  @Override
  public <T> void indexByQuery(Query<T> query) {
    throw implementationNotInClassPath();
  }

  @Override
  public <T> void indexByQuery(Query<T> query, int bulkBatchSize) {
    throw implementationNotInClassPath();
  }

  @Nullable
  @Override
  public <T> T getById(Class<T> beanType, Object id) {
    throw implementationNotInClassPath();
  }

  @Override
  public <T> List<T> findList(Query<T> query) {
    throw implementationNotInClassPath();
  }

  @Override
  public <T> void findEach(Query<T> query, QueryEachConsumer<T> consumer) {
    throw implementationNotInClassPath();
  }

  @Override
  public void process(List<DocStoreQueueEntry> queueEntries) throws IOException {
    throw implementationNotInClassPath();
  }
}
