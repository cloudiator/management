package io.github.cloudiator.persistance;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import io.github.cloudiator.util.JpaContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 31.05.17.
 */
public class JpaModule extends AbstractModule {

  private final String jpaUnit;
  private final JpaContext jpaContext;

  public JpaModule(String jpaUnit, JpaContext jpaContext) {
    this.jpaUnit = jpaUnit;
    this.jpaContext = jpaContext;
  }

  @Override
  protected void configure() {
    install(buildPersistModule());
    bind(TenantModelRepository.class).to(TenantModelRepositoryJpa.class);
    bind(SecureEntryModelRepository.class).to(SecureEntryModelRepositoryJpa.class);
  }

  private JpaPersistModule buildPersistModule() {
    final JpaPersistModule jpaPersistModule = new JpaPersistModule(jpaUnit);
    Map<String, String> config = new HashMap<>();
    config.put("hibernate.dialect", jpaContext.dialect());
    config.put("javax.persistence.jdbc.driver", jpaContext.driver());
    config.put("javax.persistence.jdbc.url", jpaContext.url());
    config.put("javax.persistence.jdbc.user", jpaContext.user());
    config.put("javax.persistence.jdbc.password", jpaContext.password());
    jpaPersistModule.properties(config);
    return jpaPersistModule;
  }
}
