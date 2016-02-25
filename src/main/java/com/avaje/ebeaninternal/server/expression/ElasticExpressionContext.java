package com.avaje.ebeaninternal.server.expression;

import com.avaje.ebean.OrderBy;
import com.avaje.ebean.plugin.SpiBeanType;
import com.avaje.ebean.plugin.SpiExpressionPath;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Context for writing elastic search expressions.
 */
public class ElasticExpressionContext {

  public static final String MUST = "must";
  public static final String SHOULD = "should";
  public static final String MUST_NOT = "must_not";
  public static final String BOOL = "bool";
  public static final String TERM = "term";
  public static final String RANGE = "range";
  public static final String TERMS = "terms";
  public static final String IDS = "ids";
  public static final String VALUES = "values";
  public static final String PREFIX = "prefix";
  public static final String MATCH = "match";
  public static final String WILDCARD = "wildcard";
  public static final String EXISTS = "exists";
  public static final String FIELD = "field";

  private final JsonGenerator json;

  private final SpiBeanType<?> desc;

  public ElasticExpressionContext(JsonGenerator json, SpiBeanType<?> desc) {
    this.json = json;
    this.desc = desc;
  }

  /**
   * Return the JsonGenerator.
   */
  public JsonGenerator json() {
    return json;
  }

  /**
   * Flush the JsonGenerator buffer.
   */
  public void flush() throws IOException {
    json.flush();
  }

  /**
   * Return true if the path contains a many.
   */
  public boolean containsMany(String path) {
    SpiExpressionPath elPath = desc.expressionPath(path);
    return elPath == null || elPath.containsMany();
  }

  /**
   * Return an associated 'raw' property given the property name.
   */
  private String rawProperty(String propertyName) {
    return desc.docStoreRawProperty(propertyName);
  }

  public void writeBoolStart(boolean conjunction) throws IOException {

    writeBoolStart((conjunction) ? MUST : SHOULD);
  }

  public void writeBoolMustStart() throws IOException {
    writeBoolStart(MUST);
  }

  public void writeBoolMustNotStart() throws IOException {
    writeBoolStart(MUST_NOT);
  }

  private void writeBoolStart(String type) throws IOException {
    json.writeStartObject();
    json.writeObjectFieldStart(BOOL);
    json.writeArrayFieldStart(type);
  }

  public void writeBoolEnd() throws IOException {
    json.writeEndArray();
    json.writeEndObject();
    json.writeEndObject();
  }

  public void writeTerm(String propertyName, Object value) throws IOException {

    writeRawType(TERM, rawProperty(propertyName), value);
  }

  public void writeRange(String propertyName, String rangeType, Object value) throws IOException {

    json.writeStartObject();
    json.writeObjectFieldStart(RANGE);
    json.writeObjectFieldStart(rawProperty(propertyName));
    json.writeFieldName(rangeType);
    json.writeObject(value);
    json.writeEndObject();
    json.writeEndObject();
    json.writeEndObject();
  }

  public void writeRange(String propertyName, Op lowOp, Object valueLow, Op highOp, Object valueHigh) throws IOException {

    json.writeStartObject();
    json.writeObjectFieldStart(RANGE);
    json.writeObjectFieldStart(rawProperty(propertyName));
    json.writeFieldName(lowOp.docExp());
    json.writeObject(valueLow);
    json.writeFieldName(highOp.docExp());
    json.writeObject(valueHigh);
    json.writeEndObject();
    json.writeEndObject();
    json.writeEndObject();
  }

  public void writeTerms(String propertyName, Object[] values) throws IOException {

    json.writeStartObject();
    json.writeObjectFieldStart(TERMS);
    json.writeArrayFieldStart(rawProperty(propertyName));
    for (Object value : values) {
      json.writeObject(value);
    }
    json.writeEndArray();
    json.writeEndObject();
    json.writeEndObject();
  }


  public void writeIds(List<?> idList) throws IOException {

    json.writeStartObject();
    json.writeObjectFieldStart(IDS);
    json.writeArrayFieldStart(VALUES);
    for (Object id : idList) {
      json.writeObject(id);
    }
    json.writeEndArray();
    json.writeEndObject();
    json.writeEndObject();
  }

  public void writeId(Object value) throws IOException {

    List<Object> ids = new ArrayList<Object>(1);
    ids.add(value);
    writeIds(ids);
  }

  public void writeSuffix(String propertyName, String value) {
    throw new IllegalArgumentException("Not implemented yet. Could search for a mapped 'reversed' property and do prefix query");
  }

  public void writePrefix(String propertyName, String value) throws IOException {
    // use analysed field
    writeRawType(PREFIX, propertyName, value);
  }

  public void writeMatch(String propertyName, String value) throws IOException {
    // use analysed field
    writeRawType(MATCH, propertyName, value);
  }

  public void writeWildcard(String propertyName, String value) throws IOException {
    writeRawType(WILDCARD, propertyName, value);
  }

  public void writeRaw(String jsonExpression) throws IOException {
    json.writeRaw(jsonExpression);
  }

  public void writeExists(boolean notNull, String propName) throws IOException {
    if (!notNull) {
      writeBoolMustNotStart();
    }
    writeExists(propName);
    if (!notNull) {
      writeBoolEnd();
    }
  }

  private void writeExists(String propName) throws IOException {
    writeRawType(EXISTS, FIELD, propName);
  }

  private void writeRawType(String type, String propertyName, Object value) throws IOException {

    json.writeStartObject();
    json.writeObjectFieldStart(type);
    json.writeFieldName(propertyName);
    json.writeObject(value);
    json.writeEndObject();
    json.writeEndObject();
  }

  public void writeSimple(Op type, String propName, Object value) throws IOException {
    switch (type) {
      case EQ:
        writeTerm(propName, value);
        break;
      case NOT_EQ:
        writeBoolMustNotStart();
        writeTerm(propName, value);
        writeBoolEnd();
        break;
      case EXISTS:
        writeExists(true, propName);
        break;
      case NOT_EXISTS:
        writeExists(false, propName);
        break;
      case BETWEEN:
        throw new IllegalStateException("BETWEEN Not expected in SimpleExpression?");

      default:
        writeRange(propName, type.docExp(), value);
    }

  }

  /**
   * Write the query sort.
   */
  public <T> void writeOrderBy(OrderBy<T> orderBy) throws IOException {

    if (orderBy != null && !orderBy.isEmpty()) {

      json.writeArrayFieldStart("sort");
      for (OrderBy.Property property : orderBy.getProperties()) {
        json.writeStartObject();
        json.writeObjectFieldStart(rawProperty(property.getProperty()));
        json.writeStringField("order", property.isAscending()? "asc" : "desc");
        json.writeEndObject();
        json.writeEndObject();
      }
      json.writeEndArray();
    }

  }
}
