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
  private final Consumer<TwistData> onMessageAction;

  public RosBridgeServiceImpl(final RosBridge rosBridge, final Consumer<TwistData> onMessageAction) {
    this.rosBridge = rosBridge;
    this.onMessageAction = onMessageAction;
  }

  @Override
  public void writeTwistData(final TwistData data, final StreamObserver<Empty> observer) {
    this.onMessageAction.accept(data);
    observer.onNext(Empty.getDefaultInstance());
    observer.onCompleted();
  }

  @Override
  public StreamObserver<TwistData> streamTwistData(final StreamObserver<Empty> observer) {
    return new StreamObserver<TwistData>() {
      @Override
      public void onNext(final TwistData data) {
        onMessageAction.accept(data);
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
