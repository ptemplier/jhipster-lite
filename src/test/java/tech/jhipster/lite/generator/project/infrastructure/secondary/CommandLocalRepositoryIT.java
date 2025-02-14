package tech.jhipster.lite.generator.project.infrastructure.secondary;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tech.jhipster.lite.TestUtils.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.jhipster.lite.IntegrationTest;
import tech.jhipster.lite.error.domain.GeneratorException;
import tech.jhipster.lite.generator.project.domain.Project;

@IntegrationTest
class CommandLocalRepositoryIT {

  @Autowired
  CommandLocalRepository repository;

  @Test
  void shouldNpmInstallAndPrettierFormat() {
    Project project = tmpProjectWithPackageJson();

    repository.npmInstall(project);
    repository.npmPrettierFormat(project);

    assertFileExist(project, "node_modules");
  }

  @Test
  void shouldNotNpmInstall() {
    Project project = tmpProject();

    assertThatThrownBy(() -> repository.npmInstall(project)).isExactlyInstanceOf(GeneratorException.class);
  }

  @Test
  void shouldNotNpmPrettierFormat() {
    Project project = tmpProject();

    assertThatThrownBy(() -> repository.npmPrettierFormat(project)).isExactlyInstanceOf(GeneratorException.class);
  }
}
