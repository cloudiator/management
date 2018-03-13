package io.github.cloudiator.management.user.domain;

public class User {

  private String email;
  private String password;
  private String salt;
  private Tenant tenant;


  public User(String email, String password, String salt, Tenant tenant) {

    this.email = email;
    this.password = password;
    this.salt = salt;
    this.tenant = tenant;
  }

  protected User() {
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

  public String getSalt() {
    return salt;
  }

  public Tenant getTenant() {
    return tenant;
  }

  public void setTenant(Tenant tenant) {
    this.tenant = tenant;
  }
}
