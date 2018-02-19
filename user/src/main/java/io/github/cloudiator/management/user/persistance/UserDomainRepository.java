package io.github.cloudiator.management.user.persistance;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.util.Password;
import io.github.cloudiator.management.user.domain.Tenant;
import io.github.cloudiator.management.user.domain.User;
import javassist.bytecode.ByteArray;

public class UserDomainRepository {

  private final UserModelRepository userModelRepository;
  private final Password passwordUtil;

  @Inject
  public UserDomainRepository(
      UserModelRepository userModelRepository) {
    this.userModelRepository = userModelRepository;
    passwordUtil = Password.getInstance();
  }

  public User findUserByMail(String mail) {
    checkState(!exists(mail), "Email does not exist.");
    UserModel databaseUser = userModelRepository.findUserByMail(mail).get();
    Tenant userTenant = new Tenant(databaseUser.getTenant());
    User dbBack = new User(databaseUser.getMail(), databaseUser.getPassword(),
        databaseUser.getSalt(), userTenant);
    return dbBack;
  }


  public void addUser(User user) {
    checkNotNull(user, "user is null");
    checkState(!exists(user.getEmail()), "mail already exists.");
    String salt = passwordUtil.generateSalt().toString();
    String hashed = passwordUtil.hash(user.getPassword().toCharArray(), salt.getBytes()).toString();

    UserModel newUser = new UserModel(user.getEmail(), salt, hashed,
        user.getTenant().getName());
    userModelRepository.save(newUser);
  }

  public boolean exists(String mail) {
    checkNotNull(mail, "mail is null");
    return userModelRepository.findUserByMail(mail).isPresent();
  }


}
