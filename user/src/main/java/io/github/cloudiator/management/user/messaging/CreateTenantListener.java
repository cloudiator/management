package io.github.cloudiator.management.user.messaging;

import com.google.inject.Provider;
import com.google.inject.internal.ErrorsException;
import com.google.inject.persist.UnitOfWork;
import io.github.cloudiator.management.user.converter.TenantConverter;
import io.github.cloudiator.management.user.converter.UserConverter;
import io.github.cloudiator.management.user.converter.UserNewConverter;
import io.github.cloudiator.management.user.domain.Tenant;
import io.github.cloudiator.persistance.TenantDomainRepository;
import io.github.cloudiator.persistance.UserDomainRepository;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.CreateTenantRequest;
import org.cloudiator.messages.entities.User.CreateTenantResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class CreateTenantListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final TenantConverter tenantConverter = new TenantConverter();
  private final TenantDomainRepository tenantDomainRepository;
  private final UnitOfWork unitOfWork;
  private final Provider<EntityManager> entityManager;

  @Inject
  public CreateTenantListener(MessageInterface messagingInterface,
      TenantDomainRepository tenantDomainRepository, UnitOfWork unitOfWork,
      Provider<EntityManager> entityManager) {
    this.messagingInterface = messagingInterface;
    this.tenantDomainRepository = tenantDomainRepository;
    this.unitOfWork = unitOfWork;
    this.entityManager = entityManager;
  }

  @Override
  public void run() {

    messagingInterface.subscribe(CreateTenantRequest.class, CreateTenantRequest.parser(),
        new MessageCallback<CreateTenantRequest>() {
          @Override
          public void accept(String id, CreateTenantRequest content) {
            //start the transaction
            unitOfWork.begin();
            entityManager.get().getTransaction().begin();

            CreateTenantResponse.Builder responseBuilder = CreateTenantResponse.newBuilder();
            System.out.println("#### RECEIVED MESSAGE #### " + content.toString());
            //convert to domain object
            Tenant domainTenant = new Tenant(content.getTenant());

            try {
              //store to database
              tenantDomainRepository.addTenant(domainTenant);
              responseBuilder.setTenant(tenantConverter.apply(domainTenant));

              //success
              messagingInterface.reply(id, responseBuilder.build());
              entityManager.get().getTransaction().commit();

            } catch (Exception err) {
              //error
              messagingInterface.reply(CreateTenantResponse.class, id,
                  Error.newBuilder().setCode(400).setMessage(err.getMessage()).build());
              entityManager.get().getTransaction().rollback();
            } finally {
              unitOfWork.end();
            }
          }
        });

  }
}
