package io.github.cloudiator.management.user.domain;


import com.google.inject.persist.Transactional;
import de.uniulm.omi.cloudiator.util.Password;
import de.uniulm.omi.cloudiator.util.configuration.Configuration;
import io.github.cloudiator.persistance.UserDomainRepository;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthService {

  private HashMap<String, Entry<User, Token>> tokenTable;
  private boolean initialized = false;


  private final UserDomainRepository userDomainRepository;

  private long ttlMillis = 604800000; // 7 days

  @Inject
  public AuthService(UserDomainRepository userDomainRepository) {
    this.userDomainRepository = userDomainRepository;
    init();
  }

  @Transactional
  void init() {
    if (initialized) {
      throw new IllegalStateException("Already initialized.");
    }
    final String configMode = Configuration.conf().getString("auth.mode");
    System.out.println("\nreading auth.mode: " + configMode);

    // HashMap with StringToken as Key and Pair<User,Token> as value
    this.tokenTable = new HashMap<String, Entry<User, Token>>();

    if (configMode.matches("testmode")) {
      System.out.println("---- starting testmode ----");
      User userfortest = null;
      if (this.userDomainRepository.exists("testuser")) {
        System.out.println("testuser already exists. ");
      } else {
        //setUp TestUser
        System.out.println("Testuser will be generated. ");
        byte[] salt = Password.getInstance().generateSalt();
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String pwd = "passwordfortestuser";
        String hashed = new String(Password.getInstance().hash(pwd.toCharArray(), salt));
        Tenant tenant = new Tenant("admin");

        userfortest = new User("testuser", hashed,
            encodedSalt, tenant);
        this.userDomainRepository.addUser(userfortest);


      }
      userfortest = this.userDomainRepository.findUserByMail("testuser");
      System.out
          .println("TestUser: "
              + "\nEmail: " + userfortest.getEmail()
              + "\nPw: passwordfortestuser " + "\nTenant: " + userfortest.getTenant().getName());
      final String authToken = Configuration.conf().getString("auth.token");
      Token tokenfortest = new Token(authToken, "testuser", System.currentTimeMillis(),
          System.currentTimeMillis() + ttlMillis);
      tokenTable.put(authToken, new SimpleEntry<User, Token>(userfortest, tokenfortest));
      System.out
          .println("\nToken erzeugt:\n----- \nToken: " + tokenfortest.getStingToken() + " \nUser: "
              + tokenfortest.getOwner() + "\nIssued at: " + new Date(tokenfortest.getIssuedAt())
              + "\nExpires: " + new Date(tokenfortest.getExpires()) + "\n------");

    } else {
      System.out.println("---- starting normalmode ----");
      System.out.println("\nTOKEN: " + Configuration.conf().getString("auth.token")
          + "\nTOKEN will be ignored");
      if (this.userDomainRepository.exists("testuser")) {
        User del = this.userDomainRepository.findUserByMail("testuser");
        this.userDomainRepository.deleteUser(del);
        System.out.println("\ntestuser deleted ");
      }
    }
    this.initialized = true;
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
