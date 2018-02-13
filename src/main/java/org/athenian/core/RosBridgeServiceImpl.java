/*
 * Copyright © 2018 The Athenian School - http://www.athenian.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.athenian.core;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.athenian.RosBridgeClient;
import org.athenian.RosBridgeServer;
import org.athenian.grpc.CommandValue;
import org.athenian.grpc.EncoderValue;
import org.athenian.grpc.RosBridgeServiceGrpc;
import org.athenian.grpc.TwistValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

class RosBridgeServiceImpl
    extends RosBridgeServiceGrpc.RosBridgeServiceImplBase {

  private static final Logger logger = LoggerFactory.getLogger(RosBridgeServiceImpl.class);

  private final RosBridgeServer      rosBridgeServer;
  private final Consumer<TwistValue> onMessageAction;

  public RosBridgeServiceImpl(final RosBridgeServer rosBridgeServer, final Consumer<TwistValue> onMessageAction) {
    this.rosBridgeServer = rosBridgeServer;
    this.onMessageAction = onMessageAction;
  }

  @Override
  public void writeTwistValue(final TwistValue value, final StreamObserver<Empty> observer) {
    this.onMessageAction.accept(value);
    observer.onNext(Empty.getDefaultInstance());
    observer.onCompleted();
  }

  @Override
  public StreamObserver<TwistValue> streamTwistValues(final StreamObserver<Empty> observer) {
    return new StreamObserver<TwistValue>() {
      @Override
      public void onNext(final TwistValue value) {
        onMessageAction.accept(value);
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

  @Override
  public void readEncoderValues(StringValue request, StreamObserver<EncoderValue> observer) {
    logger.info("Returning encoder data for: " + request.getValue());
    for (int i = 0; i < RosBridgeClient.COUNT; i++)
      observer.onNext(EncoderValue.newBuilder().setValue(i).build());
    observer.onCompleted();
  }

  @Override
  public void readCommandValues(Empty request, StreamObserver<CommandValue> observer) {
    logger.info("Returning command");
    for (int i = 0; i < RosBridgeClient.COUNT; i++)
      observer.onNext(CommandValue.newBuilder()
                                  .setCommand(String.format("Command %d", i))
                                  .setCommandArg(String.format("Command Arg %d", i))
                                  .build());
    observer.onCompleted();
  }
}
