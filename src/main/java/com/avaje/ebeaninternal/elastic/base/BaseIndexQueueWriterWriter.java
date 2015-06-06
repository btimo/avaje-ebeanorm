package com.avaje.ebeaninternal.elastic.base;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Transaction;
import com.avaje.ebeaninternal.elastic.IndexQueueWriter;
import com.avaje.ebeaninternal.elastic.IndexQueueEntry;

import java.sql.Timestamp;
import java.util.List;

/**
 * Base implementation of IndexQueueWriter that inserts the events into a database table.
 */
public class BaseIndexQueueWriterWriter implements IndexQueueWriter {

  public static final int ACTION_INDEX = 1;

  public static final int ACTION_DELETE = 2;

  public static final int PROCESSING_FALSE = 0;

  public static final int PROCESSING_TRUE = 1;

  final EbeanServer server;

  final String sql;

  public BaseIndexQueueWriterWriter(EbeanServer server, String tableName) {
    this.server = server;
    this.sql = createSql(tableName);
  }

  protected String createSql(String tableName) {
    return "insert into "+tableName+" (queue_id, doc_id, action, processing, when_queued) values (?,?,?,?,?)";
  }


  @Override
  public void queue(List<IndexQueueEntry> queueEntries) {

    if (queueEntries.isEmpty()) {
      return;
    }

    SqlUpdate sqlUpdate = server.createSqlUpdate(sql);
    Transaction transaction = server.createTransaction();
    try {
      transaction.setBatchSize(100);

      for (IndexQueueEntry entry : queueEntries) {
        sqlUpdate.setParameter(1, entry.getQueueId());
        sqlUpdate.setParameter(2, entry.getBeanId().toString());
        sqlUpdate.setParameter(3, entry.getType().getDbValue());
        sqlUpdate.setParameter(4, PROCESSING_FALSE);
        sqlUpdate.setParameter(5, new Timestamp(System.currentTimeMillis()));

        sqlUpdate.execute();
      }

      transaction.commit();

    } finally {
      transaction.end();
    }

  }
}
