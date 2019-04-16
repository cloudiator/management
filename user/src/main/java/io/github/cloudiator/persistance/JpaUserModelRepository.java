package io.github.cloudiator.persistance;


import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import io.github.cloudiator.util.JpaResultHelper;
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
    throw new UnsupportedOperationException();
  }
}
