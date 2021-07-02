package kz.greetgo.cached.core.main;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@EqualsAndHashCode
@RequiredArgsConstructor
public class MethodAnnotationData {
  public final Long                maximumSize;
  public final Long                lifeTimeSec;
  public final String              cacheEngineName;
  public final Map<String, Object> params;

  public long maximumSizeOr(long defaultMaximumSize) {
    return maximumSize != null ? maximumSize : defaultMaximumSize;
  }

  public long lifeTimeSecOr(long defaultLifeTimeSec) {
    return lifeTimeSec != null ? lifeTimeSec : defaultLifeTimeSec;
  }
}
