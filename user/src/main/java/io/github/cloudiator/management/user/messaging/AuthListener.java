package io.github.cloudiator.management.user.messaging;

import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import io.github.cloudiator.management.user.domain.AuthService;
import io.github.cloudiator.management.user.persistance.TenantDomainRepository;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.AuthRequest;
import org.cloudiator.messages.entities.User.AuthResponse;
import org.cloudiator.messages.entities.UserEntities.Token;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class AuthListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final UnitOfWork unitOfWork;
  private final Provider<EntityManager> entityManager;

  private AuthService authService;

  @Inject
  public AuthListener(MessageInterface messagingInterface,
      UnitOfWork unitOfWork, Provider<EntityManager> entityManager) {
    this.messagingInterface = messagingInterface;
    this.unitOfWork = unitOfWork;
    this.entityManager = entityManager;
    this.authService = new AuthService();
  }


  @Override
  public void run() {

    messagingInterface.subscribe(AuthRequest.class, AuthRequest.parser(),
        new MessageCallback<AuthRequest>() {
          @Override
          public void accept(String id, AuthRequest content) {
            unitOfWork.begin();
            entityManager.get().getTransaction().begin();

            Token sendedToken = content.getToken();
            sendedToken.getOwner();

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

