package io.github.cloudiator.management.user.messaging;

import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import io.github.cloudiator.management.user.converter.TenantConverter;
import io.github.cloudiator.management.user.domain.AuthService;
import io.github.cloudiator.management.user.domain.Tenant;
import io.github.cloudiator.persistance.TenantDomainRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.User.TenantQueryRequest;
import org.cloudiator.messages.entities.User.TenantQueryResponse;
import org.cloudiator.messaging.MessageCallback;
import org.cloudiator.messaging.MessageInterface;

public class TenantQueryListener implements Runnable {

  private final MessageInterface messagingInterface;
  private final TenantDomainRepository tenantDomainRepository;
  private final UnitOfWork unitOfWork;
  private final Provider<EntityManager> entityManager;

  private AuthService authService;
  private final TenantConverter tenantConverter = TenantConverter.INSTANCE;


  @Inject
  public TenantQueryListener(MessageInterface messagingInterface,
      TenantDomainRepository tenantDomainRepository, UnitOfWork unitOfWork,
      Provider<EntityManager> entityManager, AuthService authService) {
    this.messagingInterface = messagingInterface;
    this.tenantDomainRepository = tenantDomainRepository;
    this.unitOfWork = unitOfWork;
    this.entityManager = entityManager;
    this.authService = authService;

  }

  @Override
  public void run() {

    messagingInterface.subscribe(TenantQueryRequest.class, TenantQueryRequest.parser(),
        new MessageCallback<TenantQueryRequest>() {
          @Override
          public void accept(String id, TenantQueryRequest content) {
            //start the transaction
            unitOfWork.begin();
            entityManager.get().getTransaction().begin();

            try {
              TenantQueryResponse.Builder responseBuilder = TenantQueryResponse.newBuilder();
              if (content.getTenantId().isEmpty()) {
                //All Tenants are requested
                for (Tenant tenant : tenantDomainRepository.findAllTenants()) {
                  responseBuilder.addTenant(tenantConverter.apply(tenant));
                }

              } else {
                // single IDQuery
                Tenant requestedTenant = tenantDomainRepository
                    .findTenantbyName(content.getTenantId());
                responseBuilder.addTenant(tenantConverter.apply(requestedTenant));
              }

              messagingInterface.reply(id, responseBuilder.build());
              entityManager.get().getTransaction().commit();
            } catch (IllegalStateException ill) {
              messagingInterface
                  .reply(TenantQueryResponse.class, id,
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
