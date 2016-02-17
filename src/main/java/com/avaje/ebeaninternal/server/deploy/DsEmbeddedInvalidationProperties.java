package com.avaje.ebeaninternal.server.deploy;

import com.avaje.ebeaninternal.server.core.PersistRequestBean;
import com.avaje.ebeanservice.api.DocStoreUpdates;

/**
 * Checks if a persist request means an embedded/nested object in another document needs updating.
 *
 * This has specific properties to check (so not all properties invalidate).
 */
public final class DsEmbeddedInvalidationProperties extends DsEmbeddedInvalidation {

  /**
   * Properties that trigger invalidation.
   */
  final int[] properties;

  public DsEmbeddedInvalidationProperties(String queueId, String path, int[] properties) {
    super(queueId, path);
    this.properties = properties;
  }

  @Override
  public void embeddedInvalidate(PersistRequestBean<?> request, DocStoreUpdates docStoreUpdates) {
    if (request.hasDirtyProperty(properties)) {
      logger.warn("Invalidate queueId:" + queueId + " path:" + path + " id:" + request.getBeanId()+" - partial");
      docStoreUpdates.queueNested(queueId, path, request.getBeanId());
    }
  }


}
