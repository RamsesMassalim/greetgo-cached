package kz.greetgo.cached.core.util;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ErrUtil {
  public static List<String> extractErrors(Throwable throwable) {
    if (throwable == null) {
      return null;
    }
    List<String> ret = new ArrayList<>();
    Throwable    e   = throwable;
    while (e != null) {
      ret.add(e.getMessage());
      e = e.getCause();
    }
    return ret;
  }

  public static String extractErrorsDetails(Throwable throwable) {
    if (throwable == null) {
      return null;
    }

    var writer = new CharArrayWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  public static String extractStackTrace(Throwable e) {
    StringWriter errorContent = new StringWriter();
    try (var printWriter = new PrintWriter(errorContent)) {
      e.printStackTrace(printWriter);
    }
    return errorContent.toString();
  }
}
