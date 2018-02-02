package io.github.cloudiator.management.user.persistance;

import de.uniulm.omi.cloudiator.persistance.entities.Model;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
class UserModel extends Model {

  @Column(unique = true, nullable = false)
  private String mail;

  @Column(nullable = false)
  private String salt;

  @Column(nullable = false)
  private String password;

  protected UserModel() {

  }

  UserModel(String mail, String salt, String password) {
    this.mail = mail;
    this.salt = salt;
    this.password = password;
  }

}
