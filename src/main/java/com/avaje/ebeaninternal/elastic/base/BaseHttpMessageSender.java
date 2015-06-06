package com.avaje.ebeaninternal.elastic.base;


import com.avaje.ebeaninternal.elastic.BulkMessageSender;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.io.Writer;

/**
 * Basic implementation for sending the JSON payload to the ElasticSearch Bulk API.
 */
public class BaseHttpMessageSender implements BulkMessageSender {

  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  final OkHttpClient client = new OkHttpClient();

  final String baseUrl;

  final String bulkUrl;

  public BaseHttpMessageSender(String baseUrl) {
    this.baseUrl = baseUrl;
    this.bulkUrl = deriveBulkUrl(baseUrl);
  }

  /**
   * Return the Bulk API URL given the base URL.
   */
  protected String deriveBulkUrl(String baseUrl) {

    if (baseUrl == null) return null;

    if (baseUrl.endsWith("_bulk")) {
      return baseUrl;
    } else if (baseUrl.endsWith("/")) {
      return baseUrl+"_bulk";
    } else {
      return baseUrl+"/_bulk";
    }
  }

  @Override
  public String post(String json) throws IOException {

    RequestBody body = RequestBody.create(JSON, json);
    Request request = new Request.Builder()
        .url(bulkUrl)
        .put(body)
        .build();

    Response response = client.newCall(request).execute();
    return response.body().string();
  }
}
