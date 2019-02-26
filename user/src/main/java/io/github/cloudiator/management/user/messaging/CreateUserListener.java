package io.github.cloudiator.management.user.messaging;

import com.google.inject.persist.Transactional;
import io.github.cloudiator.management.user.converter.UserConverter;
import io.github.cloudiator.management.user.converter.UserNewConverter;
import io.github.cloudiator.management.user.domain.AuthenticationService;
import io.github.cloudiator.management.user.domain.User;
import io.github.cloudiator.management.user.domain.UserNew;
import java.util.Optional;
import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.CreateUserRequest;
import org.cloudiator.messages.entities.User.CreateUserResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserListener implements KafkaListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserListener.class);
  private final MessageInterface messagingInterface;
  private final UserNewConverter userNewConverter = new UserNewConverter();
  private final UserConverter userConverter = UserConverter.INSTANCE;
  private final AuthenticationService authenticationService;

  @Inject
  public CreateUserListener(MessageInterface messagingInterface,
      AuthenticationService authenticationService) {
    this.messagingInterface = messagingInterface;
    this.authenticationService = authenticationService;
  }

  @Transactional
  private User createUser(UserNew userNew) {
    return authenticationService.createUser(userNew);
  }

  @Override
  public void run() {

    messagingInterface.subscribe(CreateUserRequest.class, CreateUserRequest.parser(),
        new MessageCallback<CreateUserRequest>() {

          @Override
          public void accept(String id, CreateUserRequest content) {

            try {
              UserNew requestedUser = userNewConverter.applyBack(content.getNewUser());

              final Optional<User> existingUser = authenticationService
                  .getUser(content.getNewUser().getEmail());

              if (existingUser.isPresent()) {
                messagingInterface.reply(CreateUserResponse.class, id,
                    Error.newBuilder().setCode(400).setMessage("User already exists").build());
                return;
              }

              final User createdUser = createUser(requestedUser);

              final CreateUserResponse responseMessage = CreateUserResponse.newBuilder()
                  .setUser(userConverter.apply(createdUser)).build();

              messagingInterface.reply(id, responseMessage);

            } catch (Exception e) {
              LOGGER.error("Error while creating user.", e);
              messagingInterface.reply(CreateUserResponse.class, id,
                  Error.newBuilder().setCode(400)
                      .setMessage("Error while creating user: " + e.getMessage()).build());
            }
          }
        }
    );

  }
}
