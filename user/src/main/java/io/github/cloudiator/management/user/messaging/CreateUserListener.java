package io.github.cloudiator.management.user.messaging;

import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import io.github.cloudiator.management.user.converter.UserConverter;
import io.github.cloudiator.management.user.converter.UserNewConverter;
import io.github.cloudiator.management.user.domain.User;
import io.github.cloudiator.management.user.domain.UserNew;
import io.github.cloudiator.management.user.persistance.UserDomainRepository;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.CreateUserRequest;
import org.cloudiator.messages.entities.User.CreateUserResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class CreateUserListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final UserNewConverter userNewConverter = new UserNewConverter();
  private final UserConverter userConverter = new UserConverter();
  private final UserDomainRepository userDomainRepository;
  private final UnitOfWork unitOfWork;
  private final Provider<EntityManager> entityManager;


  @Inject
  public CreateUserListener(MessageInterface messagingInterface,
      UserDomainRepository userDomainRepository, UnitOfWork unitOfWork,
      Provider<EntityManager> entityManager) {
    this.messagingInterface = messagingInterface;
    this.userDomainRepository = userDomainRepository;
    this.unitOfWork = unitOfWork;
    this.entityManager = entityManager;
  }

  @Override
  public void run() {

    messagingInterface.subscribe(CreateUserRequest.class, CreateUserRequest.parser(),
        new MessageCallback<CreateUserRequest>() {

          @Override
          public void accept(String id, CreateUserRequest content) {
            //start the transaction
            unitOfWork.begin();
            entityManager.get().getTransaction().begin();

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
              return;
            }

            domainUser.setEmail(requestedUser.getEmail());
            domainUser.setPassword(requestedUser.getPassword());
            domainUser.setTenant(requestedUser.getTenant());

            //store to database
            userDomainRepository.addUser(domainUser);

            //ERROR: Tenant not existing
            //ERROR: User already exists

            //reply
            responseBuilder.setUser(userConverter.apply(domainUser));
            CreateUserResponse response = responseBuilder.build();

            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());

            //success
            messagingInterface.reply(id, response);
            entityManager.get().getTransaction().commit();
/*
            //error
            messagingInterface.reply(CreateUserResponse.class, id, Error.newBuilder().build());
            entityManager.get().getTransaction().rollback();
*/
            unitOfWork.end();
          }
        }
    );

  }
}
