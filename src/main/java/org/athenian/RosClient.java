package org.athenian;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.athenian.common.Utils;
import org.athenian.grpc.RosBridgeServiceGrpc.RosBridgeServiceBlockingStub;
import org.athenian.grpc.RosBridgeServiceGrpc.RosBridgeServiceStub;
import org.athenian.grpc.TwistData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Strings.isNullOrEmpty;
import static io.grpc.ClientInterceptors.intercept;
import static org.athenian.grpc.RosBridgeServiceGrpc.newBlockingStub;
import static org.athenian.grpc.RosBridgeServiceGrpc.newStub;

public class RosClient {
  private static final Logger logger = LoggerFactory.getLogger(RosClient.class);

  private final AtomicReference<ManagedChannel>               channelRef      = new AtomicReference<>();
  private final AtomicReference<RosBridgeServiceBlockingStub> blockingStubRef = new AtomicReference<>();
  private final AtomicReference<RosBridgeServiceStub>         asyncStubRef    = new AtomicReference<>();
  private final String hostname;
  private final int    port;

  public RosClient(final RosClientOptions options, final String inProcessServerName) {

    if (options.getHostname().contains(":")) {
      String[] vals = options.getHostname().split(":");
      this.hostname = vals[0];
      this.port = Integer.valueOf(vals[1]);
    }
    else {
      this.hostname = options.getHostname();
      this.port = 50051;
    }

    this.channelRef.set(isNullOrEmpty(inProcessServerName) ? NettyChannelBuilder.forAddress(this.hostname, this.port)
                                                                                .usePlaintext(true)
                                                                                .build()
                                                           : InProcessChannelBuilder.forName(inProcessServerName)
                                                                                    .usePlaintext(true)
                                                                                    .build());
    logger.info("Creating gRPC stubs");
    this.blockingStubRef.set(newBlockingStub(intercept(this.getChannel())));
    this.asyncStubRef.set(newStub(intercept(this.getChannel())));
  }

  public static void main(final String[] argv) {
    final RosClientOptions options = new RosClientOptions(argv);
    final RosClient rosClient = new RosClient(options, null);
    rosClient.writeData();
  }

  public void writeData() {
    final StreamObserver<TwistData> observer = this.getAsyncStub().reportTwistData(
        new StreamObserver<Empty>() {
          @Override
          public void onNext(Empty empty) {
            // Ignore Empty return value
          }

          @Override
          public void onError(Throwable t) {
            final Status s = Status.fromThrowable(t);
            logger.info("Error in writeResponsesToProxyUntilDisconnected(): {} {}", s.getCode(), s.getDescription());
          }

          @Override
          public void onCompleted() {

          }
        });

    for (int i = 0; i < 100; i++) {
      TwistData data = TwistData.newBuilder()
                                .setLinearX(i)
                                .setLinearY(i + 1)
                                .setLinearZ(i + 2)
                                .setAngularX(i + 3)
                                .setAngularY(i + 4)
                                .setAngularZ(i + 5)
                                .build();
      logger.info("Writing data");
      observer.onNext(data);
    }
    Utils.sleepForSecs(10);
    observer.onCompleted();
  }

  private ManagedChannel getChannel() { return this.channelRef.get(); }

  private RosBridgeServiceBlockingStub getBlockingStub() { return this.blockingStubRef.get(); }

  private RosBridgeServiceStub getAsyncStub() { return this.asyncStubRef.get(); }
}
