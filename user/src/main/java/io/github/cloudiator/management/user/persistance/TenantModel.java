package io.github.cloudiator.management.user.persistance;

import de.uniulm.omi.cloudiator.persistance.entities.Model;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class TenantModel extends Model {

  @Column(nullable = false, unique = true)
  String name;


}
