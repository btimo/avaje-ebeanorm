package com.avaje.ebean.annotation;

/**
 * Defines the behavior options when a Insert, Update or Delete event occurs
 * on a bean with an associated ElasticSearch index.
 * <p>
 * For some indexes or some transactions if can be beneficial to queueIndex the
 * event for later processing rather than look to updateAdd ElasticSearch at that time.
 * </p>
 */
public enum IndexEvent {

  /**
   * Add the event to the queueIndex for processing later to updateAdd the index.
   */
  QUEUE,

  /**
   * Update the ElasticSearch index.
   * <p>
   *   This will typically result in a call to the ElasticSearch bulk api to occur in
   *   a background (or foreground) thread to updateAdd the index.
   * </p>
   */
  UPDATE,

  /**
   * Ignore the event and not updateAdd any index.
   * <p>
   *   This can be used on a index or for a transaction where you want to have more
   *   manual programmatic control over the updating of the index.  Say you want to
   *   IGNORE on a particular transaction and instead manually queueIndex a bulk index updateAdd.
   * </p>
   */
  IGNORE
}
