// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: feature.proto

package data;

public interface FeatureListsOrBuilder extends
    // @@protoc_insertion_point(interface_extends:tensorflow.FeatureLists)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Map from feature name to feature list.
   * </pre>
   *
   * <code>map&lt;string, .tensorflow.FeatureList&gt; feature_list = 1;</code>
   */
  int getFeatureListCount();
  /**
   * <pre>
   * Map from feature name to feature list.
   * </pre>
   *
   * <code>map&lt;string, .tensorflow.FeatureList&gt; feature_list = 1;</code>
   */
  boolean containsFeatureList(
      java.lang.String key);
  /**
   * Use {@link #getFeatureListMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, data.FeatureList>
  getFeatureList();
  /**
   * <pre>
   * Map from feature name to feature list.
   * </pre>
   *
   * <code>map&lt;string, .tensorflow.FeatureList&gt; feature_list = 1;</code>
   */
  java.util.Map<java.lang.String, data.FeatureList>
  getFeatureListMap();
  /**
   * <pre>
   * Map from feature name to feature list.
   * </pre>
   *
   * <code>map&lt;string, .tensorflow.FeatureList&gt; feature_list = 1;</code>
   */

  data.FeatureList getFeatureListOrDefault(
      java.lang.String key,
      data.FeatureList defaultValue);
  /**
   * <pre>
   * Map from feature name to feature list.
   * </pre>
   *
   * <code>map&lt;string, .tensorflow.FeatureList&gt; feature_list = 1;</code>
   */

  data.FeatureList getFeatureListOrThrow(
      java.lang.String key);
}
