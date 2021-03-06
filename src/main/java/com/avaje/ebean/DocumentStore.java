package com.avaje.ebean;

import com.avaje.ebeanservice.docstore.api.DocQueryRequest;
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
   * This will execute the query against the database creating a document for each
   * bean graph and sending this to the document store.
   * </p>
   * <p>
   * Note that the select and fetch paths of the query is set for you to match the
   * document structure needed based on <code>@DocStore</code> and <code>@DocStoreEmbedded</code>
   * so what this query requires is the predicates only.
   * </p>
   * <p>
   * This query will be executed using findEach so it is safe to use a query
   * that will fetch a lot of beans. The default bulkBatchSize is used.
   * </p>
   *
   * @param query The query used to update the associated document store.
   */
  <T> void indexByQuery(Query<T> query);

  /**
   * Update the associated document store index using the result of the query additionally specifying a
   * bulkBatchSize to use for sending the messages to ElasticSearch.
   */
  <T> void indexByQuery(Query<T> query, int bulkBatchSize);

  /**
   * Update the document store for all beans of this type.
   * <p>
   * This is the same as indexByQuery where the query has no predicates and so fetches all rows.
   */
  void indexAll(Class<?> beanType);

  /**
   * Return the bean by fetching it's content from the document store.
   * If the document is not found null is returned.
   */
  @Nullable
  <T> T find(DocQueryRequest<T> request);

  /**
   * Execute the find list query. This request is prepared to execute secondary queries.
   */
  <T> List<T> findList(DocQueryRequest<T> request);

  /**
   * Execute the query against the document store returning the paged list.
   * <p>
   * The query should have <code>firstRow</code> or <code>maxRows</code> set prior to calling this method.
   * </p>
   */
  <T> PagedList<T> findPagedList(DocQueryRequest<T> request);

  /**
   * Execute the query against the document store with the expectation of a large set of results
   * that are processed in a scrolling resultSet fashion.
   * <p>
   * For example, with the ElasticSearch doc store this uses SCROLL.
   * </p>
   */
  <T> void findEach(DocQueryRequest<T> query, QueryEachConsumer<T> consumer);

  /**
   * Execute the query against the document store with the expectation of a large set of results
   * that are processed in a scrolling resultSet fashion.
   * <p>
   * Unlike findEach() this provides the opportunity to stop iterating through the large query.
   * </p>
   * <p>
   * For example, with the ElasticSearch doc store this uses SCROLL.
   * </p>
   */
  <T> void findEachWhile(DocQueryRequest<T> query, QueryEachWhileConsumer<T> consumer);

  /**
   * Process the queue entries sending updates to the document store or queuing them for later processing.
   */
  long process(List<DocStoreQueueEntry> queueEntries) throws IOException;

  /**
   * Drop the index from the document store (similar to DDL drop table).
   */
  void dropIndex(String indexName);

  /**
   * Create an index given a mapping file as a resource in the classPath (similar to DDL create table).
   *
   * @param indexName       the name of the new index
   * @param alias           the alias of the index
   */
  void createIndex(String indexName, String alias);

  /**
   * Copy the index to a new index.
   * <p>
   * This copy process does not use the database but instead will copy from the source index to a destination index.
   * </p>
   *
   * <pre>{@code
   *
   *  long copyCount = documentStore.copyIndex(Product.class, "product_copy");
   *
   * }</pre>
   *
   *
   * @param beanType The bean type of the source index
   * @param newIndex The name of the index to copy to
   * @return the number of documents copied to the new index
   */
  long copyIndex(Class<?> beanType, String newIndex);

  /**
   * Copy entries from an index to a new index but limiting to documents that have been
   * modified since the sinceEpochMillis time.
   * <p>
   * To support this the document needs to have a <code>@WhenModified</code> property.
   * </p>
   *
   * <pre>{@code
   *
   *  long copyCount = documentStore.copyIndex(Product.class, "product_copy", sinceMillis);
   *
   * }</pre>
   *
   *
   * @param beanType The bean type of the source index
   * @param newIndex The name of the index to copy to
   * @return the number of documents copied to the new index
   */
  long copyIndex(Class<?> beanType, String newIndex, long sinceEpochMillis);

  /**
   * Copy from a source index to a new index taking only the documents
   * matching the given query.
   *
   * <pre>{@code
   *
   *  // predicates to select the source documents to copy
   *  Query<Product> query = server.find(Product.class)
   *    .where()
   *      .ge("whenModified", new Timestamp(since))
   *      .ge("name", "A")
   *      .lt("name", "D")
   *      .query();
   *
   *  // copy from the source index to "product_copy" index
   *  long copyCount = documentStore.copyIndex(query, "product_copy", 1000);
   *
   * }</pre>
   *
   * @param query         The query to select the source documents to copy
   * @param newIndex      The target index to copy the documents to
   * @param bulkBatchSize The ElasticSearch bulk batch size, if 0 uses the default.
   * @return The number of documents copied to the new index.
   */
  long copyIndex(Query<?> query, String newIndex, int bulkBatchSize);
}
