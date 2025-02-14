package tech.jhipster.lite.generator.server.javatool.base.application;

import org.springframework.stereotype.Service;
import tech.jhipster.lite.generator.project.domain.Project;
import tech.jhipster.lite.generator.server.javatool.base.domain.JavaBaseService;

@Service
public class JavaBaseApplicationService {

  private final JavaBaseService javaBaseService;

  public JavaBaseApplicationService(JavaBaseService javaBaseService) {
    this.javaBaseService = javaBaseService;
  }

  public void init(Project project) {
    javaBaseService.init(project);
  }
}
