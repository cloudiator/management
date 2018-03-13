package io.github.cloudiator.management.user.messaging;

import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import io.github.cloudiator.management.user.converter.TokenConverter;
import io.github.cloudiator.management.user.converter.UserConverter;
import io.github.cloudiator.management.user.domain.AuthService;
import io.github.cloudiator.management.user.domain.Token;
import io.github.cloudiator.management.user.domain.User;
import java.util.Map.Entry;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.AuthRequest;
import org.cloudiator.messages.entities.User.AuthResponse;
import org.cloudiator.messages.entities.UserEntities;

import org.cloudiator.messages.entities.UserEntities.Tenant;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class AuthListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final UnitOfWork unitOfWork;
  private final Provider<EntityManager> entityManager;
  private final AuthService authService;
  private final TokenConverter tokenConverter;
  private final UserConverter userConverter;

  @Inject
  public AuthListener(MessageInterface messagingInterface,
      UnitOfWork unitOfWork, Provider<EntityManager> entityManager, AuthService authService) {
    this.messagingInterface = messagingInterface;
    this.unitOfWork = unitOfWork;
    this.entityManager = entityManager;
    this.authService = authService;
    this.tokenConverter = new TokenConverter();
    this.userConverter = new UserConverter();
  }


  @Override
  public void run() {

    messagingInterface.subscribe(AuthRequest.class, AuthRequest.parser(),
        new MessageCallback<AuthRequest>() {
          @Override
          public void accept(String id, AuthRequest content) {
            unitOfWork.begin();
            entityManager.get().getTransaction().begin();

            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());
            AuthResponse.Builder response = AuthResponse.newBuilder();
            //convert to domain object
            Token contentToken = tokenConverter.applyBack(content.getToken());

            //get from TokenTable
            Entry<User, Token> tableEntry = authService.getToken(contentToken);
            // Token not in Table
            if (tableEntry.getKey() == null) {
              messagingInterface.reply(AuthResponse.class, id,
                  Error.newBuilder().setMessage("Token not listed").build());
              entityManager.get().getTransaction().rollback();
              unitOfWork.end();
              return;
            }
            //check Token:
            Long now = System.currentTimeMillis();
            // Token is expired
            if (tableEntry.getValue().getExpires() <= now) {
              //error
              messagingInterface.reply(AuthResponse.class, id,
                  Error.newBuilder().setMessage("Token expired").build());
              entityManager.get().getTransaction().rollback();
              unitOfWork.end();
              return;
            }
            //create reply
            UserEntities.User feedback = userConverter.apply(tableEntry.getKey());
            response.setUser(feedback);
            //success
            messagingInterface.reply(id, response.build());
            entityManager.get().getTransaction().commit();
            unitOfWork.end();
          }
        }
    );
  }
}

