package io.github.cloudiator.management.user.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import de.uniulm.omi.cloudiator.util.Password;
import java.util.Base64;

public class User {

  private final String email;
  private final String password;
  private final String salt;
  private final Tenant tenant;
  private static final Password PASSWORD_UTIL = Password.getInstance();


  public User(String email, String password, String salt, Tenant tenant) {

    checkNotNull(email, "email is null");
    checkArgument(!email.isEmpty(), "email is empty");
    this.email = email;

    checkNotNull(password, "password is null");
    checkArgument(!password.isEmpty(), "password is empty");
    this.password = password;

    checkNotNull(salt, "salt is null");
    checkArgument(!salt.isEmpty(), "salt is empty");
    this.salt = salt;

    checkNotNull(tenant, "tenant is null");
    this.tenant = tenant;
  }

  public boolean validatePassword(final String password) {
    final char[] passwordInChar = password.toCharArray();

    return PASSWORD_UTIL.check(passwordInChar, passwordInChar, saltInByte());
  }

  public String email() {
    return email;
  }

  public String password() {
    return password;
  }

  private char[] passwordInChar() {
    return password().toCharArray();
  }

  public String salt() {
    return salt;
  }

  private byte[] saltInByte() {
    return Base64.getDecoder().decode(salt());
  }

  public Tenant tenant() {
    return tenant;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("email", email).add("tenant", tenant).toString();
  }
}
