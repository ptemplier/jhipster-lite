package tech.jhipster.lite.generator.server.springboot.logstash.tcp.domain;

import static tech.jhipster.lite.common.domain.FileUtils.getPath;
import static tech.jhipster.lite.generator.project.domain.Constants.MAIN_JAVA;
import static tech.jhipster.lite.generator.project.domain.Constants.TEST_JAVA;
import static tech.jhipster.lite.generator.project.domain.DefaultConfig.BASE_NAME;
import static tech.jhipster.lite.generator.project.domain.DefaultConfig.PACKAGE_NAME;

import java.util.Map;
import java.util.TreeMap;
import tech.jhipster.lite.generator.buildtool.generic.domain.BuildToolService;
import tech.jhipster.lite.generator.project.domain.Project;
import tech.jhipster.lite.generator.project.domain.ProjectRepository;
import tech.jhipster.lite.generator.server.springboot.properties.domain.SpringBootPropertiesService;

public class LogstashDomainService implements LogstashService {

  public static final String SOURCE = "server/springboot/logging/logstash/tcp";
  public static final String DESTINATION = "technical/infrastructure/secondary/logstash";

  private final BuildToolService buildToolService;
  private final ProjectRepository projectRepository;
  private final SpringBootPropertiesService springBootPropertiesService;

  public LogstashDomainService(
    BuildToolService buildToolService,
    ProjectRepository projectRepository,
    SpringBootPropertiesService springBootPropertiesService
  ) {
    this.buildToolService = buildToolService;
    this.projectRepository = projectRepository;
    this.springBootPropertiesService = springBootPropertiesService;
  }

  @Override
  public void init(Project project) {
    addDependencies(project);
    addJavaFiles(project);
    addProperties(project);
  }

  @Override
  public void addDependencies(Project project) {
    buildToolService.addProperty(project, "logstash-logback-encoder", Logstash.getLogstashLogbackEncoderVersion());
    buildToolService.addDependency(project, Logstash.logstashLogbackEncoderDependency());
  }

  @Override
  public void addJavaFiles(Project project) {
    project.addDefaultConfig(PACKAGE_NAME);
    project.addDefaultConfig(BASE_NAME);
    String packageNamePath = project.getPackageNamePath().orElse(getPath("com/mycompany/myapp"));

    templateToLogstash(project, packageNamePath, "src", "LogstashTcpConfiguration.java", MAIN_JAVA);
    templateToLogstash(project, packageNamePath, "src", "LogstashTcpLifeCycle.java", MAIN_JAVA);
    templateToLogstash(project, packageNamePath, "src", "LogstashTcpProperties.java", MAIN_JAVA);

    templateToLogstash(project, packageNamePath, "test", "LogstashTcpConfigurationIT.java", TEST_JAVA);
    templateToLogstash(project, packageNamePath, "test", "LogstashTcpConfigurationTest.java", TEST_JAVA);
    templateToLogstash(project, packageNamePath, "test", "LogstashTcpLifeCycleTest.java", TEST_JAVA);
    templateToLogstash(project, packageNamePath, "test", "LogstashTcpPropertiesTest.java", TEST_JAVA);
  }

  @Override
  public void addProperties(Project project) {
    properties().forEach((k, v) -> springBootPropertiesService.addProperties(project, k, v));
  }

  private Map<String, Object> properties() {
    TreeMap<String, Object> result = new TreeMap<>();

    result.put("application.logging.logstash.tcp.enabled", true);
    result.put("application.logging.logstash.tcp.host", "localhost");
    result.put("application.logging.logstash.tcp.port", 5000);
    result.put("application.logging.logstash.tcp.ring-buffer-size", 8192);
    result.put("application.logging.logstash.tcp.shutdown_grace_period", "PT1M");

    return result;
  }

  private void templateToLogstash(Project project, String source, String type, String sourceFilename, String destination) {
    projectRepository.template(project, getPath(SOURCE, type), sourceFilename, getPath(destination, source, DESTINATION));
  }
}
