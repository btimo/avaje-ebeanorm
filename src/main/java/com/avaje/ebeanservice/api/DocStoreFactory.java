package com.avaje.ebeanservice.api;

import com.avaje.ebean.plugin.SpiServer;

/**
 * Creates the integration components for DocStore integration.
 */
public interface DocStoreFactory {

  /**
   * Create and return all the DocStore integration components.
   */
  DocStoreIntegration create(SpiServer server);

}
