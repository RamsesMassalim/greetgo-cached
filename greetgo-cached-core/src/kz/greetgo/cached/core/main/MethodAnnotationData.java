package kz.greetgo.cached.core.main;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@EqualsAndHashCode
@RequiredArgsConstructor
public class MethodAnnotationData {
  public final Long                maximumSize;
  public final Long                lifeTimeSec;
  public final String              cacheEngineName;
  public final Map<String, Object> params;
  public final Set<String>         groups;

  public long maximumSizeOr(long defaultMaximumSize) {
    return maximumSize != null ? maximumSize : defaultMaximumSize;
  }

  public long lifeTimeSecOr(long defaultLifeTimeSec) {
    return lifeTimeSec != null ? lifeTimeSec : defaultLifeTimeSec;
  }
}
