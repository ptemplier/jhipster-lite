package tech.jhipster.lite.module.domain.javadependency.command;

import tech.jhipster.lite.error.domain.Assert;
import tech.jhipster.lite.module.domain.javadependency.JavaDependency;

public record AddDirectJavaDependency(JavaDependency dependency) implements JavaDependencyCommand, AddJavaDependency {
  public AddDirectJavaDependency {
    Assert.notNull("dependency", dependency);
  }

  @Override
  public JavaDependencyCommandType type() {
    return JavaDependencyCommandType.ADD_DEPENDENCY;
  }
}