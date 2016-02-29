package com.avaje.ebean.text;

import com.avaje.ebean.FetchPath;
import com.avaje.ebean.Query;
import com.avaje.ebeaninternal.server.query.SplitName;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This is a Tree like structure of paths and properties that can be used for
 * defining which parts of an object graph to fetch or render in JSON.
 */
public class FetchPaths implements FetchPath {

  private final Map<String, PathProps> pathMap;

  /**
   * Parse and return a PathProperties from nested string format like
   * (a,b,c(d,e),f(g)) where "c" is a path containing "d" and "e" and "f" is a
   * path containing "g" and the root path contains "a","b","c" and "f".
   */
  public static FetchPaths parse(String source) {

    PathProperties pathProps = PathPropertiesParser.parse(source);

    LinkedHashMap<String,PathProps> map = new LinkedHashMap<String, PathProps>();
    for (PathProperties.Props props : pathProps.getPathProps()) {
      String path = props.getPath();
      map.put(path, new PathProps(props.getProperties(), props.getPropertiesAsString()));
    }
    return new FetchPaths(map);
  }

  public FetchPaths add(String prefix, FetchPaths subPath) {

    LinkedHashMap<String,PathProps> map = new LinkedHashMap<String, PathProps>();

    for (Entry<String, PathProps> entry : pathMap.entrySet()) {
      map.put(entry.getKey(), entry.getValue());
    }

    for (Entry<String, PathProps> entry : subPath.pathMap.entrySet()) {
      String path = SplitName.add(prefix, entry.getKey());
      map.put(path, entry.getValue());
    }

    return new FetchPaths(map);
  }

  /**
   * Construct given the map.
   */
  private FetchPaths(Map<String, PathProps> pathMap) {
    this.pathMap = pathMap;
  }

  /**
   * Return true if there are no paths defined.
   */
  public boolean isEmpty() {
    return pathMap.isEmpty();
  }

  public String toString() {
    return pathMap.toString();
  }

  /**
   * Apply these path properties as fetch paths to the query.
   */
  public <T> void apply(Query<T> query) {

    for (Entry<String, PathProps> entry : pathMap.entrySet()) {
      String path = entry.getKey();
      String props = entry.getValue().getProperties();
      if (path == null) {
        query.select(props);
      } else {
        query.fetch(path, props);
      }
    }
  }



  @Override
  public boolean hasPath(String path) {
    return pathMap.containsKey(path);
  }

  @Override
  public Set<String> getProperties(String path) {
    PathProps props = pathMap.get(path);
    return (props == null) ? null : props.getPropSet();
  }

  //  /**
//   * Return true if the property (dot notation) is included in the PathProperties.
//   */
//  public boolean includesProperty(String name) {
//
//    String[] split = SplitName.split(name);
//    Props props = pathMap.get(split[0]);
//    return (props != null && props.includes(split[1]));
//  }
//
//  /**
//   * Return true if the property is included using a prefix.
//   */
//  public boolean includesProperty(String prefix, String name) {
//    return includesProperty(SplitName.add(prefix, name));
//  }
//
//  /**
//   * Return true if the fetch path is included in the PathProperties.
//   * <p>
//   * The fetch path is a OneToMany or ManyToMany path in dot notation.
//   * </p>
//   */
//  public boolean includesPath(String path) {
//    return pathMap.containsKey(path);
//  }
//
//  /**
//   * Return true if the path is included using a prefix.
//   */
//  public boolean includesPath(String prefix, String name) {
//    return includesPath(SplitName.add(prefix, name));
//  }

  private static class PathProps {

//    private final String path;

    private final String properties;

    private final Set<String> propSet;

    public PathProps(Set<String> propSet, String properties) {//String path,
//      this.path = path;
      this.propSet = new LinkedHashSet<String>(propSet);
      this.properties = properties;
    }

//    private PathProps copy(String newPath) {
//      return new PathProps(newPath, propSet, properties);
//    }
//
//    public String getPath() {
//      return path;
//    }

    public String getProperties() {
      return properties;
    }

    public Set<String> getPropSet() {
      return propSet;
    }
  }

}
