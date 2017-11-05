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

  private final String programName;

  @Parameter(names = {"-c", "--conf", "--config"}, description = "Configuration file or pingUrl")
  private String              configName    = null;
  @Parameter(names = {"-u", "--usage"}, help = true)
  private boolean             usage         = false;
  @DynamicParameter(names = "-D", description = "Dynamic property assignment")
  private Map<String, String> dynamicParams = new HashMap<>();

  protected BaseOptions(final String programName, final String[] argv) {
    this.programName = programName;
    this.parseArgs(argv);
    this.readParams();
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
    this.dynamicParams.forEach(
        (key, value) -> {
          // Strip quotes
          final String prop = format("%s=%s", key, value.startsWith("\"") && value.endsWith("\"")
                                                   ? value.substring(1, value.length() - 1)
                                                   : value);
          System.setProperty(key, prop);
        });
  }

  public Map<String, String> getDynamicParams() { return this.dynamicParams; }
}
