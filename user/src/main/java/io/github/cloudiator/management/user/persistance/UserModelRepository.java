package io.github.cloudiator.management.user.persistance;


import io.github.cloudiator.persistance.DomainRepository;
import java.util.Optional;

public interface UserModelRepository extends DomainRepository<UserModel> {

  Optional<UserModel> findUserByMail(String mail);


  void setUserTenant(String email, String tenant);

}
