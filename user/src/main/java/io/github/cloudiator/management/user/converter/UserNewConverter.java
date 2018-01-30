package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.UserNew;
import org.cloudiator.messages.entities.UserEntities;

public class UserNewConverter implements TwoWayConverter<UserNew, UserEntities.UserNew> {

  private final TenantConverter tenantConverter;

  public UserNewConverter() {
    this.tenantConverter = new TenantConverter();
  }

  @Override
  public UserNew applyBack(UserEntities.UserNew protoUserNew) {
    UserNew userNew = new UserNew();
    userNew.setEmail(protoUserNew.getEmail());
    userNew.setPassword(protoUserNew.getPassword());
    userNew.setPasswordRepeat(protoUserNew.getPasswordRepeat());
    userNew.setTenant(tenantConverter.applyBack(protoUserNew.getTenant()));

    return userNew;
  }

  @Override
  public UserEntities.UserNew apply(UserNew domainUserNew) {
    UserEntities.UserNew.Builder builder = UserEntities.UserNew.newBuilder()
        .setEmail(domainUserNew.getEmail())
        .setPassword(domainUserNew.getPassword())
        .setPasswordRepeat(domainUserNew.getPasswordRepeat())
        .setTenant(tenantConverter.apply(domainUserNew.getTenant()));
    return builder.build();
  }
}
