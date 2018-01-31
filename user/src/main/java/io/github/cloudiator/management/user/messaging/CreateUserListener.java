package io.github.cloudiator.management.user.messaging;

import io.github.cloudiator.management.user.converter.UserConverter;
import io.github.cloudiator.management.user.converter.UserNewConverter;
import io.github.cloudiator.management.user.domain.User;
import io.github.cloudiator.management.user.domain.UserNew;
import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.CreateUserRequest;
import org.cloudiator.messages.entities.User.CreateUserResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;
import org.cloudiator.messaging.ResponseException;

public class CreateUserListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final UserNewConverter userNewConverter = new UserNewConverter();
  private final UserConverter userConverter = new UserConverter();

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
            UserNew requestedUser = userNewConverter.applyBack(content.getNewUser());
            User domainUser = new User();

            //ERROR: PasswordMismatch
            if (!requestedUser.getPasswordRepeat().matches(requestedUser.getPassword())) {
              messagingInterface
                  .reply(CreateUserResponse.class, id,
                      Error.newBuilder().setCode(400).setMessage("PasswordRepeat does not match.")
                          .build());
            }

            domainUser.setEmail(requestedUser.getEmail());
            domainUser.setPassword(requestedUser.getPassword());
            domainUser.setTenant(requestedUser.getTenant());

            //store to database

            //ERROR: Tenant not existing
            //ERROR: User already exists

            //reply
            responseBuilder.setUser(userConverter.apply(domainUser));
            CreateUserResponse response = responseBuilder.build();

            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());

            //success
            messagingInterface.reply( id, response);


            //error
            messagingInterface.reply(CreateUserResponse.class, id, Error.newBuilder().build());


          }
        }
    );

  }
}
