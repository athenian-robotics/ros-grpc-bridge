import grpc
import logging
import time

from stubs.rosbridge_service_pb2 import RosBridgeServiceStub
from stubs.rosbridge_service_pb2 import TwistData

logger = logging.getLogger(__name__)


class RosBridgeClient(object):
    def __init__(self, hostname):
        self.hostname = hostname
        self.channel = grpc.insecure_channel(self.hostname)
        self.stub = RosBridgeServiceStub(self.channel)

    def stream_twist(self, iter):
        try:
            server_info = self.stub.streamTwistData(iter)
        except BaseException as e:
            logger.error("Failed to reach gRPC server at {0} [{1}]".format(self.hostname, e))
            time.sleep(1)

    def report_twist(self, twist_data):
        try:
            server_info = self.stub.reportTwistData(twist_data)
        except BaseException as e:
            logger.error("Failed to reach gRPC server at {0} [{1}]".format(self.hostname, e))
            time.sleep(1)


if __name__ == "__main__":

    bridge = RosBridgeClient("localhost:50051")

    count = 10000

    def twit_gen(cnt):
        for i in range(cnt):
            twist_data = TwistData(linear_x=i,
                                   linear_y=i + 1,
                                   linear_z=i + 2,
                                   angular_x=i + 3,
                                   angular_y=i + 4,
                                   angular_z=i + 5)
            # print("Streaming data")
            yield twist_data


    start1 = time.time()
    bridge.stream_twist(twit_gen(count))
    end1 = time.time()
    print("Stream elapsed: " + str(end1 - start1))

    start2 = time.time()
    for i in range(count):
        twist_data = TwistData(linear_x=i,
                               linear_y=i + 1,
                               linear_z=i + 2,
                               angular_x=i + 3,
                               angular_y=i + 4,
                               angular_z=i + 5)
        #print("Sending data")
        bridge.report_twist(twist_data)
    end2 = time.time()
    print("Nonstream elapsed: " + str(end2 - start2))

    time.sleep(10)
