package io.github.cloudiator.management.user.domain;

public class UserNew {

  private String email;
  private Tenant tenant;
  private String password;

  public UserNew(String email, Tenant tenant, String password) {

    this.email = email;
    this.tenant = tenant;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public Tenant getTenant() {
    return tenant;
  }
}
