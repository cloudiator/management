package io.github.cloudiator.persistance;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"secureEntryKey", "tenantId"})
})
public class SecureEntryModel extends Model {

  @Column(nullable = false, name = "secureEntryKey")
  @Lob
  private String key;
  @Column(nullable = false)
  @Lob
  private String encryptedValue;
  @ManyToOne(optional = false)
  @JoinColumn(name = "tenantId")
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
    this.tenant = tenant;
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
