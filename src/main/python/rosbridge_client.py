import grpc
import logging
from google.protobuf.wrappers_pb2 import *

from stubs.rosbridge_service_pb2 import RosBridgeServiceStub
from stubs.rosbridge_service_pb2 import TwistValue

logger = logging.getLogger(__name__)


class RosBridgeClient(object):
    def __init__(self,
                 hostname,
                 log_info=logger.info,
                 log_debug=logger.debug,
                 log_error=logger.error):
        self.__hostname = hostname
        self.__channel = grpc.insecure_channel(self.__hostname)
        self.__stub = RosBridgeServiceStub(self.__channel)
        self.__log_info = log_info
        self.__log_debug = log_debug
        self.__log_error = log_error

    def write_twist(self, twist_data):
        try:
            self.__stub.writeTwistValue(twist_data)
        except BaseException as e:
            self.__log_error("Failed to reach gRPC server at {0} [{1}]".format(self.__hostname, e))
            raise e

    def stream_twist(self, iter_val):
        try:
            self.__stub.streamTwistValues(iter_val)
        except BaseException as e:
            self.__log_error("Failed to reach gRPC server at {0} [{1}]".format(self.__hostname, e))
            raise e

    def read_encoder(self, encoder_name):
        try:
            return self.__stub.readEncoderValues(StringValue(name=encoder_name))
        except BaseException as e:
            self.__log_error("Failed to reach gRPC server at {0} [{1}]".format(self.__hostname, e))
            raise e

    @staticmethod
    def newTwistData(lin, ang):
        return TwistValue(linear_x=lin,
                          linear_y=0,
                          linear_z=0,
                          angular_x=0,
                          angular_y=0,
                          angular_z=ang)
