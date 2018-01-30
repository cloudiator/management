package io.github.cloudiator.management.user.messaging;

import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.CreateUserRequest;
import org.cloudiator.messages.entities.User.CreateUserResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class CreateUserListener implements Runnable {

  private final MessageInterface messagingInterface;

  @Inject
  public CreateUserListener(MessageInterface messagingInterface) {
    this.messagingInterface = messagingInterface;
  }

  @Override
  public void run() {

    messagingInterface.subscribe(CreateUserRequest.class, CreateUserRequest.parser(),
        new MessageCallback<CreateUserRequest>() {
          @Override
          public void accept(String id, CreateUserRequest content) {

            CreateUserResponse.Builder responseBuilder = CreateUserResponse.newBuilder();

            //convert to domain object



            //store to database

            //reply

            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());

            //success
            messagingInterface.reply(id, responseBuilder.build());

            //error
            messagingInterface.reply(CreateUserResponse.class, id, Error.newBuilder().build());

          }
        }
    );

  }
}
