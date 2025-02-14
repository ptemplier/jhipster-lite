package tech.jhipster.lite.generator.server.springboot.cache.common.application;

import static tech.jhipster.lite.TestUtils.assertFileContent;
import static tech.jhipster.lite.TestUtils.assertFileExist;
import static tech.jhipster.lite.common.domain.FileUtils.getPath;
import static tech.jhipster.lite.generator.project.domain.Constants.MAIN_JAVA;

import java.util.List;
import tech.jhipster.lite.generator.project.domain.DefaultConfig;
import tech.jhipster.lite.generator.project.domain.Project;
import tech.jhipster.lite.generator.server.springboot.cache.common.domain.SpringBootCacheDomainService;

public class SpringBootCacheAssert {

  public static void assertDependencies(Project project) {
    assertFileContent(
      project,
      "pom.xml",
      List.of(
        "<dependency>",
        "<groupId>org.springframework.boot</groupId>",
        "<artifactId>spring-boot-starter-cache</artifactId>",
        "</dependency>"
      )
    );
  }

  public static void assertEnableCaching(Project project) {
    String basePackage = project.getPackageName().orElse("com.mycompany.myapp");
    String cachePackage = basePackage + ".technical.infrastructure.secondary.cache";

    String basePath = project.getPackageNamePath().orElse(getPath(DefaultConfig.PACKAGE_PATH));
    String cachePath = getPath(MAIN_JAVA, basePath, SpringBootCacheDomainService.DESTINATION);

    assertFileExist(project, getPath(cachePath, "CacheConfiguration.java"));

    assertFileContent(project, getPath(cachePath, "CacheConfiguration.java"), "package " + cachePackage);
  }
}
