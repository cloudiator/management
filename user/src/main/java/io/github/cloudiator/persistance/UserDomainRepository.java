package io.github.cloudiator.persistance;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.util.Password;
import io.github.cloudiator.management.user.domain.Tenant;
import io.github.cloudiator.management.user.domain.User;
import javax.annotation.Nullable;

public class UserDomainRepository {

  private final UserModelRepository userModelRepository;
  private final TenantModelRepository tenantModelRepository;
  private final Password passwordUtil;

  @Inject
  public UserDomainRepository(
      UserModelRepository userModelRepository, TenantModelRepository tenantModelRepository) {
    this.userModelRepository = userModelRepository;
    this.tenantModelRepository = tenantModelRepository;
    passwordUtil = Password.getInstance();
  }

  @Nullable
  public User findUserByMail(String mail) {

    UserModel databaseUser = userModelRepository.findUserByMail(mail).orElse(null);

    if (databaseUser == null) {
      return null;
    }
    Tenant userTenant = new Tenant(databaseUser.getTenant().getName());
    return new User(databaseUser.getMail(), databaseUser.getPassword(),
        databaseUser.getSalt(), userTenant);
  }


  public void addUser(User user) {
    checkNotNull(user, "user is null");
    checkState(!exists(user.email()), "mail already exists.");
    TenantModel userTenant;
    if (tenantModelRepository.findTenantByName(user.tenant().getName()).isPresent()) {
      userTenant = tenantModelRepository.findTenantByName(user.tenant().getName())
          .get();
    } else {
      userTenant = new TenantModel(user.tenant().getName());
      tenantModelRepository.save(userTenant);
    }
    UserModel userModel = new UserModel(user.email(), user.salt(), user.password(),
        userTenant);

    userModelRepository.save(userModel);
  }

  public boolean exists(String mail) {
    checkNotNull(mail, "mail is null");
    return userModelRepository.findUserByMail(mail).isPresent();
  }

  public void setUserTenant(String email, String tenant) {

  }

  public void deleteUser(User user) {
    checkNotNull(user, "user is null");
    checkState(exists(user.email()), "user does not exists.");
    UserModel candidate = userModelRepository.findUserByMail(user.email()).get();
    userModelRepository.delete(candidate);

  }


}
