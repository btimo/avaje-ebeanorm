package com.avaje.ebeanservice.api;

/**
 * Thrown when a document is not found in a document store.
 */
public class DocumentNotFoundException extends RuntimeException {

  /**
   * Construct with a message.
   */
  public DocumentNotFoundException(String message) {
    super(message);
  }

}
