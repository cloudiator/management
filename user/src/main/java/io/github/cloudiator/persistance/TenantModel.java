package io.github.cloudiator.persistance;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
class TenantModel extends Model {

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(targetEntity = UserModel.class, mappedBy = "tenant",cascade = CascadeType.ALL)
  private Set<UserModel> listeners;

  protected TenantModel() {
  }

  TenantModel(String name) {
    this.name = name;
    listeners = new HashSet<UserModel>();
  }

  public String getName() {
    return name;
  }


  public Set<UserModel> getListeners() {
    return listeners;
  }

  public void setListeners(Set<UserModel> listeners) {
    this.listeners = listeners;
  }

  public void addListener (UserModel user){
    this.listeners.add(user);
  }

}
