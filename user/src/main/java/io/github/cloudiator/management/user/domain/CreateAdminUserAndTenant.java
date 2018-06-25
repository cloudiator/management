package io.github.cloudiator.management.user.domain;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import de.uniulm.omi.cloudiator.util.Password;

public class CreateAdminUserAndTenant {

  private final AuthenticationService authenticationService;

  private static final Password PASSWORD = Password.getInstance();

  private static final String ADMIN_PASSWORD = "admin";
  private static final String ADMIN_TENANT = "admin";
  private static final UserNew ADMIN_USER = new UserNew("john.doe@example.com",
      new Tenant(ADMIN_TENANT),
      ADMIN_PASSWORD);


  @Inject
  public CreateAdminUserAndTenant(
      AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;

    execute();

  }

  @Transactional
  void execute() {
    if (!authenticationService.getTenant(ADMIN_PASSWORD).isPresent()) {
      this.authenticationService.createTenant(ADMIN_TENANT);
    }
    if (!authenticationService.getUser("john.doe@example.com").isPresent()) {
      this.authenticationService.createUser(ADMIN_USER);
    }
  }


}
