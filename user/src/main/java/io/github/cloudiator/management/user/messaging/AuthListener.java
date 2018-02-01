package io.github.cloudiator.management.user.messaging;

import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.AuthRequest;
import org.cloudiator.messages.entities.User.AuthResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class AuthListener implements Runnable {

  private final MessageInterface messagingInterface;

  @Inject
  public AuthListener(MessageInterface messagingInterface) {
    this.messagingInterface = messagingInterface;
  }

  @Override
  public void run() {

    messagingInterface.subscribe(AuthRequest.class, AuthRequest.parser(),
        new MessageCallback<AuthRequest>() {
          @Override
          public void accept(String id, AuthRequest content) {

            AuthResponse response = AuthResponse.newBuilder().build();

            //convert to domain object


            //store to database

            //reply

            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());

            //success
            messagingInterface.reply(id, response);

            //error
            messagingInterface.reply(AuthResponse.class, id, Error.newBuilder().build());

          }
        }
    );
  }
}

