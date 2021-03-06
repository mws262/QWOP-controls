# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: example.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
from google.protobuf import descriptor_pb2
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()

from protobuf import feature_pb2 as feature__pb2

DESCRIPTOR = _descriptor.FileDescriptor(
  name='example.proto',
  package='tensorflow',
  syntax='proto3',
  serialized_pb=_b('\n\rexample.proto\x12\ntensorflow\x1a\rfeature.proto\"1\n\x07\x45xample\x12&\n\x08\x66\x65\x61tures\x18\x01 \x01(\x0b\x32\x14.tensorflow.Features\"i\n\x0fSequenceExample\x12%\n\x07\x63ontext\x18\x01 \x01(\x0b\x32\x14.tensorflow.Features\x12/\n\rfeature_lists\x18\x02 \x01(\x0b\x32\x18.tensorflow.FeatureListsB\x1a\n\x04\x64\x61taB\rExampleProtosP\x01\xf8\x01\x01\x62\x06proto3')
  ,
  dependencies=[feature__pb2.DESCRIPTOR,])
_sym_db.RegisterFileDescriptor(DESCRIPTOR)




_EXAMPLE = _descriptor.Descriptor(
  name='Example',
  full_name='tensorflow.Example',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='features', full_name='tensorflow.Example.features', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=44,
  serialized_end=93,
)


_SEQUENCEEXAMPLE = _descriptor.Descriptor(
  name='SequenceExample',
  full_name='tensorflow.SequenceExample',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='context', full_name='tensorflow.SequenceExample.context', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='feature_lists', full_name='tensorflow.SequenceExample.feature_lists', index=1,
      number=2, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=95,
  serialized_end=200,
)

_EXAMPLE.fields_by_name['features'].message_type = feature__pb2._FEATURES
_SEQUENCEEXAMPLE.fields_by_name['context'].message_type = feature__pb2._FEATURES
_SEQUENCEEXAMPLE.fields_by_name['feature_lists'].message_type = feature__pb2._FEATURELISTS
DESCRIPTOR.message_types_by_name['Example'] = _EXAMPLE
DESCRIPTOR.message_types_by_name['SequenceExample'] = _SEQUENCEEXAMPLE

Example = _reflection.GeneratedProtocolMessageType('Example', (_message.Message,), dict(
  DESCRIPTOR = _EXAMPLE,
  __module__ = 'example_pb2'
  # @@protoc_insertion_point(class_scope:tensorflow.Example)
  ))
_sym_db.RegisterMessage(Example)

SequenceExample = _reflection.GeneratedProtocolMessageType('SequenceExample', (_message.Message,), dict(
  DESCRIPTOR = _SEQUENCEEXAMPLE,
  __module__ = 'example_pb2'
  # @@protoc_insertion_point(class_scope:tensorflow.SequenceExample)
  ))
_sym_db.RegisterMessage(SequenceExample)


DESCRIPTOR.has_options = True
DESCRIPTOR._options = _descriptor._ParseOptions(descriptor_pb2.FileOptions(), _b('\n\004dataB\rExampleProtosP\001\370\001\001'))
# @@protoc_insertion_point(module_scope)
