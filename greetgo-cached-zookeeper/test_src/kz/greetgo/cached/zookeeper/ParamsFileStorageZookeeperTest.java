package kz.greetgo.cached.zookeeper;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ParamsFileStorageZookeeperTest {

  ParamsFileStorageZookeeper fs;

  @BeforeClass
  public void createFs() {
    fs = ParamsFileStorageZookeeper.builder()
                                   .connectionString("localhost:17018")
                                   .pathBase("ParamsFileStorageZookeeperTest/wow")
                                   .sessionTimeoutMs(60 * 1000)
                                   .connectionTimeoutMs(15 * 1000)
                                   .maxRetries(3)
                                   .retryBaseSleepTimeMs(1000)
                                   .build();
  }

  @AfterClass
  public void closeFs() {
    fs.close();
  }

  @Test
  public void write() {
    var lastModifiedAt = fs.write("Test-file-1", "Test file 1 content");
    System.out.println("ZD4JV4hsQu :: lastModifiedAt = " + lastModifiedAt);
  }

  @Test
  public void read() {
    var content = fs.read("Test-file-1");
    System.out.println("863DCJ1pJq :: content = " + content);
  }

}
