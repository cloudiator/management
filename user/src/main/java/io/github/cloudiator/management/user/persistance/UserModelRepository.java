package io.github.cloudiator.management.user.persistance;

import de.uniulm.omi.cloudiator.persistance.repositories.ModelRepository;
import java.util.Optional;

public interface UserModelRepository extends ModelRepository<UserModel> {

  Optional<UserModel> findUserByMail(String mail);

}
