package com.avaje.ebeanservice.api;

import com.avaje.ebean.text.PathProperties;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.text.json.WriteJson;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Bulk updates to a document store.
 */
public interface DocStoreBulkUpdate {

  /**
   * Create for building JSON payload.
   */
  WriteJson createWriteJson(SpiEbeanServer server, JsonGenerator gen, PathProperties pathProperties);

  /**
   * Return the current JSON generator.
   */
  JsonGenerator gen();

}
