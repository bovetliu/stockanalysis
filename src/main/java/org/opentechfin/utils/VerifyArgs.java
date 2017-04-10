package org.opentechfin.utils;

/**
 * Created by boweiliu on 4/8/17.
 */
public class VerifyArgs {

  public static void nonNullOrEmptyStr(String arg) {
    nonNullOrEmptyStr(arg, null);
  }

  public static void nonNullOrEmptyStr(String arg, String message) {
    if (isStrNullOrEmpty(arg)) {
      throw new IllegalArgumentException(!isStrNullOrEmpty(message) ? message : "str arg cannot be null or empty");
    }
  }

  public static boolean isStrNullOrEmpty(CharSequence charSequence) {
    return charSequence == null || charSequence.length() == 0;
  }
}
