package io.github.cloudiator.management.user.persistance;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import io.github.cloudiator.management.user.domain.User;

public class UserDomainRepository {

  private final UserModelRepository userModelRepository;

  @Inject
  public UserDomainRepository(
      UserModelRepository userModelRepository) {
    this.userModelRepository = userModelRepository;
  }


  public void addUser(User user) {
    checkNotNull(user, "user is null");
    checkState(!exists(user.getEmail()), "mail already exists.");



  }

  public boolean exists(String mail) {
    checkNotNull(mail, "mail is null");
    return userModelRepository.findUserByMail(mail).isPresent();
  }


}
