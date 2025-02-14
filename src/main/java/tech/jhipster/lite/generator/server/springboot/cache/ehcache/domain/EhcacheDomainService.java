package tech.jhipster.lite.generator.server.springboot.cache.ehcache.domain;

import static tech.jhipster.lite.common.domain.FileUtils.getPath;
import static tech.jhipster.lite.generator.project.domain.Constants.*;
import static tech.jhipster.lite.generator.project.domain.DefaultConfig.BASE_NAME;
import static tech.jhipster.lite.generator.project.domain.DefaultConfig.PACKAGE_NAME;

import tech.jhipster.lite.generator.buildtool.generic.domain.BuildToolService;
import tech.jhipster.lite.generator.project.domain.Project;
import tech.jhipster.lite.generator.project.domain.ProjectRepository;
import tech.jhipster.lite.generator.server.springboot.cache.jcache.domain.SpringBootJCacheService;
import tech.jhipster.lite.generator.server.springboot.properties.domain.SpringBootPropertiesService;

public class EhcacheDomainService implements EhcacheService {

  public static final String SOURCE = "server/springboot/cache/ehcache";
  public static final String DESTINATION = "technical/infrastructure/secondary/cache/ehcache";

  private final BuildToolService buildToolService;
  private final ProjectRepository projectRepository;
  private final SpringBootJCacheService springBootJCacheService;
  private final SpringBootPropertiesService springBootPropertiesService;

  public EhcacheDomainService(
    BuildToolService buildToolService,
    ProjectRepository projectRepository,
    SpringBootJCacheService springBootJCacheService,
    SpringBootPropertiesService springBootPropertiesService
  ) {
    this.buildToolService = buildToolService;
    this.projectRepository = projectRepository;
    this.springBootJCacheService = springBootJCacheService;
    this.springBootPropertiesService = springBootPropertiesService;
  }

  @Override
  public void initJavaConfiguration(Project project) {
    addDependencies(project);
    addEnableCaching(project);
    addJavaConfig(project);
    addJavaProperties(project);
  }

  @Override
  public void initXmlConfiguration(Project project) {
    addDependencies(project);
    addXmlDependencies(project);
    addEnableCaching(project);
    addEhcacheXml(project);
    addXmlProperty(project);
  }

  @Override
  public void addDependencies(Project project) {
    springBootJCacheService.addDependencies(project);
    buildToolService.addDependency(project, Ehcache.ehcacheDependency());
  }

  @Override
  public void addXmlDependencies(Project project) {
    buildToolService.addDependency(project, Ehcache.jakartaXmlBindApi());
    buildToolService.addDependency(project, Ehcache.glassfishJaxbRuntime());
  }

  @Override
  public void addEnableCaching(Project project) {
    springBootJCacheService.addEnableCaching(project);
  }

  @Override
  public void addEhcacheXml(Project project) {
    projectRepository.add(project, getPath(SOURCE, "resources"), "ehcache.xml", getPath(MAIN_RESOURCES, "config/ehcache"));
  }

  @Override
  public void addXmlProperty(Project project) {
    springBootPropertiesService.addProperties(project, "spring.cache.jcache.config", "classpath:config/ehcache/ehcache.xml");
  }

  @Override
  public void addJavaConfig(Project project) {
    springBootJCacheService.addJavaConfig(project);

    project.addDefaultConfig(PACKAGE_NAME);
    project.addDefaultConfig(BASE_NAME);
    String packageNamePath = project.getPackageNamePath().orElse(getPath("com/mycompany/myapp"));

    templateToEhcache(project, packageNamePath, "src", "EhcacheConfiguration.java", MAIN_JAVA);
    templateToEhcache(project, packageNamePath, "src", "EhcacheConfigurer.java", MAIN_JAVA);
    templateToEhcache(project, packageNamePath, "src", "EhcacheProperties.java", MAIN_JAVA);

    templateToEhcache(project, packageNamePath, "test", "EhcacheConfigurationIT.java", TEST_JAVA);
  }

  @Override
  public void addJavaProperties(Project project) {
    springBootPropertiesService.addProperties(project, "application.cache.ehcache.max-entries", 100);
    springBootPropertiesService.addProperties(project, "application.cache.ehcache.time-to-live-seconds", 3600);
  }

  private void templateToEhcache(Project project, String source, String type, String sourceFilename, String destination) {
    projectRepository.template(project, getPath(SOURCE, type), sourceFilename, getPath(destination, source, DESTINATION));
  }
}
