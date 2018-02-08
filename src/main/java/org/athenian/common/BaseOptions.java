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

package org.athenian.common;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;

public abstract class BaseOptions {

  private static final Logger   logger     = LoggerFactory.getLogger(BaseOptions.class);
  private static final String[] EMPTY_ARGV = {};

  private final AtomicReference<Config> configRef = new AtomicReference<>();

  private final String   programName;
  private final String[] argv;

  @Parameter(names = {"-c", "--conf", "--config"}, description = "Configuration file or pingUrl")
  private String              configName    = null;
  @Parameter(names = {"-u", "--usage"}, help = true)
  private boolean             usage         = false;
  @DynamicParameter(names = "-D", description = "Dynamic property assignment")
  private Map<String, String> dynamicParams = new HashMap<>();

  protected BaseOptions(final String programName, final String[] argv) {
    this.programName = programName;
    this.argv = argv;
  }

  public BaseOptions init() {
    this.parseArgs(this.argv);
    this.readParams();
    return this;
  }

  private void parseArgs(final String[] argv) {
    try {
      final JCommander jcom = new JCommander(this);
      jcom.setProgramName(this.programName);
      jcom.setCaseSensitiveOptions(false);
      jcom.parse(argv == null ? EMPTY_ARGV : argv);

      if (this.usage) {
        jcom.usage();
        System.exit(0);
      }
    }
    catch (ParameterException e) {
      logger.error(e.getMessage(), e);
      System.exit(1);
    }
  }

  private void readParams() {
    this.dynamicParams
        .forEach(
            (key, value) -> {
              // Strip quotes
              final String prop = format("%s=%s", key, value.startsWith("\"") && value.endsWith("\"")
                                                       ? value.substring(1, value.length() - 1)
                                                       : value);
              System.setProperty(key, prop);
            });
  }
}
