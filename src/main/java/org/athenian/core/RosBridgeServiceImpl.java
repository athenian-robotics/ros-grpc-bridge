package org.athenian.core;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.athenian.RosBridge;
import org.athenian.grpc.RosBridgeServiceGrpc;
import org.athenian.grpc.TwistData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

class RosBridgeServiceImpl
    extends RosBridgeServiceGrpc.RosBridgeServiceImplBase {

  private static final Logger logger = LoggerFactory.getLogger(RosBridgeServiceImpl.class);

  private final RosBridge           rosBridge;
  private final Consumer<TwistData> action;

  public RosBridgeServiceImpl(final RosBridge rosBridge, final Consumer<TwistData> action) {
    this.rosBridge = rosBridge;
    this.action = action;
  }

  @Override
  public void writeTwistData(final TwistData data, final StreamObserver<Empty> observer) {
    this.action.accept(data);
    observer.onNext(Empty.getDefaultInstance());
    observer.onCompleted();
  }

  @Override
  public StreamObserver<TwistData> streamTwistData(final StreamObserver<Empty> observer) {
    return new StreamObserver<TwistData>() {
      @Override
      public void onNext(final TwistData data) {
        action.accept(data);
      }

      @Override
      public void onError(Throwable t) {
        final Status status = Status.fromThrowable(t);
        if (status != Status.CANCELLED)
          logger.info("Error in streamTwistData(): {}", status);
        try {
          observer.onNext(Empty.getDefaultInstance());
          observer.onCompleted();
        }
        catch (StatusRuntimeException e) {
          // logger.warn("StatusRuntimeException", e);
          // Ignore
        }
      }

      @Override
      public void onCompleted() {
        observer.onNext(Empty.getDefaultInstance());
        observer.onCompleted();
      }
    };
  }
}
