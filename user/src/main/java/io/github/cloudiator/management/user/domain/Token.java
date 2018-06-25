package io.github.cloudiator.management.user.domain;


import java.sql.Date;

public class Token {

  private String token;
  private String owner;
  private long issuedAt;
  private long expiresAt;


  public Token(String stringToken, String owner, long issuedAt, long expires) {
    this.token = stringToken;
    this.issuedAt = issuedAt;
    this.expiresAt = expires;
    this.owner = owner;
  }

  public String getStringToken() {
    return token;
  }

  public String getOwner() {
    return owner;
  }


  public long getIssuedAt() {
    return issuedAt;
  }

  public long getExpires() {
    return expiresAt;
  }


}
