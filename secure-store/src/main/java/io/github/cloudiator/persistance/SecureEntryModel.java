package io.github.cloudiator.persistance;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"key", "tenant"})
})
public class SecureEntryModel extends Model {

  @Column(nullable = false)
  private String key;
  @Column(nullable = false)
  private String encryptedValue;
  @ManyToOne(optional = false)
  private TenantModel tenant;

  /**
   * Empty constructor for hibernate
   */
  protected SecureEntryModel() {

  }

  SecureEntryModel(TenantModel tenant, String key, String encryptedValue) {

    checkNotNull(tenant, "tenant is null");
    checkNotNull(key, "key is null");
    checkArgument(!key.isEmpty(), "key is empty");
    checkNotNull(encryptedValue, "encryptedValue is null");
    checkArgument(!encryptedValue.isEmpty(), "encryptedValue is empty");

    this.key = key;
    this.encryptedValue = encryptedValue;
  }


  public String getKey() {
    return key;
  }

  public String getEncryptedValue() {
    return encryptedValue;
  }

  public SecureEntryModel setEncryptedValue(String encryptedValue) {
    this.encryptedValue = encryptedValue;
    return this;
  }
}
