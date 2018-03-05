package io.github.cloudiator.management.user.domain;

import de.uniulm.omi.cloudiator.util.Password;
import io.github.cloudiator.management.user.converter.TokenConverter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.inject.Singleton;

@Singleton
public class AuthService {

  private final HashMap<String, Entry<User, Token>> tokenTable;

  private long ttlMillis = 604800000; // 7 days

  public AuthService() {
    // HashMap with StringToken as Key and Pair<User,Token> as value
    this.tokenTable = new HashMap<String, Entry<User, Token>>();
    User userforTest = new User("admin", "password", "salt", new Tenant("admin"));
    Token tokenforTest = new Token("secret", "admin", System.currentTimeMillis(),
        System.currentTimeMillis() + ttlMillis);
    tokenTable.put("secret", new SimpleEntry<User, Token>(userforTest, tokenforTest));
  }

  public Entry<User, Token> getToken(Token contentToken) {
    //not existing
    if (!tokenTable.containsKey(contentToken.getStingToken())) {
      return new SimpleEntry<User, Token>(null, contentToken);
    }
    Entry<User, Token> tableEntry = tokenTable.get(contentToken.getStingToken());

    return tableEntry;

  }

  public Token createNewToken(User user) {

    long issued = System.currentTimeMillis();
    long expired = issued + ttlMillis;
    String stringToken;
    do {
      stringToken = Base64.getEncoder()
          .encodeToString(Password.getInstance().generateToken().getBytes());
    } while (tokenTable.containsKey(stringToken));
    Token token = new Token(stringToken, user.getEmail(), issued, expired);
    tokenTable.put(stringToken, new SimpleEntry<>(user, token));

    return token;
  }

  public Token removeToken(String stringToken) {
    if (!tokenTable.containsKey(stringToken)) {
      throw new IllegalArgumentException("Token is unkown: " + stringToken);
    }
    Entry<User, Token> removed = tokenTable.remove(stringToken);
    return removed.getValue();
  }

  public ArrayList<Token> getAllTokens() {
    ArrayList<Token> result = new ArrayList<>();

    for (Entry<User, Token> entry : tokenTable.values()) {
      result.add(entry.getValue());
    }

    return result;
  }

}
