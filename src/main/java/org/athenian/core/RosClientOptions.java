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

import com.beust.jcommander.Parameter;
import com.google.common.collect.Iterables;
import org.athenian.RosClient;
import org.athenian.common.BaseOptions;

import java.util.Collections;
import java.util.List;

public class RosClientOptions
    extends BaseOptions {

  @Parameter(names = {"-h", "--hostname"}, description = "Host name")
  private String hostname = "localhost";

  public RosClientOptions(final List<String> args) {
    this(Iterables.toArray(args != null ? args : Collections.emptyList(), String.class));
  }

  public RosClientOptions(final String[] argv) {
    super(RosClient.class.getName(), argv);
  }

  public String getHostname() { return this.hostname; }
}