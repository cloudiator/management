package io.github.cloudiator.management.user.messaging;

import com.google.inject.persist.Transactional;
import io.github.cloudiator.management.user.converter.TenantConverter;
import io.github.cloudiator.management.user.domain.AuthService;
import io.github.cloudiator.management.user.domain.AuthenticationService;
import io.github.cloudiator.management.user.domain.Tenant;
import java.util.Optional;
import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.TenantQueryRequest;
import org.cloudiator.messages.entities.User.TenantQueryResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class TenantQueryListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final AuthenticationService authenticationService;

  private AuthService authService;
  private final TenantConverter tenantConverter = TenantConverter.INSTANCE;


  @Inject
  public TenantQueryListener(MessageInterface messagingInterface,
      AuthenticationService authenticationService) {
    this.messagingInterface = messagingInterface;
    this.authenticationService = authenticationService;
  }

  @Transactional
  private Iterable<Tenant> retrieveTenants() {
    return authenticationService.retrieveTenants();
  }

  @Override
  public void run() {

    messagingInterface.subscribe(TenantQueryRequest.class, TenantQueryRequest.parser(),
        new MessageCallback<TenantQueryRequest>() {
          @Override
          public void accept(String id, TenantQueryRequest content) {
            //start the transaction

            try {
              TenantQueryResponse.Builder responseBuilder = TenantQueryResponse.newBuilder();
              if (content.getTenantId().isEmpty()) {
                //All Tenants are requested
                for (Tenant tenant : retrieveTenants()) {
                  responseBuilder.addTenant(tenantConverter.apply(tenant));
                }

              } else {
                // single IDQuery
                Optional<Tenant> requestedTenant = authenticationService
                    .getTenant(content.getTenantId());
                requestedTenant
                    .ifPresent(tenant -> responseBuilder.addTenant(tenantConverter.apply(tenant)));

              }
              messagingInterface.reply(id, responseBuilder.build());
            } catch (Exception e) {
              messagingInterface
                  .reply(TenantQueryResponse.class, id,
                      Error.newBuilder().setCode(500)
                          .setMessage("Error while searching tenant " + e.getMessage()).build());
            }

          }
        }
    );

  }
}
