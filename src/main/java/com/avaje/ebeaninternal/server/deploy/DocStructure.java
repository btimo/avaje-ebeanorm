package com.avaje.ebeaninternal.server.deploy;

import com.avaje.ebean.text.PathProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Document structure for mapping to document store.
 */
public class DocStructure {

  /**
   * The full document structure.
   */
  private final PathProperties doc;

  /**
   * The embedded document structures by path.
   */
  private final Map<String,PathProperties> embedded = new HashMap<String, PathProperties>();

  /**
   * Create given an initial deployment doc mapping.
   */
  public DocStructure(PathProperties pathProps) {
    this.doc = pathProps;
  }

  /**
   * Add a property at the root level.
   */
  public void addProperty(String name) {
    doc.addToPath(null, name);
  }

  /**
   * Add an embedded property with it's document structure.
   */
  public void addNested(String path, PathProperties embeddedDoc) {
    doc.add(path, embeddedDoc);
    embedded.put(path, embeddedDoc);
  }

  /**
   * Return the document structure.
   */
  public PathProperties doc() {
    return doc;
  }

  /**
   * Return the document structure for an embedded path.
   */
  public PathProperties getNested(String path) {
    return embedded.get(path);
  }

}
