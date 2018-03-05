package io.github.cloudiator.management.user.messaging;

import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import de.uniulm.omi.cloudiator.util.Password;
import io.github.cloudiator.management.user.converter.LoginConverter;
import io.github.cloudiator.management.user.converter.TokenConverter;
import io.github.cloudiator.management.user.converter.UserConverter;
import io.github.cloudiator.management.user.domain.AuthService;
import io.github.cloudiator.management.user.domain.Token;
import io.github.cloudiator.management.user.domain.User;
import io.github.cloudiator.persistance.UserDomainRepository;
import java.util.Base64;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.LoginRequest;
import org.cloudiator.messages.entities.User.LoginResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class LoginListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final LoginConverter loginConverter = new LoginConverter();
  private final UserDomainRepository userDomainRepository;
  private final UnitOfWork unitOfWork;
  private final Provider<EntityManager> entityManager;
  private final Password passwordUtil;
  private AuthService authService;
  private final TokenConverter tokenConverter;
  private final UserConverter userConverter;

  @Inject
  public LoginListener(MessageInterface messagingInterface,
      UserDomainRepository userDomainRepository, UnitOfWork unitOfWork,
      Provider<EntityManager> entityManager, AuthService authService) {
    this.messagingInterface = messagingInterface;
    this.userDomainRepository = userDomainRepository;
    this.unitOfWork = unitOfWork;
    this.entityManager = entityManager;
    this.passwordUtil = Password.getInstance();
    this.authService = authService;
    this.tokenConverter = new TokenConverter();
    this.userConverter = new UserConverter();
  }

  @Override
  public void run() {

    messagingInterface.subscribe(LoginRequest.class, LoginRequest.parser(),
        new MessageCallback<LoginRequest>() {
          @Override
          public void accept(String id, LoginRequest content) {
            //start the transaction
            unitOfWork.begin();
            entityManager.get().getTransaction().begin();



            //convert to domain object
            User contentUser = loginConverter.applyBack(content.getLogin());
            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());
            System.out.println("User: " + contentUser);

            try {
              LoginResponse.Builder responseBuilder = LoginResponse.newBuilder();

              //validate to database
              User databaseUser = userDomainRepository.findUserByMail(contentUser.getEmail());
              System.out.println(
                  "DB: " + databaseUser.toString() + " pw: " + databaseUser.getPassword() + " salt "
                      + databaseUser.getSalt());

              byte[] salt = Base64.getDecoder().decode(databaseUser.getSalt());

              boolean erg;
              erg = passwordUtil.check(contentUser.getPassword().toCharArray(),
                  databaseUser.getPassword().toCharArray(), salt);
              System.out.println("Password: " + erg);
              if (!erg) {
                System.out.println(
                    "passwordError: " + databaseUser + "content: " + contentUser.getPassword());
                messagingInterface
                    .reply(LoginResponse.class, id,
                        Error.newBuilder().setCode(400)
                            .setMessage(
                                "Password does not match. " + contentUser.getPassword() + "db: "
                                    + databaseUser.getPassword())
                            .build());
                entityManager.get().getTransaction().rollback();
                //return;
              }

              //create Token
              Token newToken = authService.createNewToken(databaseUser);
              System.out.println("\n " + newToken);
              //creating reply
              responseBuilder
                  .setToken(tokenConverter.apply(newToken))
                  .setUser(userConverter.apply(databaseUser));
              //success
              messagingInterface.reply(id, responseBuilder.build());
              entityManager.get().getTransaction().commit();
            } catch (IllegalStateException ill) {

              //error
              messagingInterface
                  .reply(LoginResponse.class, id,
                      Error.newBuilder().setCode(400).setMessage(ill.getMessage()).build());
              entityManager.get().getTransaction().rollback();
            } finally {
              unitOfWork.end();
            }

          }
        }
    );

  }
}
