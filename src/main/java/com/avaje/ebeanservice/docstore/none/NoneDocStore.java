package com.avaje.ebeanservice.docstore.none;

import com.avaje.ebean.DocStoreQueueEntry;
import com.avaje.ebean.DocumentStore;
import com.avaje.ebean.Query;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * DocumentStore that barfs it is used.
 */
public class NoneDocStore implements DocumentStore {

  public static final IllegalStateException ERR =
      new IllegalStateException("DocStore implementation not included."
    +" You need to add the maven dependency for avaje-ebeanorm-elastic");

  @Override
  public <T> void indexByQuery(Query<T> query) {
    throw ERR;
  }

  @Override
  public <T> void indexByQuery(Query<T> query, int bulkBatchSize) {
    throw ERR;
  }

  @Nullable
  @Override
  public <T> T getById(Class<T> beanType, Object id) {
    throw ERR;
  }

  @Override
  public <T> List<T> findList(Query<T> query) {
    throw ERR;
  }

  @Override
  public void process(List<DocStoreQueueEntry> queueEntries) throws IOException {
    throw ERR;
  }
}
