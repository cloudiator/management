package io.github.cloudiator.management.user.domain;

import de.uniulm.omi.cloudiator.util.Password;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class AuthService {

  private final HashMap<String, Token> tokenTable;

  public AuthService() {
    this.tokenTable = new HashMap<String, Token>();
  }

  public boolean checkToken(String email, String token) {
    if (!tokenTable.containsKey(email)) {
      throw new IllegalArgumentException("User is unkown: " + email);
    }
    Token actualToken = tokenTable.get(email);

    return token.equals(actualToken.getToken());
  }

  public Token createNewToken(String email) {
    Date actual = new Date();
    Timestamp generationTime = new Timestamp(actual.getTime());
    String stringToken = Password.getInstance().generateToken();
    Token token = new Token(stringToken, email, generationTime);

    tokenTable.put(email, token);

    return token;
  }

  public Token removeToken(String email) {
    if (!tokenTable.containsKey(email)) {
      throw new IllegalArgumentException("User is unkown: " + email);
    }
    Token removed = tokenTable.remove(email);
    return removed;
  }

  public ArrayList<Token> getAllTokens() {
    ArrayList<Token> result = new ArrayList<>();

    for (Token tok : tokenTable.values()) {
      result.add(tok);
    }

    return result;
  }

}
