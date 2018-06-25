package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.UserNew;
import org.cloudiator.messages.entities.UserEntities;

public class UserNewConverter implements TwoWayConverter<UserNew, UserEntities.UserNew> {

  private static final TenantConverter TENANT_CONVERTER = TenantConverter.INSTANCE;

  @Override
  public UserNew applyBack(UserEntities.UserNew protoUserNew) {
    return new UserNew(protoUserNew.getEmail(),
        TENANT_CONVERTER.applyBack(protoUserNew.getTenant()),
        protoUserNew.getPassword());
  }

  @Override
  public UserEntities.UserNew apply(UserNew domainUserNew) {
    UserEntities.UserNew.Builder builder = UserEntities.UserNew.newBuilder()
        .setEmail(domainUserNew.getEmail())
        .setPassword(domainUserNew.getPassword())
        .setTenant(TENANT_CONVERTER.apply(domainUserNew.getTenant()));
    return builder.build();
  }
}
