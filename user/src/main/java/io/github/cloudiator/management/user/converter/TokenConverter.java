package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.OneWayConverter;
import io.github.cloudiator.management.user.domain.Token;
import org.cloudiator.messages.entities.UserEntities;

public class TokenConverter implements OneWayConverter<Token, UserEntities.Token> {

  public static final TokenConverter INSTANCE = new TokenConverter();

  private TokenConverter() {

  }

  @Override
  public UserEntities.Token apply(Token domainToken) {
    UserEntities.Token.Builder builder = UserEntities.Token.newBuilder()
        .setToken(domainToken.getStringToken())
        .setUserEmail(domainToken.getOwner())
        .setGenerationTime(domainToken.getIssuedAt())
        .setExpireTime(domainToken.getExpires());
    return builder.build();
  }
}
