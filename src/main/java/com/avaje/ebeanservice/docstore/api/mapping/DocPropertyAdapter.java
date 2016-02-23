package com.avaje.ebeanservice.docstore.api.mapping;

/**
 * Adapter for DocPropertyVisitor that does not do anything.
 * Used to extend and implement only the desired methods.
 */
public abstract class DocPropertyAdapter implements DocPropertyVisitor {

  @Override
  public void visitProperty(DocPropertyMapping property) {
    // do nothing
  }

  @Override
  public void visitBegin() {
    // do nothing
  }

  @Override
  public void visitEnd() {
    // do nothing
  }

  @Override
  public void visitBeginObject(DocPropertyMapping property) {
    // do nothing
  }

  @Override
  public void visitEndObject(DocPropertyMapping property) {
    // do nothing
  }

  @Override
  public void visitBeginList(DocPropertyMapping property) {
    // do nothing
  }

  @Override
  public void visitEndList(DocPropertyMapping property) {
    // do nothing
  }
}
