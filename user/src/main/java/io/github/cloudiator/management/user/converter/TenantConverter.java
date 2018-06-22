package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.Tenant;
import org.cloudiator.messages.entities.UserEntities;

public class TenantConverter implements TwoWayConverter<Tenant, UserEntities.Tenant> {

  public static final TenantConverter INSTANCE = new TenantConverter();

  private TenantConverter() {

  }

  @Override
  public Tenant applyBack(UserEntities.Tenant protoTenant) {
    Tenant result = new Tenant(protoTenant.getTenant());
    return result;
  }

  @Override
  public UserEntities.Tenant apply(Tenant domainTenant) {
    UserEntities.Tenant.Builder builder = UserEntities.Tenant.newBuilder()
        .setTenant(domainTenant.getName());
    return builder.build();
  }
}
