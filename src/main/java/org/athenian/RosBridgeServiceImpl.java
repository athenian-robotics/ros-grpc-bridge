package org.athenian;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.athenian.grpc.RosBridgeServiceGrpc;
import org.athenian.grpc.TwistData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

class RosBridgeServiceImpl
    extends RosBridgeServiceGrpc.RosBridgeServiceImplBase {

  private static final Logger     logger       = LoggerFactory.getLogger(RosBridgeServiceImpl.class);
  private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

  private final RosBridge rosBridge;

  public RosBridgeServiceImpl(final RosBridge rosBridge) {
    this.rosBridge = rosBridge;
  }

  @Override
  public StreamObserver<TwistData> reportTwistData(StreamObserver<Empty> responseObserver) {
    return new StreamObserver<TwistData>() {
      @Override
      public void onNext(final TwistData twistData) {
        logger.info("Got data {}", twistData);
      }

      @Override
      public void onError(Throwable t) {
        final Status status = Status.fromThrowable(t);
        if (status != Status.CANCELLED)
          logger.info("Error in reportTwist(): {}", status);
        try {
          responseObserver.onNext(Empty.getDefaultInstance());
          responseObserver.onCompleted();
        }
        catch (StatusRuntimeException e) {
          // logger.warn("StatusRuntimeException", e);
          // Ignore
        }
      }

      @Override
      public void onCompleted() {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
      }
    };
  }
}
