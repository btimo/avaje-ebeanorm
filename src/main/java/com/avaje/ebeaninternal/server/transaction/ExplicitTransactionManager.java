package com.avaje.ebeaninternal.server.transaction;

import com.avaje.ebean.BackgroundExecutor;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.api.SpiTransaction;
import com.avaje.ebeanservice.api.DocStoreUpdateProcessor;
import com.avaje.ebeaninternal.server.cluster.ClusterManager;
import com.avaje.ebeaninternal.server.core.BootupClasses;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptorManager;

import java.sql.Connection;

/**
 * TransactionManager where the transactions start with explicit "begin" statement.
 */
public class ExplicitTransactionManager extends TransactionManager {

  public ExplicitTransactionManager(ServerConfig serverConfig, ClusterManager clusterManager, BackgroundExecutor backgroundExecutor,
                                      DocStoreUpdateProcessor indexUpdateProcessor, BeanDescriptorManager descMgr, BootupClasses bootupClasses) {

    super(serverConfig, clusterManager, backgroundExecutor, indexUpdateProcessor, descMgr, bootupClasses);
  }

  /**
   * Create a ExplicitJdbcTransaction.
   */
  @Override
  protected SpiTransaction createTransaction(boolean explicit, Connection c, long id) {

    return new ExplicitJdbcTransaction(prefix + id, explicit, c, this);
  }

}
