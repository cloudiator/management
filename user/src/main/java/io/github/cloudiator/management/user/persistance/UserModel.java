package io.github.cloudiator.management.user.persistance;



import io.github.cloudiator.persistance.*;
import de.uniulm.omi.cloudiator.util.Password;
import io.github.cloudiator.management.user.domain.Tenant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
class UserModel extends Model {

  @Column(unique = true, nullable = false)
  private String mail;

  @Column(nullable = false)
  private String salt;

  @Column(nullable = false)
  private String password;

  @Column(nullable = true)
  private String tenant;

  protected UserModel() {

  }

  UserModel(String mail, String salt, String password, String tenant) {
    this.mail = mail;
    this.salt = salt;
    this.password = password;
    this.tenant = tenant;
  }

  String getMail() {
    return mail;
  }

  String getTenant() {
    return tenant;
  }

  String getPassword() {
    return password;
  }

  String getSalt() {
    return salt;
  }

  void setTenant(String newTenant) {
    this.tenant = newTenant;
  }

}
