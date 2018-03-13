// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tensorflow/core/example/feature.proto

package org.tensorflow.example;

/**
 * Protobuf type {@code tensorflow.Features}
 */
public  final class Features extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:tensorflow.Features)
    FeaturesOrBuilder {
  // Use Features.newBuilder() to construct.
  private Features(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Features() {
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private Features(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
              feature_ = com.google.protobuf.MapField.newMapField(
                  FeatureDefaultEntryHolder.defaultEntry);
              mutable_bitField0_ |= 0x00000001;
            }
            com.google.protobuf.MapEntry<java.lang.String, org.tensorflow.example.Feature>
            feature__ = input.readMessage(
                FeatureDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
            feature_.getMutableMap().put(
                feature__.getKey(), feature__.getValue());
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.tensorflow.example.FeatureProtos.internal_static_tensorflow_Features_descriptor;
  }

  @Override
@SuppressWarnings({"rawtypes"})
  protected com.google.protobuf.MapField internalGetMapField(
      int number) {
    switch (number) {
      case 1:
        return internalGetFeature();
      default:
        throw new RuntimeException(
            "Invalid map field number: " + number);
    }
  }
  @Override
protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.tensorflow.example.FeatureProtos.internal_static_tensorflow_Features_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.tensorflow.example.Features.class, org.tensorflow.example.Features.Builder.class);
  }

  public static final int FEATURE_FIELD_NUMBER = 1;
  private static final class FeatureDefaultEntryHolder {
    static final com.google.protobuf.MapEntry<
        java.lang.String, org.tensorflow.example.Feature> defaultEntry =
            com.google.protobuf.MapEntry
            .<java.lang.String, org.tensorflow.example.Feature>newDefaultInstance(
                org.tensorflow.example.FeatureProtos.internal_static_tensorflow_Features_FeatureEntry_descriptor, 
                com.google.protobuf.WireFormat.FieldType.STRING,
                "",
                com.google.protobuf.WireFormat.FieldType.MESSAGE,
                org.tensorflow.example.Feature.getDefaultInstance());
  }
  private com.google.protobuf.MapField<
      java.lang.String, org.tensorflow.example.Feature> feature_;
  private com.google.protobuf.MapField<java.lang.String, org.tensorflow.example.Feature>
  internalGetFeature() {
    if (feature_ == null) {
      return com.google.protobuf.MapField.emptyMapField(
          FeatureDefaultEntryHolder.defaultEntry);
    }
    return feature_;
  }

  @Override
public int getFeatureCount() {
    return internalGetFeature().getMap().size();
  }
  /**
   * <pre>
   * Map from feature name to feature.
   * </pre>
   *
   * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
   */

  @Override
public boolean containsFeature(
      java.lang.String key) {
    if (key == null) { throw new java.lang.NullPointerException(); }
    return internalGetFeature().getMap().containsKey(key);
  }
  /**
   * Use {@link #getFeatureMap()} instead.
   */
  @Override
@java.lang.Deprecated
  public java.util.Map<java.lang.String, org.tensorflow.example.Feature> getFeature() {
    return getFeatureMap();
  }
  /**
   * <pre>
   * Map from feature name to feature.
   * </pre>
   *
   * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
   */

  @Override
public java.util.Map<java.lang.String, org.tensorflow.example.Feature> getFeatureMap() {
    return internalGetFeature().getMap();
  }
  /**
   * <pre>
   * Map from feature name to feature.
   * </pre>
   *
   * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
   */

  @Override
public org.tensorflow.example.Feature getFeatureOrDefault(
      java.lang.String key,
      org.tensorflow.example.Feature defaultValue) {
    if (key == null) { throw new java.lang.NullPointerException(); }
    java.util.Map<java.lang.String, org.tensorflow.example.Feature> map =
        internalGetFeature().getMap();
    return map.containsKey(key) ? map.get(key) : defaultValue;
  }
  /**
   * <pre>
   * Map from feature name to feature.
   * </pre>
   *
   * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
   */

  @Override
public org.tensorflow.example.Feature getFeatureOrThrow(
      java.lang.String key) {
    if (key == null) { throw new java.lang.NullPointerException(); }
    java.util.Map<java.lang.String, org.tensorflow.example.Feature> map =
        internalGetFeature().getMap();
    if (!map.containsKey(key)) {
      throw new java.lang.IllegalArgumentException();
    }
    return map.get(key);
  }

  private byte memoizedIsInitialized = -1;
  @Override
public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @Override
public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    com.google.protobuf.GeneratedMessageV3
      .serializeStringMapTo(
        output,
        internalGetFeature(),
        FeatureDefaultEntryHolder.defaultEntry,
        1);
  }

  @Override
public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (java.util.Map.Entry<java.lang.String, org.tensorflow.example.Feature> entry
         : internalGetFeature().getMap().entrySet()) {
      com.google.protobuf.MapEntry<java.lang.String, org.tensorflow.example.Feature>
      feature__ = FeatureDefaultEntryHolder.defaultEntry.newBuilderForType()
          .setKey(entry.getKey())
          .setValue(entry.getValue())
          .build();
      size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, feature__);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.tensorflow.example.Features)) {
      return super.equals(obj);
    }
    org.tensorflow.example.Features other = (org.tensorflow.example.Features) obj;

    boolean result = true;
    result = result && internalGetFeature().equals(
        other.internalGetFeature());
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptorForType().hashCode();
    if (!internalGetFeature().getMap().isEmpty()) {
      hash = (37 * hash) + FEATURE_FIELD_NUMBER;
      hash = (53 * hash) + internalGetFeature().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.tensorflow.example.Features parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.tensorflow.example.Features parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.tensorflow.example.Features parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.tensorflow.example.Features parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.tensorflow.example.Features parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.tensorflow.example.Features parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.tensorflow.example.Features parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.tensorflow.example.Features parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.tensorflow.example.Features parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.tensorflow.example.Features parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.tensorflow.example.Features prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code tensorflow.Features}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:tensorflow.Features)
      org.tensorflow.example.FeaturesOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.tensorflow.example.FeatureProtos.internal_static_tensorflow_Features_descriptor;
    }

    @Override
	@SuppressWarnings({"rawtypes"})
    protected com.google.protobuf.MapField internalGetMapField(
        int number) {
      switch (number) {
        case 1:
          return internalGetFeature();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @Override
	@SuppressWarnings({"rawtypes"})
    protected com.google.protobuf.MapField internalGetMutableMapField(
        int number) {
      switch (number) {
        case 1:
          return internalGetMutableFeature();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @Override
	protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.tensorflow.example.FeatureProtos.internal_static_tensorflow_Features_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.tensorflow.example.Features.class, org.tensorflow.example.Features.Builder.class);
    }

    // Construct using org.tensorflow.example.Features.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
	public Builder clear() {
      super.clear();
      internalGetMutableFeature().clear();
      return this;
    }

    @Override
	public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.tensorflow.example.FeatureProtos.internal_static_tensorflow_Features_descriptor;
    }

    @Override
	public org.tensorflow.example.Features getDefaultInstanceForType() {
      return org.tensorflow.example.Features.getDefaultInstance();
    }

    @Override
	public org.tensorflow.example.Features build() {
      org.tensorflow.example.Features result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
	public org.tensorflow.example.Features buildPartial() {
      org.tensorflow.example.Features result = new org.tensorflow.example.Features(this);
      int from_bitField0_ = bitField0_;
      result.feature_ = internalGetFeature();
      result.feature_.makeImmutable();
      onBuilt();
      return result;
    }

    @Override
	public Builder clone() {
      return super.clone();
    }
    @Override
	public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
	public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
	public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
	public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
	public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
	public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.tensorflow.example.Features) {
        return mergeFrom((org.tensorflow.example.Features)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.tensorflow.example.Features other) {
      if (other == org.tensorflow.example.Features.getDefaultInstance()) return this;
      internalGetMutableFeature().mergeFrom(
          other.internalGetFeature());
      onChanged();
      return this;
    }

    @Override
	public final boolean isInitialized() {
      return true;
    }

    @Override
	public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.tensorflow.example.Features parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.tensorflow.example.Features) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private com.google.protobuf.MapField<
        java.lang.String, org.tensorflow.example.Feature> feature_;
    private com.google.protobuf.MapField<java.lang.String, org.tensorflow.example.Feature>
    internalGetFeature() {
      if (feature_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            FeatureDefaultEntryHolder.defaultEntry);
      }
      return feature_;
    }
    private com.google.protobuf.MapField<java.lang.String, org.tensorflow.example.Feature>
    internalGetMutableFeature() {
      onChanged();;
      if (feature_ == null) {
        feature_ = com.google.protobuf.MapField.newMapField(
            FeatureDefaultEntryHolder.defaultEntry);
      }
      if (!feature_.isMutable()) {
        feature_ = feature_.copy();
      }
      return feature_;
    }

    @Override
	public int getFeatureCount() {
      return internalGetFeature().getMap().size();
    }
    /**
     * <pre>
     * Map from feature name to feature.
     * </pre>
     *
     * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
     */

    @Override
	public boolean containsFeature(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      return internalGetFeature().getMap().containsKey(key);
    }
    /**
     * Use {@link #getFeatureMap()} instead.
     */
    @Override
	@java.lang.Deprecated
    public java.util.Map<java.lang.String, org.tensorflow.example.Feature> getFeature() {
      return getFeatureMap();
    }
    /**
     * <pre>
     * Map from feature name to feature.
     * </pre>
     *
     * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
     */

    @Override
	public java.util.Map<java.lang.String, org.tensorflow.example.Feature> getFeatureMap() {
      return internalGetFeature().getMap();
    }
    /**
     * <pre>
     * Map from feature name to feature.
     * </pre>
     *
     * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
     */

    @Override
	public org.tensorflow.example.Feature getFeatureOrDefault(
        java.lang.String key,
        org.tensorflow.example.Feature defaultValue) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.tensorflow.example.Feature> map =
          internalGetFeature().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <pre>
     * Map from feature name to feature.
     * </pre>
     *
     * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
     */

    @Override
	public org.tensorflow.example.Feature getFeatureOrThrow(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.tensorflow.example.Feature> map =
          internalGetFeature().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
    }

    public Builder clearFeature() {
      getMutableFeature().clear();
      return this;
    }
    /**
     * <pre>
     * Map from feature name to feature.
     * </pre>
     *
     * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
     */

    public Builder removeFeature(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      getMutableFeature().remove(key);
      return this;
    }
    /**
     * Use alternate mutation accessors instead.
     */
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, org.tensorflow.example.Feature>
    getMutableFeature() {
      return internalGetMutableFeature().getMutableMap();
    }
    /**
     * <pre>
     * Map from feature name to feature.
     * </pre>
     *
     * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
     */
    public Builder putFeature(
        java.lang.String key,
        org.tensorflow.example.Feature value) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      if (value == null) { throw new java.lang.NullPointerException(); }
      getMutableFeature().put(key, value);
      return this;
    }
    /**
     * <pre>
     * Map from feature name to feature.
     * </pre>
     *
     * <code>map&lt;string, .tensorflow.Feature&gt; feature = 1;</code>
     */

    public Builder putAllFeature(
        java.util.Map<java.lang.String, org.tensorflow.example.Feature> values) {
      getMutableFeature().putAll(values);
      return this;
    }
    @Override
	public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    @Override
	public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:tensorflow.Features)
  }

  // @@protoc_insertion_point(class_scope:tensorflow.Features)
  private static final org.tensorflow.example.Features DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.tensorflow.example.Features();
  }

  public static org.tensorflow.example.Features getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Features>
      PARSER = new com.google.protobuf.AbstractParser<Features>() {
    @Override
	public Features parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new Features(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<Features> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Features> getParserForType() {
    return PARSER;
  }

  @Override
public org.tensorflow.example.Features getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

