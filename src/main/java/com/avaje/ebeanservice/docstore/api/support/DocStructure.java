package com.avaje.ebeanservice.docstore.api.support;

import com.avaje.ebean.text.PathProperties;
import com.avaje.ebean.FetchPath;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.deploy.BeanProperty;
import com.avaje.ebeaninternal.server.deploy.BeanPropertyAssoc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

  private final Map<String,PathProperties> manyRoot = new HashMap<String, PathProperties>();

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
    doc.addNested(path, embeddedDoc);
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
  public FetchPath getEmbedded(String path) {
    return embedded.get(path);
  }

  public FetchPath getEmbeddedManyRoot(String path) {
    return manyRoot.get(path);
  }

  /**
   * For 'many' nested properties we need an additional root based graph to fetch and update.
   */
  public <T> void prepareMany(BeanDescriptor<T> desc) {
    Set<String> strings = embedded.keySet();
    for (String prop : strings) {
      BeanPropertyAssoc<?> embProp = (BeanPropertyAssoc<?>)desc.getBeanProperty(prop);
      if (embProp.isMany()) {
        BeanDescriptor<?> targetDescriptor = embProp.getTargetDescriptor();
        BeanProperty idProperty = targetDescriptor.getIdProperty();
        PathProperties manyRootPath = new PathProperties();

        manyRootPath.addToPath(null, idProperty.getName());
        PathProperties nested = embedded.get(prop);
        manyRootPath.addNested(prop, nested);
        manyRoot.put(prop, manyRootPath);
      }
    }
  }
}
