package io.github.cloudiator.management.user.messaging;

import io.github.cloudiator.management.user.converter.TenantConverter;
import io.github.cloudiator.management.user.domain.AuthenticationService;
import io.github.cloudiator.management.user.domain.Tenant;
import java.util.Optional;
import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.CreateTenantRequest;
import org.cloudiator.messages.entities.User.CreateTenantResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTenantListener implements KafkaListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateTenantListener.class);
  private final MessageInterface messagingInterface;
  private final TenantConverter tenantConverter = TenantConverter.INSTANCE;
  private final AuthenticationService authenticationService;

  @Inject
  public CreateTenantListener(MessageInterface messagingInterface,
      AuthenticationService authenticationService) {
    this.messagingInterface = messagingInterface;
    this.authenticationService = authenticationService;
  }

  private Tenant createTenant(String name) {
    return authenticationService.createTenant(name);
  }

  @Override
  public void run() {

    messagingInterface.subscribe(CreateTenantRequest.class, CreateTenantRequest.parser(),
        new MessageCallback<CreateTenantRequest>() {
          @Override
          public void accept(String id, CreateTenantRequest content) {

            try {

              final Optional<Tenant> existingTenant = authenticationService
                  .getTenant(content.getTenant());

              if (existingTenant.isPresent()) {
                messagingInterface.reply(CreateTenantResponse.class, id,
                    Error.newBuilder().setCode(400).setMessage("Tenant already exists").build());
                return;
              }

              final Tenant tenant = createTenant(content.getTenant());

              final CreateTenantResponse createTenantResponse = CreateTenantResponse.newBuilder()
                  .setTenant(tenantConverter.apply(tenant))
                  .build();

              messagingInterface.reply(id, createTenantResponse);

            } catch (Exception e) {
              LOGGER.error("Error while creating user.", e);
              messagingInterface.reply(CreateTenantResponse.class, id,
                  Error.newBuilder().setCode(400)
                      .setMessage("Error while creating tenant: " + e.getMessage()).build());
            }
          }
        });

  }
}
