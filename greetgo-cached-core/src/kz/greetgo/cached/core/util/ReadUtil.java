package kz.greetgo.cached.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReadUtil {
  public static int readInt(String name, Map<String, Object> cacheParams) {
    var objectValue = cacheParams.get(name);
    if (objectValue == null) {
      throw new RuntimeException("9x0Rwo1tWM :: No parameter " + name);
    }

    if (objectValue instanceof Integer) {
      return (Integer) objectValue;
    }

    throw new RuntimeException("x7YDJqxCpV :: Illegal parameter type (must be Integer)."
                                 + " Parameter `" + name + "`"
                                 + ", type `" + objectValue.getClass().getSimpleName() + "`"
                                 + ", value `" + objectValue + "`");
  }

  public static long readLong(String name, Map<String, Object> cacheParams) {
    var objectValue = cacheParams.get(name);
    if (objectValue == null) {
      throw new RuntimeException("jtaUw3G6T9 :: No parameter " + name);
    }

    if (objectValue instanceof Long) {
      return (Long) objectValue;
    }

    throw new RuntimeException("7eW4QI74gu :: Illegal parameter type (must be Long)."
                                 + " Parameter `" + name + "`"
                                 + ", type `" + objectValue.getClass().getSimpleName() + "`"
                                 + ", value `" + objectValue + "`");
  }

  public static List<String> toLineList(String description) {
    if (description == null) {
      return List.of();
    }
    List<String> ret = new ArrayList<>();
    Collections.addAll(ret, description.split("\n"));
    return ret;
  }
}
