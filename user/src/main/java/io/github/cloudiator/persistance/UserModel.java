package io.github.cloudiator.persistance;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
class UserModel extends Model {

  @Column(unique = true, nullable = false)
  @Lob
  private String mail;

  @Column(nullable = false)
  @Lob
  private String salt;

  @Column(nullable = false)
  @Lob
  private String password;

  @ManyToOne
  private TenantModel tenant;

  protected UserModel() {
  }


  public TenantModel getTenant() {
    return tenant;
  }


  UserModel(String mail, String salt, String password, TenantModel tenant) {
    this.mail = mail;
    this.salt = salt;
    this.password = password;
    this.tenant = tenant;
  }

  String getMail() {
    return mail;
  }


  String getPassword() {
    return password;
  }

  String getSalt() {
    return salt;
  }

  void setTenant(TenantModel newTenant) {
    this.tenant = newTenant;
  }


}
