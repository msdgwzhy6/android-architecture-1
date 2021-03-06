package com.thepacific.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.quote;

public final class MapString {

  private MapString() {
    throw new AssertionError("No instances");
  }

  public static Builder format(String string) {
    return new Builder(string);
  }

  public static Builder format(String string, String prefix, String suffix) {
    return new Builder(string, prefix, suffix);
  }

  public static class Builder {

    private final Pattern pattern;

    private String baseString;
    private String prefix;
    private String suffix;
    private boolean strictMode = true;

    private Builder(String string) {
      this(string, "{", "}");
    }

    private Builder(String string, String prefix, String suffix) {
      baseString = string;
      this.prefix = prefix;
      this.suffix = suffix;
      pattern = Pattern.compile(quote(prefix) + ".*?" + quote(suffix));
    }

    /**
     * If you set the strict mode to false, the builder won't throw any exception
     * if a key is not found or if a key is still present in the final string.
     */
    public Builder strictMode(boolean active) {
      this.strictMode = active;
      return this;
    }

    /**
     * @param key The key, without the '{}'.
     * @param value The value to put for that key.
     * @return The builder for DSL.
     */
    public Builder with(String key, Object value) {
      if (value == null) {
        value = "";
      }
      if (strictMode && !baseString.contains(prefix + key + suffix)) {
        throw new RuntimeException(Joiner.on("Couldn't find key \"")
            .join(key, "\" in string \"", baseString, "\"."));
      }
      baseString = baseString.replace(prefix + key + suffix, value.toString());
      return this;
    }

    /**
     * Create the final string.
     */
    public String build() {
      final Matcher matcher = pattern.matcher(baseString);
      if (strictMode && matcher.find()) {
        throw new RuntimeException("You didn't pass an arg for key " + matcher.group());
      } else {
        return baseString;
      }
    }
  }
}
