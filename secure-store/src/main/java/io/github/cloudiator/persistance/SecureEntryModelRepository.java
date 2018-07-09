package io.github.cloudiator.persistance;

import javax.annotation.Nullable;

interface SecureEntryModelRepository extends ModelRepository<SecureEntryModel> {

  @Nullable
  SecureEntryModel getEntry(String key, String userId);


}
