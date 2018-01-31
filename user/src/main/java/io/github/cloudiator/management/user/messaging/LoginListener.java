package io.github.cloudiator.management.user.messaging;

import io.github.cloudiator.management.user.converter.LoginConverter;
import io.github.cloudiator.management.user.domain.User;
import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.LoginRequest;
import org.cloudiator.messages.entities.User.LoginResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class LoginListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final LoginConverter loginConverter = new LoginConverter();

  @Inject
  public LoginListener(MessageInterface messagingInterface) {
    this.messagingInterface = messagingInterface;
  }

  @Override
  public void run() {

    messagingInterface.subscribe(LoginRequest.class, LoginRequest.parser(),
        new MessageCallback<LoginRequest>() {
          @Override
          public void accept(String id, LoginRequest content) {

            LoginResponse.Builder responseBuilder = LoginResponse.newBuilder();

            //convert to domain object
            User userRequested = loginConverter.applyBack(content.getLogin());

            //validate to database

            //create Token

            //reply

            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());

            //success
            messagingInterface.reply(id, responseBuilder.build());

            //error
            messagingInterface.reply(LoginResponse.class, id, Error.newBuilder().build());

          }
        }
    );

  }
}
