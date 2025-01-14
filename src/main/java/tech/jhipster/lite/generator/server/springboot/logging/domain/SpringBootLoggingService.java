package tech.jhipster.lite.generator.server.springboot.logging.domain;

import tech.jhipster.lite.generator.project.domain.Project;

public interface SpringBootLoggingService {
  void addLogger(Project project, String packageName, Level level);

  void addLoggerTest(Project project, String packageName, Level level);
}
