package kz.greetgo.cached.zookeeper;

import kz.greetgo.cached.core.file_storage.ParamsFileStorage;
import lombok.NonNull;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

public class ParamsFileStorageZookeeper implements ParamsFileStorage, AutoCloseable {

  private final Builder builder;

  private ParamsFileStorageZookeeper(Builder builder) {
    this.builder = builder;
  }

  public static class Builder {
    private boolean built                = false;
    private String  connectionString;
    private int     sessionTimeoutMs     = 60 * 1000;
    private int     connectionTimeoutMs  = 15 * 1000;
    private int     maxRetries           = 3;
    private int     retryBaseSleepTimeMs = 1000;
    private String  pathBase             = null;

    public Builder pathBase(String pathBase) {
      checkBuilt();
      this.pathBase = pathBase;
      return this;
    }

    public Builder connectionString(String connectionString) {
      checkBuilt();
      this.connectionString = connectionString;
      return this;
    }

    public Builder sessionTimeoutMs(int sessionTimeoutMs) {
      checkBuilt();
      this.sessionTimeoutMs = sessionTimeoutMs;
      return this;
    }

    public Builder connectionTimeoutMs(int connectionTimeoutMs) {
      checkBuilt();
      this.connectionTimeoutMs = connectionTimeoutMs;
      return this;
    }

    public Builder maxRetries(int maxRetries) {
      checkBuilt();
      this.maxRetries = maxRetries;
      return this;
    }

    public Builder retryBaseSleepTimeMs(int retryBaseSleepTimeMs) {
      checkBuilt();
      this.retryBaseSleepTimeMs = retryBaseSleepTimeMs;
      return this;
    }

    public ParamsFileStorageZookeeper build() {
      checkBuilt();
      built = true;
      requireNonNull(connectionString, "connectionString");
      return new ParamsFileStorageZookeeper(this);
    }

    private void checkBuilt() {
      if (built) {
        throw new RuntimeException("3EdWFB5RzX :: Already built");
      }
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final AtomicReference<CuratorFramework> client = new AtomicReference<>(null);

  private final ReentrantLock locker = new ReentrantLock();

  private final AtomicBoolean closed = new AtomicBoolean(false);

  private CuratorFramework client() {
    if (closed.get()) {
      throw new RuntimeException("nRB4WDLu41 :: Already closed");
    }

    {
      CuratorFramework ret = client.get();
      if (ret != null) {
        return ret;
      }
    }

    locker.lock();
    try {
      {
        CuratorFramework ret = client.get();
        if (ret != null) {
          return ret;
        }
      }

      RetryPolicy retryPolicy = new ExponentialBackoffRetry(builder.retryBaseSleepTimeMs, builder.maxRetries);

      CuratorFramework ret = CuratorFrameworkFactory.newClient(builder.connectionString,
                                                               builder.sessionTimeoutMs,
                                                               builder.connectionTimeoutMs,
                                                               retryPolicy);

      ret.start();

      client.set(ret);

      return ret;
    } finally {
      locker.unlock();
    }
  }

  @Override
  public void close() {
    closed.set(true);
    Optional.ofNullable(client.getAndSet(null))
            .ifPresent(CuratorFramework::close);
  }

  private static String slashing(String path) {
    if (path == null) {
      return null;
    }

    String s = path;
    while (s.startsWith("/")) {
      s = s.substring(1);
    }
    while (s.endsWith("/")) {
      s = s.substring(0, s.length() - 1);
    }

    return s;
  }

  private String realPath(String path) {
    var baseDir  = slashing(builder.pathBase);
    var fileName = slashing(path);

    if (baseDir == null && fileName == null) {
      return "/";
    }

    if (baseDir == null) {
      return "/" + fileName;
    }

    if (fileName == null) {
      return "/" + baseDir;
    }

    return "/" + baseDir + "/" + fileName;
  }

  @Override
  public Optional<String> read(@NonNull String path) {
    try {

      byte[] bytes = client().getData().forPath(realPath(path));
      if (bytes == null) {
        return Optional.empty();
      }
      return Optional.of(new String(bytes, UTF_8));

    } catch (KeeperException.NoNodeException e) {
      return Optional.empty();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Date> lastModifiedAt(@NonNull String path) {
    try {

      Stat stat = client().checkExists().forPath(realPath(path));
      if (stat == null) {
        return Optional.empty();
      }
      return Optional.of(new Date(stat.getMtime()));

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Date> write(@NonNull String path, String content) {
    try {

      if (content == null) {
        client().delete().forPath(realPath(path));
        return Optional.empty();
      }

      {
        Stat stat = new Stat();

        byte[] contentBytes = content.getBytes(UTF_8);
        client().create().orSetData()
                .storingStatIn(stat)
                .creatingParentsIfNeeded()
                .forPath(realPath(path), contentBytes);

        return Optional.of(new Date(stat.getMtime()));
      }

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
