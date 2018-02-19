package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.User;
import org.cloudiator.messages.entities.UserEntities.Login;

public class LoginConverter implements TwoWayConverter<User, Login> {

  private TenantConverter tenantConverter;

  public LoginConverter() {
    this.tenantConverter = new TenantConverter();
  }

  @Override
  public User applyBack(Login protologin) {
    User user = new User(protologin.getEmail(), protologin.getPassword(), "salt",
        tenantConverter.applyBack(protologin.getTenant()));
    return user;
  }

  @Override
  public Login apply(User domainUser) {
    Login.Builder builder = Login.newBuilder()
        .setEmail(domainUser.getEmail())
        .setPassword(domainUser.getPassword())
        .setTenant(tenantConverter.apply(domainUser.getTenant()));
    return builder.build();
  }
}
