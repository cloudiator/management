package io.github.cloudiator.management.user.domain;

public class User {

  private String email;
  private String password;
  private Tenant tenant;


  public User(String email, String password, Tenant tenant) {

    this.email = email;
    this.password = password;
    this.tenant = tenant;
  }

  public User() {
    this.email = null;
    this.password = null;
    this.tenant = null;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Tenant getTenant() {
    return tenant;
  }

  public void setTenant(Tenant tenant) {
    this.tenant = tenant;
  }
}
