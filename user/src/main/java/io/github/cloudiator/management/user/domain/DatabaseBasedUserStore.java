package io.github.cloudiator.management.user.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import io.github.cloudiator.persistance.UserDomainRepository;
import java.util.Optional;

public class DatabaseBasedUserStore implements UserStore {

  private final UserDomainRepository userDomainRepository;

  @Inject
  public DatabaseBasedUserStore(
      UserDomainRepository userDomainRepository) {
    this.userDomainRepository = userDomainRepository;
  }

  @Override
  public Optional<User> getUser(String email) {
    checkNotNull(email, "email is null");
    return Optional.ofNullable(userDomainRepository.findUserByMail(email));
  }
}
