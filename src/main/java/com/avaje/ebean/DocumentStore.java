package com.avaje.ebean;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * Document storage operations.
 */
public interface DocumentStore {

  /**
   * Update the associated document store using the result of the query.
   * <p>
   * Note that the select and fetch paths of the query is set for you to match the
   * document structure needed for the index so what this query requires is the
   * predicates only.
   * </p>
   * <p>
   *   This query will be executed using findEach so it is safe to use a query
   *   that will fetch a lot of beans. The default bulkBatchSize is used.
   * </p>
   *
   * @param query The query used to update the associated document store.
   */
  <T> void indexByQuery(Query<T> query);

  /**
   * Update the associated ElasticSearch index using the result of the query additionally specifying a
   * bulkBatchSize to use for sending the messages to ElasticSearch.
   */
  <T> void indexByQuery(Query<T> query, int bulkBatchSize);

  /**
   * Return the bean by fetching it's content from the document store.
   * If the document is not found null is returned.
   */
  @Nullable
  <T> T getById(Class<T> beanType, Object id);

  /**
   * Execute the query against the document store returning the list.
   */
  <T> List<T> findList(Query<T> query);

  /**
   * Process the queue entries.
   */
  void process(List<DocStoreQueueEntry> queueEntries) throws IOException;

}
