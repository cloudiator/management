package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.User;
import org.cloudiator.messages.entities.UserEntities;

public class UserConverter implements TwoWayConverter<User, UserEntities.User> {

  public static final UserConverter INSTANCE = new UserConverter();
  private static final TenantConverter TENANT_CONVERTER = TenantConverter.INSTANCE;

  private UserConverter() {

  }

  @Override
  public User applyBack(UserEntities.User protoUser) {
    User user = new User(protoUser.getEmail(), "password", "salt",
        TENANT_CONVERTER.applyBack(protoUser.getTenant()));

    return user;
  }

  @Override
  public UserEntities.User apply(User domainUser) {
    UserEntities.User.Builder builder = UserEntities.User.newBuilder()
        .setEmail(domainUser.email())
        .setTenant(TENANT_CONVERTER.apply(domainUser.tenant()));
    return builder.build();
  }
}
