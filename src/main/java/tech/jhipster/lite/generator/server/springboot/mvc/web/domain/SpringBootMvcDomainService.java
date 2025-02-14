package tech.jhipster.lite.generator.server.springboot.mvc.web.domain;

import static tech.jhipster.lite.common.domain.FileUtils.getPath;
import static tech.jhipster.lite.generator.project.domain.Constants.MAIN_JAVA;
import static tech.jhipster.lite.generator.project.domain.Constants.TEST_JAVA;
import static tech.jhipster.lite.generator.project.domain.DefaultConfig.PACKAGE_NAME;
import static tech.jhipster.lite.generator.server.springboot.mvc.web.domain.SpringBootMvc.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhipster.lite.error.domain.UnauthorizedValueException;
import tech.jhipster.lite.generator.buildtool.generic.domain.BuildToolService;
import tech.jhipster.lite.generator.project.domain.Project;
import tech.jhipster.lite.generator.project.domain.ProjectRepository;
import tech.jhipster.lite.generator.server.springboot.logging.domain.Level;
import tech.jhipster.lite.generator.server.springboot.logging.domain.SpringBootLoggingService;
import tech.jhipster.lite.generator.server.springboot.properties.domain.SpringBootPropertiesService;

public class SpringBootMvcDomainService implements SpringBootMvcService {

  private final Logger log = LoggerFactory.getLogger(SpringBootMvcDomainService.class);

  public static final String SOURCE = "server/springboot/mvc/web";
  public static final String EXCEPTION_HANDLER_PATH = "technical/infrastructure/primary/exception";

  public final ProjectRepository projectRepository;
  public final BuildToolService buildToolService;
  public final SpringBootPropertiesService springBootPropertiesService;
  public final SpringBootLoggingService springBootLoggingService;

  public SpringBootMvcDomainService(
    ProjectRepository projectRepository,
    BuildToolService buildToolService,
    SpringBootPropertiesService springBootPropertiesService,
    SpringBootLoggingService springBootLoggingService
  ) {
    this.projectRepository = projectRepository;
    this.buildToolService = buildToolService;
    this.springBootPropertiesService = springBootPropertiesService;
    this.springBootLoggingService = springBootLoggingService;
  }

  @Override
  public void init(Project project) {
    addSpringBootMvc(project);
  }

  @Override
  public void addSpringBootMvc(Project project) {
    buildToolService.addDependency(project, springBootStarterWebDependency());

    addServerPortInProperties(project);
    addExceptionHandler(project);
    addLoggerInConfiguration(project, "org.springframework.web", Level.WARN);
  }

  @Override
  public void addSpringBootUndertow(Project project) {
    buildToolService.addDependency(project, springBootStarterWebDependency(), List.of(tomcatDependency()));
    buildToolService.addDependency(project, undertowDependency());

    addServerPortInProperties(project);
    addExceptionHandler(project);
    addLoggerInConfiguration(project, "io.undertow", Level.WARN);
  }

  @Override
  public void addSpringBootActuator(Project project) {
    buildToolService.addDependency(project, springBootActuatorDependency());

    springBootPropertiesService.addProperties(project, "management.endpoints.web.base-path", "/management");
    springBootPropertiesService.addProperties(
      project,
      "management.endpoints.web.exposure.include",
      "configprops, env, health, info, logfile, loggers, threaddump"
    );
    springBootPropertiesService.addProperties(project, "management.endpoint.health.probes.enabled", "true");
    springBootPropertiesService.addProperties(project, "management.endpoint.health.group.liveness.include", "livenessState");
    springBootPropertiesService.addProperties(project, "management.endpoint.health.group.readiness.include", "readinessState");
  }

  @Override
  public void addExceptionHandler(Project project) {
    project.addDefaultConfig(PACKAGE_NAME);

    buildToolService.addProperty(project, "problem-spring", SpringBootMvc.problemSpringVersion());
    buildToolService.addProperty(project, "problem-spring-web", "\\${problem-spring.version}");

    buildToolService.addDependency(project, problemSpringDependency());
    buildToolService.addDependency(project, springBootStarterValidation());

    String packageName = project.getPackageName().orElse("com.mycompany.myapp");
    springBootPropertiesService.addProperties(project, "application.exception.details", "false");
    springBootPropertiesService.addProperties(
      project,
      "application.exception.package",
      "org.,java.,net.,javax.,com.,io.,de.," + packageName
    );
    springBootPropertiesService.addPropertiesTest(project, "application.exception.package", "org.,java.");

    String packageNamePath = project.getPackageNamePath().orElse(getPath("com/mycompany/myapp"));
    templateToExceptionHandler(project, packageNamePath, "src", "BadRequestAlertException.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "ErrorConstants.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "ExceptionTranslator.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "FieldErrorDTO.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "HeaderUtil.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "ProblemConfiguration.java", MAIN_JAVA);

    templateToExceptionHandler(project, packageNamePath, "test", "BadRequestAlertExceptionTest.java", TEST_JAVA);
    templateToExceptionHandler(project, packageNamePath, "test", "ExceptionTranslatorIT.java", TEST_JAVA);
    templateToExceptionHandler(project, packageNamePath, "test", "ExceptionTranslatorTestController.java", TEST_JAVA);
    templateToExceptionHandler(project, packageNamePath, "test", "FieldErrorDTOTest.java", TEST_JAVA);
    templateToExceptionHandler(project, packageNamePath, "test", "HeaderUtilTest.java", TEST_JAVA);

    projectRepository.template(project, getPath(SOURCE, "test"), "TestUtil.java", getPath(TEST_JAVA, packageNamePath));
  }

  private void templateToExceptionHandler(Project project, String source, String type, String sourceFilename, String destination) {
    projectRepository.template(project, getPath(SOURCE, type), sourceFilename, getPath(destination, source, EXCEPTION_HANDLER_PATH));
  }

  private void addServerPortInProperties(Project project) {
    springBootPropertiesService.addProperties(project, "server.port", getServerPort(project));
    springBootPropertiesService.addPropertiesTest(project, "server.port", 0);
  }

  private void addLoggerInConfiguration(Project project, String packageName, Level level) {
    springBootLoggingService.addLogger(project, packageName, level);
    springBootLoggingService.addLoggerTest(project, packageName, level);
  }

  private int getServerPort(Project project) {
    int serverPort;
    try {
      serverPort = project.getIntegerConfig("serverPort").orElse(8080);
    } catch (UnauthorizedValueException e) {
      log.warn("The serverPort config is not valid");
      serverPort = 8080;
    }
    return serverPort;
  }
}
