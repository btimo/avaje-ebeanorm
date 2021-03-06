package com.avaje.ebeaninternal.server.type;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public class DataBind {

  private final PreparedStatement pstmt;

  private final StringBuilder bindLog = new StringBuilder();

  private int pos;

  public DataBind(PreparedStatement pstmt) {
    this.pstmt = pstmt;
  }

  /**
   * Append an entry to the bind log.
   */
  public StringBuilder append(Object entry) {
    return bindLog.append(entry);
  }

  /**
   * Return the bind log.
   */
  public StringBuilder log() {
    return bindLog;
  }

  /**
   * Close the underlying prepared statement.
   */
  public void close() throws SQLException {
    pstmt.close();
  }

  public int currentPos() {
    return pos;
  }

  public void setObject(Object value) throws SQLException {
    pstmt.setObject(++pos, value);
  }

  public void setObject(Object value, int sqlType) throws SQLException {
    pstmt.setObject(++pos, value, sqlType);
  }

  public void setNull(int jdbcType) throws SQLException {
    pstmt.setNull(++pos, jdbcType);
  }

  public int nextPos() {
    return ++pos;
  }

  public void decrementPos() {
    --pos;
  }

  public int executeUpdate() throws SQLException {
    return pstmt.executeUpdate();
  }

  public PreparedStatement getPstmt() {
    return pstmt;
  }

  public void setString(String s) throws SQLException {
    pstmt.setString(++pos, s);
  }

  public void setInt(int i) throws SQLException {
    pstmt.setInt(++pos, i);
  }

  public void setLong(long i) throws SQLException {
    pstmt.setLong(++pos, i);
  }

  public void setShort(short i) throws SQLException {
    pstmt.setShort(++pos, i);
  }

  public void setFloat(float i) throws SQLException {
    pstmt.setFloat(++pos, i);
  }

  public void setDouble(double i) throws SQLException {
    pstmt.setDouble(++pos, i);
  }

  public void setBigDecimal(BigDecimal v) throws SQLException {
    pstmt.setBigDecimal(++pos, v);
  }

  public void setDate(java.sql.Date v) throws SQLException {
    pstmt.setDate(++pos, v);
  }

  public void setTimestamp(Timestamp v) throws SQLException {
    pstmt.setTimestamp(++pos, v);
  }

  public void setTime(Time v) throws SQLException {
    pstmt.setTime(++pos, v);
  }

  public void setBoolean(boolean v) throws SQLException {
    pstmt.setBoolean(++pos, v);
  }

  public void setBytes(byte[] v) throws SQLException {
    pstmt.setBytes(++pos, v);
  }

  public void setByte(byte v) throws SQLException {
    pstmt.setByte(++pos, v);
  }

  public void setChar(char v) throws SQLException {
    pstmt.setString(++pos, String.valueOf(v));
  }

  public void setBinaryStream(InputStream inputStream, long length) throws SQLException {
    pstmt.setBinaryStream(++pos, inputStream, length);
  }

  public void setBlob(byte[] bytes) throws SQLException {
    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
    pstmt.setBinaryStream(++pos, is, bytes.length);
  }

  public void setClob(String content) throws SQLException {
    Reader reader = new StringReader(content);
    pstmt.setCharacterStream(++pos, reader, content.length());
  }

}