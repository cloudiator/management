package io.github.cloudiator.management.user.messaging;

import com.google.inject.persist.Transactional;
import io.github.cloudiator.management.user.converter.TokenConverter;
import io.github.cloudiator.management.user.converter.UserConverter;
import io.github.cloudiator.management.user.domain.AuthenticationService;
import io.github.cloudiator.management.user.domain.Token;
import io.github.cloudiator.management.user.domain.User;
import java.util.Optional;
import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.AuthRequest;
import org.cloudiator.messages.entities.User.AuthResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthListener implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthListener.class);
  private final MessageInterface messagingInterface;
  private final AuthenticationService authenticationService;
  private static final TokenConverter TOKEN_CONVERTER = TokenConverter.INSTANCE;
  private static final UserConverter USER_CONVERTER = UserConverter.INSTANCE;

  @Inject
  public AuthListener(MessageInterface messagingInterface,
      AuthenticationService authenticationService) {
    this.messagingInterface = messagingInterface;
    this.authenticationService = authenticationService;
  }

  @Transactional
  private Optional<User> getUser(Token token) {
    return authenticationService.validateToken(token);
  }

  private void replyUnauthorized(String originId) {
    messagingInterface.reply(AuthResponse.class, originId,
        Error.newBuilder().setCode(401).setMessage("Unauthorized").build());
  }

  private void replyAuthorized(String originId, User user) {

    AuthResponse.Builder response = AuthResponse.newBuilder();
    response.setUser(USER_CONVERTER.apply(user));
    messagingInterface.reply(originId, response.build());

  }

  @Override
  public void run() {

    messagingInterface.subscribe(AuthRequest.class, AuthRequest.parser(),
        new MessageCallback<AuthRequest>() {
          @Override
          public void accept(String id, AuthRequest content) {

            try {

              if (!content.hasToken()) {
                replyUnauthorized(id);
                return;
              }

              final Optional<User> optionalUser = getUser(
                  TOKEN_CONVERTER.applyBack(content.getToken()));
              if (!optionalUser.isPresent()) {
                replyUnauthorized(id);
                return;
              }

              replyAuthorized(id, optionalUser.get());

            } catch (Exception e) {
              LOGGER
                  .error("Error occurred during authorization. Reporting user as unauthorized.", e);
              replyUnauthorized(id);
            }
          }
        }
    );
  }
}

