package io.github.cloudiator.management.user.converter;

import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import io.github.cloudiator.management.user.domain.Token;
import org.cloudiator.messages.entities.UserEntities;

public class TokenConverter implements TwoWayConverter<Token, UserEntities.Token> {

  @Override
  public Token applyBack(UserEntities.Token protoToken) {
    Token token = new Token(protoToken.getToken(), null, null);
    return token;
  }

  @Override
  public UserEntities.Token apply(Token domainToken) {
    UserEntities.Token.Builder builder = UserEntities.Token.newBuilder()
        .setToken(domainToken.getToken());
    return builder.build();
  }
}
