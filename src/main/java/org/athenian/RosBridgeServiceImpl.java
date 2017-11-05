package org.athenian;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.athenian.grpc.RosBridgeServiceGrpc;
import org.athenian.grpc.TwistRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

class RosBridgeServiceImpl
    extends RosBridgeServiceGrpc.RosBridgeServiceImplBase {

  private static final Logger     logger            = LoggerFactory.getLogger(RosBridgeServiceImpl.class);
  private static final AtomicLong PATH_ID_GENERATOR = new AtomicLong(0);

  private final RosBridge rosBridge;

  public RosBridgeServiceImpl(final RosBridge rosBridge) {
    this.rosBridge = rosBridge;
  }

  @Override
  public void reportTwist(TwistRequest request, StreamObserver<Empty> responseObserver) {
    super.reportTwist(request, responseObserver);
  }
}
