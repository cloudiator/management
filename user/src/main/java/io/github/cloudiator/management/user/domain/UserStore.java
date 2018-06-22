package io.github.cloudiator.management.user.domain;

import java.util.Optional;

public interface UserStore {

  Optional<User> getUser(String email);

}
