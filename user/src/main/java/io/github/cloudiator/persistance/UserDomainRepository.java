package io.github.cloudiator.persistance;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.util.Password;
import io.github.cloudiator.management.user.domain.Tenant;
import io.github.cloudiator.management.user.domain.User;
import io.github.cloudiator.management.user.domain.UserNew;

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

  public User findUserByMail(String mail) {
    checkState(exists(mail), "Email does not exist. " + mail);
    UserModel databaseUser = userModelRepository.findUserByMail(mail).get();
    Tenant userTenant = new Tenant(databaseUser.getTenant().getName());
    User dbBack = new User(databaseUser.getMail(), databaseUser.getPassword(),
        databaseUser.getSalt(), userTenant);
    System.out.println("got it: " + dbBack);
    return dbBack;
  }


  public void addUser(User user) {
    checkNotNull(user, "user is null");
    checkState(!exists(user.getEmail()), "mail already exists.");
    TenantModel userTenant;
    if (tenantModelRepository.findTenantByName(user.getTenant().getName()).isPresent()) {
      userTenant = tenantModelRepository.findTenantByName(user.getTenant().getName())
          .get();
    } else {
      userTenant = new TenantModel(user.getTenant().getName());
      tenantModelRepository.save(userTenant);
    }
    UserModel userModel = new UserModel(user.getEmail(), user.getSalt(), user.getPassword(),
        userTenant);

    userModelRepository.save(userModel);
  }

  public boolean exists(String mail) {
    checkNotNull(mail, "mail is null");
    return userModelRepository.findUserByMail(mail).isPresent();
  }

  public void setUserTenant(String email, String tenant) {

  }


}
