package io.github.cloudiator.persistance;


import java.util.Optional;

public interface UserModelRepository extends DomainRepository<UserModel> {

  Optional<UserModel> findUserByMail(String mail);

  void setUserTenant(String email, String tenant);

}
