package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.User;
import org.cloudiator.messages.entities.UserEntities;

public class UserConverter implements TwoWayConverter<User, UserEntities.User> {

  private final TenantConverter tenantConverter;

  public UserConverter() {
    this.tenantConverter = new TenantConverter();
  }

  @Override
  public User applyBack(UserEntities.User protoUser) {
    User user = new User();
    user.setEmail(protoUser.getEmail());
    user.setTenant(tenantConverter.applyBack(protoUser.getTenant()));

    //user.setPassword("loged_in");

    return user;
  }

  @Override
  public UserEntities.User apply(User domainUser) {
    UserEntities.User.Builder builder = UserEntities.User.newBuilder()
        .setEmail(domainUser.getEmail())
        .setTenant(tenantConverter.apply(domainUser.getTenant()));
    return builder.build();
  }
}
