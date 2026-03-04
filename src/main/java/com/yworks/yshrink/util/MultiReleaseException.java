package com.yworks.yshrink.util;

public class MultiReleaseException extends RuntimeException {
  @Override
  public String getMessage() {
    return "Multi-release archives containing classes in META-INF are incompatible with yGuard.";
  }
}
