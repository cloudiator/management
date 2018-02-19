package io.github.cloudiator.management.user.messaging;

import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import de.uniulm.omi.cloudiator.util.Password;
import io.github.cloudiator.management.user.converter.LoginConverter;
import io.github.cloudiator.management.user.domain.AuthService;
import io.github.cloudiator.management.user.domain.Token;
import io.github.cloudiator.management.user.domain.User;
import io.github.cloudiator.management.user.persistance.UserDomainRepository;
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

            LoginResponse.Builder responseBuilder = LoginResponse.newBuilder();

            //convert to domain object
            User contentUser = loginConverter.applyBack(content.getLogin());
            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());

            try {

              //validate to database
              User databaseUser = userDomainRepository.findUserByMail(contentUser.getEmail());
              if (!passwordUtil.check(contentUser.getPassword().toCharArray(),
                  databaseUser.getPassword().toCharArray(), databaseUser.getSalt().getBytes())) {
                messagingInterface
                    .reply(LoginResponse.class, id,
                        Error.newBuilder().setCode(400).setMessage("Password does not match.")
                            .build());
                entityManager.get().getTransaction().rollback();
                return;
              }

              //create Token
              Token yourToken = authService.createNewToken(databaseUser.getEmail());
              //reply

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
