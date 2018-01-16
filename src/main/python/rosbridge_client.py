import grpc
import logging

from stubs.rosbridge_service_pb2 import RosBridgeServiceStub
from stubs.rosbridge_service_pb2 import TwistData

logger = logging.getLogger(__name__)


class RosBridgeClient(object):
    def __init__(self, hostname):
        self.__hostname = hostname
        self.__channel = grpc.insecure_channel(self.__hostname)
        self.__stub = RosBridgeServiceStub(self.__channel)

    def report_twist(self, twist_data):
        try:
            self.__stub.writeTwistData(twist_data)
        except BaseException as e:
            logger.error("Failed to reach gRPC server at {0} [{1}]".format(self.__hostname, e))

    def stream_twist(self, iter):
        try:
            self.__stub.streamTwistData(iter)
        except BaseException as e:
            logger.error("Failed to reach gRPC server at {0} [{1}]".format(self.__hostname, e))

    @staticmethod
    def newTwistData(lin, ang):
        return TwistData(linear_x=lin,
                         linear_y=0,
                         linear_z=0,
                         angular_x=0,
                         angular_y=0,
                         angular_z=ang)
