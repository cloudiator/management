package io.github.cloudiator.management.user.messaging;

import io.github.cloudiator.management.user.converter.TenantConverter;
import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.CreateTenantRequest;
import org.cloudiator.messages.entities.User.CreateTenantResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class CreateTenantListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final TenantConverter tenantConverter;

  @Inject
  public CreateTenantListener(MessageInterface messagingInterface) {
    this.messagingInterface = messagingInterface;
    this.tenantConverter = new TenantConverter();
  }

  @Override
  public void run() {

    messagingInterface.subscribe(CreateTenantRequest.class, CreateTenantRequest.parser(),
        new MessageCallback<CreateTenantRequest>() {
          @Override
          public void accept(String id, CreateTenantRequest content) {

            CreateTenantResponse response = CreateTenantResponse.newBuilder().build();

            //convert to domain object

            //store to database

            //reply

            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());

            //success
            messagingInterface.reply(id, response);

            //error
            messagingInterface.reply(CreateTenantResponse.class, id, Error.newBuilder().build());

          }
        });

  }
}
