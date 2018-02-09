package io.github.cloudiator.management.user.persistance;

import de.uniulm.omi.cloudiator.persistance.entities.Model;
import de.uniulm.omi.cloudiator.util.Password;
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

  public String getMail(){
    return mail;
  }

  public String getTenant(){
    return tenant;
  }

  public void setTenant(String newTenant){
    this.tenant=newTenant;
  }

}
