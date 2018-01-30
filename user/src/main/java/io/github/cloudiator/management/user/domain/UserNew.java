package io.github.cloudiator.management.user.domain;

public class UserNew {

  private String email;
  private Tenant tenant;
  private String password;
  private String passwordRepeat;

  public UserNew(String email, Tenant tenant, String password, String passwordRepeat) {

    this.email = email;
    this.tenant = tenant;
    this.password = password;
    this.passwordRepeat = passwordRepeat;
  }

  public UserNew() {
    this.email = null;
    this.password = null;
    this.passwordRepeat = null;
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

  public String getPasswordRepeat() {
    return passwordRepeat;
  }

  public void setPasswordRepeat(String passwordRepeat) {
    this.passwordRepeat = passwordRepeat;
  }

  public Tenant getTenant() {
    return tenant;
  }

  public void setTenant(Tenant tenant) {
    this.tenant = tenant;
  }
}
