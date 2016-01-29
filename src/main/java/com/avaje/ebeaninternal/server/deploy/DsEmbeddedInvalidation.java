package com.avaje.ebeaninternal.server.deploy;

import com.avaje.ebeaninternal.server.core.PersistRequestBean;
import com.avaje.ebeanservice.api.DocStoreUpdates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks if a persist request means an embedded/nested object in another document needs updating.
 */
public class DsEmbeddedInvalidation {

  protected static final Logger logger = LoggerFactory.getLogger(DsEmbeddedInvalidation.class);

  protected final String queueId;

  protected final String path;

  public DsEmbeddedInvalidation(String queueId, String path) {
    this.queueId = queueId;
    this.path = path;
  }

  public void embeddedInvalidate(PersistRequestBean<?> request, DocStoreUpdates docStoreUpdates) {
    logger.warn("Invalidate queueId:" + queueId + " path:" + path + " id:" + request.getBeanId());
    docStoreUpdates.queueNested(queueId, path, request.getBeanId());
  }
}
