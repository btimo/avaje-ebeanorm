package com.avaje.ebeaninternal.elastic.base;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Transaction;
import com.avaje.ebeaninternal.elastic.IndexQueue;
import com.avaje.ebeaninternal.elastic.IndexQueueEntry;

import java.util.List;

/**
 * Base implementation of IndexQueue that inserts the events into a database table.
 */
public class BaseIndexQueue implements IndexQueue {


  final EbeanServer server;

  final String sql;

  public BaseIndexQueue(EbeanServer server, String tableName) {
    this.server = server;
    this.sql = createSql(tableName);
  }

  protected String createSql(String tableName) {
    return "insert into "+tableName+" (type,queue,object_id) values (?,?,?)";
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
        sqlUpdate.setParameter(1, entry.getType().getDbValue());
        sqlUpdate.setParameter(2, entry.getQueueId());
        sqlUpdate.setParameter(3, entry.getBeanId().toString());
        sqlUpdate.execute();
      }

      transaction.commit();

    } finally {
      transaction.end();
    }

  }
}
