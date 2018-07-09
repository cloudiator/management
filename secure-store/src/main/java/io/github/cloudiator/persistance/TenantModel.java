package io.github.cloudiator.persistance;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class TenantModel extends Model {

  @Column(nullable = false, unique = true)
  private String name;

  protected TenantModel() {

  }

  TenantModel(String name) {
    checkNotNull(name, "name is null");
    checkArgument(!name.isEmpty(), "name is empty");
    this.name = name;
  }


  public String getName() {
    return name;
  }
}
