package personal.boweiliu.utils;

import java.util.Objects;

/**
 * Created by boweiliu on 4/8/17.
 */
public class VerifyArgs {

  public static void nonNullOrEmptyStrArg(String arg) {
    nonNullOrEmptyStrArg(arg, null);
  }

  public static void nonNullOrEmptyStrArg(String arg, String message) {
    if (isStrNullOrEmpty(arg)) {
      throw new IllegalArgumentException(!isStrNullOrEmpty(message) ? message : "str arg cannot be null or empty");
    }
  }

  public static boolean isStrNullOrEmpty(CharSequence charSequence) {
    return charSequence == null || charSequence.length() == 0;
  }
}
