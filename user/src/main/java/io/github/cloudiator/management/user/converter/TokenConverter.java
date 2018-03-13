package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.Token;
import org.cloudiator.messages.entities.UserEntities;

public class TokenConverter implements TwoWayConverter<Token, UserEntities.Token> {

  @Override
  public Token applyBack(UserEntities.Token protoToken) {
    Token token = new Token(protoToken.getToken(), protoToken.getUserEmail(),
        protoToken.getGenerationTime(), protoToken.getExpireTime());
    return token;
  }

  @Override
  public UserEntities.Token apply(Token domainToken) {
    UserEntities.Token.Builder builder = UserEntities.Token.newBuilder()
        .setToken(domainToken.getStingToken())
        .setUserEmail(domainToken.getOwner())
        .setGenerationTime(domainToken.getIssuedAt())
        .setExpireTime(domainToken.getExpires());
    return builder.build();
  }
}
