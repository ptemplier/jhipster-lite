package tech.jhipster.lite.generator.server.springboot.dbmigration.liquibase.domain;

import static tech.jhipster.lite.common.domain.FileUtils.getPath;
import static tech.jhipster.lite.common.domain.WordUtils.indent;
import static tech.jhipster.lite.generator.project.domain.Constants.*;
import static tech.jhipster.lite.generator.project.domain.DefaultConfig.PACKAGE_NAME;
import static tech.jhipster.lite.generator.project.domain.DefaultConfig.PRETTIER_DEFAULT_INDENT;
import static tech.jhipster.lite.generator.server.springboot.dbmigration.liquibase.domain.Liquibase.NEEDLE_LIQUIBASE;

import tech.jhipster.lite.generator.buildtool.generic.domain.BuildToolService;
import tech.jhipster.lite.generator.buildtool.generic.domain.Dependency;
import tech.jhipster.lite.generator.project.domain.Project;
import tech.jhipster.lite.generator.project.domain.ProjectRepository;
import tech.jhipster.lite.generator.server.springboot.common.domain.SpringBootCommonService;
import tech.jhipster.lite.generator.server.springboot.logging.domain.Level;
import tech.jhipster.lite.generator.server.springboot.logging.domain.SpringBootLoggingService;

public class LiquibaseDomainService implements LiquibaseService {

  public static final String SOURCE = "server/springboot/dbmigration/liquibase";
  public static final String LIQUIBASE_PATH = "technical/infrastructure/secondary/liquibase";
  public static final String MASTER_XML = "master.xml";
  public static final String CONFIG_LIQUIBASE = "config/liquibase";

  private final ProjectRepository projectRepository;
  private final BuildToolService buildToolService;
  private final SpringBootCommonService springBootCommonService;
  private final SpringBootLoggingService springBootLoggingService;

  public LiquibaseDomainService(
    ProjectRepository projectRepository,
    BuildToolService buildToolService,
    SpringBootCommonService springBootCommonService,
    SpringBootLoggingService springBootLoggingService
  ) {
    this.projectRepository = projectRepository;
    this.buildToolService = buildToolService;
    this.springBootCommonService = springBootCommonService;
    this.springBootLoggingService = springBootLoggingService;
  }

  @Override
  public void init(Project project) {
    addLiquibase(project);
    addChangelogMasterXml(project);
    addConfigurationJava(project);
    addLoggerInConfiguration(project);
  }

  @Override
  public void addLiquibase(Project project) {
    Dependency liquibaseDependency = Dependency.builder().groupId("org.liquibase").artifactId("liquibase-core").build();
    buildToolService.addDependency(project, liquibaseDependency);

    Dependency h2dependency = Dependency.builder().groupId("com.h2database").artifactId("h2").scope("test").build();
    buildToolService.addDependency(project, h2dependency);
  }

  @Override
  public void addChangelogMasterXml(Project project) {
    projectRepository.add(project, getPath(SOURCE, "resources"), MASTER_XML, getPath(MAIN_RESOURCES, CONFIG_LIQUIBASE));
  }

  @Override
  public void addChangelogXml(Project project, String path, String fileName) {
    int indent = (Integer) project.getConfig(PRETTIER_DEFAULT_INDENT).orElse(2);
    String includeLine = new StringBuilder()
      .append(Liquibase.getIncludeLine(path, fileName))
      .append(System.lineSeparator())
      .append(indent(1, indent))
      .append(NEEDLE_LIQUIBASE)
      .toString();

    if (!projectRepository.containsRegexp(project, getPath(MAIN_RESOURCES, CONFIG_LIQUIBASE), MASTER_XML, includeLine)) {
      projectRepository.replaceText(project, getPath(MAIN_RESOURCES, CONFIG_LIQUIBASE), MASTER_XML, NEEDLE_LIQUIBASE, includeLine);
    }
  }

  @Override
  public void addConfigurationJava(Project project) {
    project.addDefaultConfig(PACKAGE_NAME);
    String packageNamePath = project.getPackageNamePath().orElse(getPath("com/mycompany/myapp"));

    templateToLiquibase(project, packageNamePath, "src", "AsyncSpringLiquibase.java", MAIN_JAVA);
    templateToLiquibase(project, packageNamePath, "src", "LiquibaseConfiguration.java", MAIN_JAVA);
    templateToLiquibase(project, packageNamePath, "src", "SpringLiquibaseUtil.java", MAIN_JAVA);

    springBootCommonService.addTestLogbackRecorder(project);
    templateToLiquibase(project, packageNamePath, "test", "AsyncSpringLiquibaseTest.java", TEST_JAVA);
    templateToLiquibase(project, packageNamePath, "test", "LiquibaseConfigurationIT.java", TEST_JAVA);
    templateToLiquibase(project, packageNamePath, "test", "SpringLiquibaseUtilTest.java", TEST_JAVA);
  }

  @Override
  public void addLoggerInConfiguration(Project project) {
    addLogger(project, "liquibase", Level.WARN);
    addLogger(project, "LiquibaseSchemaResolver", Level.INFO);
  }

  public void addLogger(Project project, String packageName, Level level) {
    springBootLoggingService.addLogger(project, packageName, level);
    springBootLoggingService.addLoggerTest(project, packageName, level);
  }

  private void templateToLiquibase(Project project, String source, String type, String sourceFilename, String destination) {
    projectRepository.template(project, getPath(SOURCE, type), sourceFilename, getPath(destination, source, LIQUIBASE_PATH));
  }
}
