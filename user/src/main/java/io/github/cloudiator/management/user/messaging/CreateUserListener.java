package io.github.cloudiator.management.user.messaging;

import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import de.uniulm.omi.cloudiator.util.Password;
import io.github.cloudiator.management.user.converter.UserConverter;
import io.github.cloudiator.management.user.converter.UserNewConverter;
import io.github.cloudiator.management.user.domain.User;
import io.github.cloudiator.management.user.domain.UserNew;
import io.github.cloudiator.persistance.TenantDomainRepository;
import io.github.cloudiator.persistance.UserDomainRepository;
import java.util.Base64;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.CreateUserRequest;
import org.cloudiator.messages.entities.User.CreateUserResponse;
import org.cloudiator.messages.entities.User.CreateUserResponse.Builder;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class CreateUserListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final UserNewConverter userNewConverter = new UserNewConverter();
  private final UserConverter userConverter = UserConverter.INSTANCE;
  private final UserDomainRepository userDomainRepository;
  private final TenantDomainRepository tenantDomainRepository;
  private final UnitOfWork unitOfWork;
  private final Provider<EntityManager> entityManager;


  @Inject
  public CreateUserListener(MessageInterface messagingInterface,
      UserDomainRepository userDomainRepository, TenantDomainRepository tenantDomainRepository,
      UnitOfWork unitOfWork,
      Provider<EntityManager> entityManager) {
    this.messagingInterface = messagingInterface;
    this.userDomainRepository = userDomainRepository;
    this.tenantDomainRepository = tenantDomainRepository;
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

            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());

            Builder responseBuilder = CreateUserResponse.newBuilder();
            UserNew requestedUser = userNewConverter.applyBack(content.getNewUser());
            /*--- not here anymore
            //ERROR: PasswordMismatch
            if (!requestedUser.getPasswordRepeat().matches(requestedUser.password())) {
              messagingInterface
                  .reply(CreateUserResponse.class, id,
                      Error.newBuilder().setCode(400).setMessage("PasswordRepeat does not match.")
                          .build());
              entityManager.get().getTransaction().rollback();
              return;
            }
            */
            try {

              //setUp new User
              byte[] salt = Password.getInstance().generateSalt();
              String encodedSalt = Base64.getEncoder().encodeToString(salt);
              String hashed = new String(
                  Password.getInstance().hash(requestedUser.getPassword().toCharArray(), salt));

              User domainUser = new User(requestedUser.getEmail(), hashed,
                  encodedSalt, requestedUser.getTenant());

              //store to database
              userDomainRepository.addUser(domainUser);

              //reply
              responseBuilder.setUser(userConverter.apply(domainUser));
              CreateUserResponse response = responseBuilder.build();

              //success
              messagingInterface.reply(id, response);
              entityManager.get().getTransaction().commit();
            } catch (IllegalStateException ill) {

              messagingInterface.reply(CreateUserResponse.class, id,
                  Error.newBuilder().setCode(400).setMessage(ill.getMessage()).build());
              entityManager.get().getTransaction().rollback();
            } catch (NullPointerException npe) {

              messagingInterface.reply(CreateUserResponse.class, id,
                  Error.newBuilder().setCode(400).setMessage(npe.getMessage()).build());
              entityManager.get().getTransaction().rollback();
            } finally {

              unitOfWork.end();
            }
          }
        }
    );

  }
}
