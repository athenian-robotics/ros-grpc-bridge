# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: rosbridge_service.proto

import sys

_b = sys.version_info[0] < 3 and (lambda x: x) or (lambda x: x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
from google.protobuf import descriptor_pb2
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from google.protobuf import empty_pb2 as google_dot_protobuf_dot_empty__pb2

from google.protobuf.empty_pb2 import *

DESCRIPTOR = _descriptor.FileDescriptor(
    name='rosbridge_service.proto',
    package='rosbridge_service',
    syntax='proto3',
    serialized_pb=_b(
        '\n\x17rosbridge_service.proto\x12\x11rosbridge_service\x1a\x1bgoogle/protobuf/empty.proto\"z\n\tTwistData\x12\x10\n\x08linear_x\x18\x02 \x01(\x01\x12\x10\n\x08linear_y\x18\x03 \x01(\x01\x12\x10\n\x08linear_z\x18\x04 \x01(\x01\x12\x11\n\tangular_x\x18\x05 \x01(\x01\x12\x11\n\tangular_y\x18\x06 \x01(\x01\x12\x11\n\tangular_z\x18\x07 \x01(\x01\x32\xa9\x01\n\x10RosBridgeService\x12H\n\x0ewriteTwistData\x12\x1c.rosbridge_service.TwistData\x1a\x16.google.protobuf.Empty\"\x00\x12K\n\x0fstreamTwistData\x12\x1c.rosbridge_service.TwistData\x1a\x16.google.protobuf.Empty\"\x00(\x01\x42\x15\n\x11org.athenian.grpcP\x01P\x00\x62\x06proto3')
    ,
    dependencies=[google_dot_protobuf_dot_empty__pb2.DESCRIPTOR, ],
    public_dependencies=[google_dot_protobuf_dot_empty__pb2.DESCRIPTOR, ])
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

_TWISTDATA = _descriptor.Descriptor(
    name='TwistData',
    full_name='rosbridge_service.TwistData',
    filename=None,
    file=DESCRIPTOR,
    containing_type=None,
    fields=[
        _descriptor.FieldDescriptor(
            name='linear_x', full_name='rosbridge_service.TwistData.linear_x', index=0,
            number=2, type=1, cpp_type=5, label=1,
            has_default_value=False, default_value=float(0),
            message_type=None, enum_type=None, containing_type=None,
            is_extension=False, extension_scope=None,
            options=None),
        _descriptor.FieldDescriptor(
            name='linear_y', full_name='rosbridge_service.TwistData.linear_y', index=1,
            number=3, type=1, cpp_type=5, label=1,
            has_default_value=False, default_value=float(0),
            message_type=None, enum_type=None, containing_type=None,
            is_extension=False, extension_scope=None,
            options=None),
        _descriptor.FieldDescriptor(
            name='linear_z', full_name='rosbridge_service.TwistData.linear_z', index=2,
            number=4, type=1, cpp_type=5, label=1,
            has_default_value=False, default_value=float(0),
            message_type=None, enum_type=None, containing_type=None,
            is_extension=False, extension_scope=None,
            options=None),
        _descriptor.FieldDescriptor(
            name='angular_x', full_name='rosbridge_service.TwistData.angular_x', index=3,
            number=5, type=1, cpp_type=5, label=1,
            has_default_value=False, default_value=float(0),
            message_type=None, enum_type=None, containing_type=None,
            is_extension=False, extension_scope=None,
            options=None),
        _descriptor.FieldDescriptor(
            name='angular_y', full_name='rosbridge_service.TwistData.angular_y', index=4,
            number=6, type=1, cpp_type=5, label=1,
            has_default_value=False, default_value=float(0),
            message_type=None, enum_type=None, containing_type=None,
            is_extension=False, extension_scope=None,
            options=None),
        _descriptor.FieldDescriptor(
            name='angular_z', full_name='rosbridge_service.TwistData.angular_z', index=5,
            number=7, type=1, cpp_type=5, label=1,
            has_default_value=False, default_value=float(0),
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
    serialized_start=75,
    serialized_end=197,
)

DESCRIPTOR.message_types_by_name['TwistData'] = _TWISTDATA

TwistData = _reflection.GeneratedProtocolMessageType('TwistData', (_message.Message,), dict(
    DESCRIPTOR=_TWISTDATA,
    __module__='rosbridge_service_pb2'
    # @@protoc_insertion_point(class_scope:rosbridge_service.TwistData)
))
_sym_db.RegisterMessage(TwistData)


DESCRIPTOR.has_options = True
DESCRIPTOR._options = _descriptor._ParseOptions(descriptor_pb2.FileOptions(), _b('\n\021org.athenian.grpcP\001'))
try:
    # THESE ELEMENTS WILL BE DEPRECATED.
    # Please use the generated *_pb2_grpc.py files instead.
    import grpc
    from grpc.framework.common import cardinality
    from grpc.framework.interfaces.face import utilities as face_utilities
    from grpc.beta import implementations as beta_implementations
    from grpc.beta import interfaces as beta_interfaces


    class RosBridgeServiceStub(object):

        def __init__(self, channel):
            """Constructor.

            Args:
              channel: A grpc.Channel.
            """
            self.writeTwistData = channel.unary_unary(
                '/rosbridge_service.RosBridgeService/writeTwistData',
                request_serializer=TwistData.SerializeToString,
                response_deserializer=google_dot_protobuf_dot_empty__pb2.Empty.FromString,
            )
            self.streamTwistData = channel.stream_unary(
                '/rosbridge_service.RosBridgeService/streamTwistData',
                request_serializer=TwistData.SerializeToString,
                response_deserializer=google_dot_protobuf_dot_empty__pb2.Empty.FromString,
            )


    class RosBridgeServiceServicer(object):

        def writeTwistData(self, request, context):
            context.set_code(grpc.StatusCode.UNIMPLEMENTED)
            context.set_details('Method not implemented!')
            raise NotImplementedError('Method not implemented!')

        def streamTwistData(self, request_iterator, context):
            context.set_code(grpc.StatusCode.UNIMPLEMENTED)
            context.set_details('Method not implemented!')
            raise NotImplementedError('Method not implemented!')


    def add_RosBridgeServiceServicer_to_server(servicer, server):
        rpc_method_handlers = {
            'writeTwistData': grpc.unary_unary_rpc_method_handler(
                servicer.writeTwistData,
                request_deserializer=TwistData.FromString,
                response_serializer=google_dot_protobuf_dot_empty__pb2.Empty.SerializeToString,
            ),
            'streamTwistData': grpc.stream_unary_rpc_method_handler(
                servicer.streamTwistData,
                request_deserializer=TwistData.FromString,
                response_serializer=google_dot_protobuf_dot_empty__pb2.Empty.SerializeToString,
            ),
        }
        generic_handler = grpc.method_handlers_generic_handler(
            'rosbridge_service.RosBridgeService', rpc_method_handlers)
        server.add_generic_rpc_handlers((generic_handler,))


    class BetaRosBridgeServiceServicer(object):
        """The Beta API is deprecated for 0.15.0 and later.

        It is recommended to use the GA API (classes and functions in this
        file not marked beta) for all further purposes. This class was generated
        only to ease transition from grpcio<0.15.0 to grpcio>=0.15.0."""

        def writeTwistData(self, request, context):
            context.code(beta_interfaces.StatusCode.UNIMPLEMENTED)

        def streamTwistData(self, request_iterator, context):
            context.code(beta_interfaces.StatusCode.UNIMPLEMENTED)


    class BetaRosBridgeServiceStub(object):
        """The Beta API is deprecated for 0.15.0 and later.

        It is recommended to use the GA API (classes and functions in this
        file not marked beta) for all further purposes. This class was generated
        only to ease transition from grpcio<0.15.0 to grpcio>=0.15.0."""

        def writeTwistData(self, request, timeout, metadata=None, with_call=False, protocol_options=None):
            raise NotImplementedError()

        writeTwistData.future = None

        def streamTwistData(self, request_iterator, timeout, metadata=None, with_call=False, protocol_options=None):
            raise NotImplementedError()

        streamTwistData.future = None


    def beta_create_RosBridgeService_server(servicer, pool=None, pool_size=None, default_timeout=None,
                                            maximum_timeout=None):
        """The Beta API is deprecated for 0.15.0 and later.

        It is recommended to use the GA API (classes and functions in this
        file not marked beta) for all further purposes. This function was
        generated only to ease transition from grpcio<0.15.0 to grpcio>=0.15.0"""
        request_deserializers = {
            ('rosbridge_service.RosBridgeService', 'streamTwistData'): TwistData.FromString,
            ('rosbridge_service.RosBridgeService', 'writeTwistData'): TwistData.FromString,
        }
        response_serializers = {
            ('rosbridge_service.RosBridgeService',
             'streamTwistData'): google_dot_protobuf_dot_empty__pb2.Empty.SerializeToString,
            ('rosbridge_service.RosBridgeService',
             'writeTwistData'): google_dot_protobuf_dot_empty__pb2.Empty.SerializeToString,
        }
        method_implementations = {
            ('rosbridge_service.RosBridgeService', 'streamTwistData'): face_utilities.stream_unary_inline(
                servicer.streamTwistData),
            ('rosbridge_service.RosBridgeService', 'writeTwistData'): face_utilities.unary_unary_inline(
                servicer.writeTwistData),
        }
        server_options = beta_implementations.server_options(request_deserializers=request_deserializers,
                                                             response_serializers=response_serializers,
                                                             thread_pool=pool, thread_pool_size=pool_size,
                                                             default_timeout=default_timeout,
                                                             maximum_timeout=maximum_timeout)
        return beta_implementations.server(method_implementations, options=server_options)


    def beta_create_RosBridgeService_stub(channel, host=None, metadata_transformer=None, pool=None, pool_size=None):
        """The Beta API is deprecated for 0.15.0 and later.

        It is recommended to use the GA API (classes and functions in this
        file not marked beta) for all further purposes. This function was
        generated only to ease transition from grpcio<0.15.0 to grpcio>=0.15.0"""
        request_serializers = {
            ('rosbridge_service.RosBridgeService', 'streamTwistData'): TwistData.SerializeToString,
            ('rosbridge_service.RosBridgeService', 'writeTwistData'): TwistData.SerializeToString,
        }
        response_deserializers = {
            ('rosbridge_service.RosBridgeService',
             'streamTwistData'): google_dot_protobuf_dot_empty__pb2.Empty.FromString,
            ('rosbridge_service.RosBridgeService',
             'writeTwistData'): google_dot_protobuf_dot_empty__pb2.Empty.FromString,
        }
        cardinalities = {
            'streamTwistData': cardinality.Cardinality.STREAM_UNARY,
            'writeTwistData': cardinality.Cardinality.UNARY_UNARY,
        }
        stub_options = beta_implementations.stub_options(host=host, metadata_transformer=metadata_transformer,
                                                         request_serializers=request_serializers,
                                                         response_deserializers=response_deserializers,
                                                         thread_pool=pool, thread_pool_size=pool_size)
        return beta_implementations.dynamic_stub(channel, 'rosbridge_service.RosBridgeService', cardinalities,
                                                 options=stub_options)
except ImportError:
    pass
# @@protoc_insertion_point(module_scope)
