package org.athenian.core;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.athenian.grpc.RosBridgeServiceGrpc;
import org.athenian.grpc.TwistData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class TwistDataStream
    implements Closeable {
  private static final Logger logger = LoggerFactory.getLogger(TwistDataStream.class);

  private final StreamObserver<TwistData> observer;

  public TwistDataStream(RosBridgeServiceGrpc.RosBridgeServiceStub asyncStub) {
    this.observer = asyncStub.streamTwistData(
        new StreamObserver<Empty>() {
          @Override
          public void onNext(Empty empty) {
            // Ignore Empty return value
          }

          @Override
          public void onError(Throwable t) {
            final Status s = Status.fromThrowable(t);
            logger.info("Error in TwistDataStream: {} {}", s.getCode(), s.getDescription());
          }

          @Override
          public void onCompleted() {

          }
        });
  }

  public void writeData(TwistData data) {
    logger.info("Writing data");
    this.observer.onNext(data);
  }

  @Override
  public void close() {
    this.observer.onCompleted();
  }
}
