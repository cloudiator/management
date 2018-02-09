package io.github.cloudiator.management.user.persistance;


import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import de.uniulm.omi.cloudiator.persistance.repositories.BaseModelRepositoryJpa;
import de.uniulm.omi.cloudiator.persistance.util.JpaResultHelper;
import java.util.Optional;
import javax.persistence.EntityManager;

public class JpaUserModelRepository extends BaseModelRepositoryJpa<UserModel> implements
    UserModelRepository {

  @Inject
  protected JpaUserModelRepository(
      Provider<EntityManager> entityManager,
      TypeLiteral<UserModel> type) {
    super(entityManager, type);
  }

  @Override
  public Optional<UserModel> findUserByMail(String mail) {

    String query = String.format("from %s where mail=:mail", type.getName());
    final UserModel userModel = (UserModel) JpaResultHelper
        .getSingleResultOrNull(em().createQuery(query).setParameter("mail", mail));
    return Optional.ofNullable(userModel);
  }


  @Override
  public void setUserTenant(String email, String tenant) {

  }
}
