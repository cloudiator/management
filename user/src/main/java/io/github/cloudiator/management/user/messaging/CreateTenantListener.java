package io.github.cloudiator.management.user.messaging;

import io.github.cloudiator.management.user.converter.TenantConverter;
import io.github.cloudiator.management.user.domain.Tenant;
import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.CreateTenantRequest;
import org.cloudiator.messages.entities.User.CreateTenantResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class CreateTenantListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final TenantConverter tenantConverter = new TenantConverter();

  @Inject
  public CreateTenantListener(MessageInterface messagingInterface) {
    this.messagingInterface = messagingInterface;
  }

  @Override
  public void run() {

    messagingInterface.subscribe(CreateTenantRequest.class, CreateTenantRequest.parser(),
        new MessageCallback<CreateTenantRequest>() {
          @Override
          public void accept(String id, CreateTenantRequest content) {

            CreateTenantResponse.Builder responseBuilder = CreateTenantResponse.newBuilder();

            //convert to domain object
            Tenant domainTenant = tenantConverter.applyBack(content.getTenant());

            //store to database

            //reply

            responseBuilder.setTenant(tenantConverter.apply(domainTenant));
            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());

            //success
            messagingInterface.reply(id, responseBuilder.build());

            //error
            messagingInterface.reply(CreateTenantResponse.class, id, Error.newBuilder().build());

          }
        });

  }
}
