package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.User;
import org.cloudiator.messages.entities.UserEntities.Login;

@Deprecated
public class LoginConverter implements TwoWayConverter<User, Login> {

  public final static LoginConverter INSTANCE = new LoginConverter();

  private static final TenantConverter TENANT_CONVERTER = TenantConverter.INSTANCE;

  private LoginConverter() {

  }

  @Override
  public User applyBack(Login protologin) {
    User user = new User(protologin.getEmail(), protologin.getPassword(), "salt",
        TENANT_CONVERTER.applyBack(protologin.getTenant()));
    return user;
  }

  @Override
  public Login apply(User domainUser) {
    Login.Builder builder = Login.newBuilder()
        .setEmail(domainUser.email())
        .setPassword(domainUser.password())
        .setTenant(TENANT_CONVERTER.apply(domainUser.tenant()));
    return builder.build();
  }
}
