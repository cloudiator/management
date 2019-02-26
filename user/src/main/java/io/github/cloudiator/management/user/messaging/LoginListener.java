package io.github.cloudiator.management.user.messaging;

import com.google.common.base.Strings;
import io.github.cloudiator.management.user.converter.TokenConverter;
import io.github.cloudiator.management.user.domain.AuthenticationService;
import io.github.cloudiator.management.user.domain.Token;
import java.util.Optional;
import javax.inject.Inject;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.LoginRequest;
import org.cloudiator.messages.entities.User.LoginResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginListener implements KafkaListener {

  private final MessageInterface messagingInterface;
  private final AuthenticationService authenticationService;
  private static final TokenConverter TOKEN_CONVERTER = TokenConverter.INSTANCE;
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginListener.class);

  @Inject
  public LoginListener(MessageInterface messagingInterface,
      AuthenticationService authenticationService) {
    this.messagingInterface = messagingInterface;
    this.authenticationService = authenticationService;
  }

  private Optional<Token> getToken(String email, String password) {
    return authenticationService.authenticateUser(email, password);
  }

  private void replyUnauthorized(String originId) {
    messagingInterface.reply(LoginResponse.class, originId,
        Error.newBuilder().setCode(401).setMessage("Unauthorized").build());
  }

  private void replyAuthorized(String originId, Token token) {
    final LoginResponse loginResponse = LoginResponse.newBuilder()
        .setToken(TOKEN_CONVERTER.apply(token))
        .build();
    messagingInterface.reply(originId, loginResponse);
  }

  @Override
  public void run() {

    messagingInterface.subscribe(LoginRequest.class, LoginRequest.parser(),
        new MessageCallback<LoginRequest>() {
          @Override
          public void accept(String id, LoginRequest content) {

            try {

              //todo: currently ignoring the tenant of login request

              LOGGER.debug(String.format("Receiving login request %s", id));

              if (!content.hasLogin()) {
                LOGGER.warn("Empty login received for request " + id);
                replyUnauthorized(id);
                return;
              }

              final String email = content.getLogin().getEmail();
              final String password = content.getLogin().getPassword();

              if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
                LOGGER.warn("Empty email or password received for request " + id);
                replyUnauthorized(id);
                return;
              }

              final Optional<Token> optionalToken = getToken(email, password);

              if (!optionalToken.isPresent()) {
                LOGGER.info(String.format("Login request %s rejected. User %s is unauthorized.", id,
                    content.getLogin().getEmail()));
                replyUnauthorized(id);
                return;
              }

              replyAuthorized(id, optionalToken.get());

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
