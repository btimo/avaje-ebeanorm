package com.avaje.ebeanservice.elastic;

/**
 * Created by rob on 7/06/15.
 */
public class IndexMessageSenderResponse {

  private final int code;

  private final String body;

  public IndexMessageSenderResponse(int code, String body) {
    this.code = code;
    this.body = body;
  }

  public int getCode() {
    return code;
  }

  public String getBody() {
    return body;
  }
}
