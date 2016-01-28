package com.avaje.ebean.text;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PathPropertiesTests {


  
  @Test
  public void test_noParentheses() {
    
    PathProperties s0 = PathProperties.parse("id,name");

    assertEquals(1, s0.getPaths().size());
    assertTrue(s0.get(null).contains("id"));
    assertTrue(s0.get(null).contains("name"));
    assertFalse(s0.get(null).contains("status"));
  }

  @Test
  public void test_noParentheses_needTrim() {
    
    PathProperties s0 = PathProperties.parse(" id, name ");

    assertEquals(1, s0.getPaths().size());
    assertTrue(s0.get(null).contains("id"));
    assertTrue(s0.get(null).contains("name"));
    assertFalse(s0.get(null).contains("status"));
  }
  
  @Test
  public void test_withParentheses() {

    PathProperties s0 = PathProperties.parse("(id,name)");

    assertEquals(1, s0.getPaths().size());
    assertTrue(s0.get(null).contains("id"));
    assertTrue(s0.get(null).contains("name"));
    assertFalse(s0.get(null).contains("status"));
  }
  
  
  @Test
  public void test_withColon() {

    PathProperties s0 = PathProperties.parse(":(id,name)");

    assertEquals(1, s0.getPaths().size());
    assertTrue(s0.get(null).contains("id"));
    assertTrue(s0.get(null).contains("name"));
    assertFalse(s0.get(null).contains("status"));
  }
  
  @Test
  public void test_nested() {

    PathProperties s1 = PathProperties.parse("id,name,shipAddr(*)");
    assertEquals(2, s1.getPaths().size());
    assertEquals(3, s1.get(null).size());
    assertTrue(s1.get(null).contains("id"));
    assertTrue(s1.get(null).contains("name"));
    assertTrue(s1.get(null).contains("shipAddr"));
    assertTrue(s1.get("shipAddr").contains("*"));
    assertEquals(1, s1.get("shipAddr").size());
  }
  
  @Test
  public void test_withParenthesesColonNested() {

    PathProperties s1 = PathProperties.parse(":(id,name,shipAddr(*))");
    assertEquals(2, s1.getPaths().size());
    assertEquals(3, s1.get(null).size());
    assertTrue(s1.get(null).contains("id"));
    assertTrue(s1.get(null).contains("name"));
    assertTrue(s1.get(null).contains("shipAddr"));
    assertTrue(s1.get("shipAddr").contains("*"));
    assertEquals(1, s1.get("shipAddr").size());
  }

  @Test
  public void test_add() {

    PathProperties root = PathProperties.parse("status,date");
    root.add("customer", PathProperties.parse("id,name"));

    PathProperties expect = PathProperties.parse("status,date,customer(id,name)");
    assertThat(root.toString()).isEqualTo(expect.toString());
  }

  @Test
  public void test_add_nested() {

    PathProperties root = PathProperties.parse("status,date");
    root.add("customer", PathProperties.parse("id,name,address(line1,city)"));

    PathProperties expect = PathProperties.parse("status,date,customer(id,name,address(line1,city))");
    assertThat(root.toString()).isEqualTo(expect.toString());
  }

  @Test
  public void test_all_properties() {

    PathProperties root = PathProperties.parse("*");
    assertThat(root.get(null)).containsExactly("*");
  }

  @Test
  public void test_all_properties_multipleLevels() {

    PathProperties root = PathProperties.parse("*,customer(*)");
    //PathProperties.Props rootProps = root.getProps(null);
    PathProperties.Props customerProps = root.getProps("customer");

    assertThat(root.get(null)).containsExactly("*","customer");
    assertThat(customerProps.getPropertiesAsString()).isEqualTo("*");

  }
}
