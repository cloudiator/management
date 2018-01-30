package io.github.cloudiator.management.user.domain;

public class Tenant {

  private final String name;

  public Tenant(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
