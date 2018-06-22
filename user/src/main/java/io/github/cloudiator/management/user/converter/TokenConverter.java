package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.Token;
import io.github.cloudiator.management.user.domain.TokenGenerator;
import org.cloudiator.messages.entities.UserEntities;

public class TokenConverter implements TwoWayConverter<Token, UserEntities.Token> {

  public static final TokenConverter INSTANCE = new TokenConverter();

  private TokenConverter() {

  }

  @Override
  public Token applyBack(UserEntities.Token protoToken) {
    Token token = new Token(protoToken.getToken(), protoToken.getUserEmail(),
        protoToken.getGenerationTime(), protoToken.getExpireTime());
    return token;
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
