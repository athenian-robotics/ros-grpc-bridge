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
    this.observer =
        asyncStub.streamTwistData(
            new StreamObserver<Empty>() {
              @Override
              public void onNext(Empty empty) {
                // Ignore Empty return value
                logger.info("Empty value returned by server");
              }

              @Override
              public void onError(Throwable t) {
                final Status s = Status.fromThrowable(t);
                logger.info("Error in TwistDataStream: {} {}", s.getCode(), s.getDescription());
              }

              @Override
              public void onCompleted() {
                logger.info("Server acknowledged completion of call");
              }
            });
  }

  public void writeTwistData(TwistData data) {
    logger.info("Writing twist data to server");
    this.observer.onNext(data);
  }

  // Signal to the server that we are done streaming data
  @Override
  public void close() {
    logger.info("Completed writing twist data to server");
    this.observer.onCompleted();
  }
}
