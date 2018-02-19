package io.github.cloudiator.management.user.domain;


import java.sql.Timestamp;

public class Token {

  private String token;
  private String owner;
  private Timestamp generationTime;

  public Token(String token, String owner, Timestamp generationTime) {
    this.token = token;
    this.owner = owner;
    this.generationTime = generationTime;
  }

  public String getToken() {

    return token;
  }

  public String getOwner(){
    return owner;
  }

  public Timestamp getGenerationTime(){
    return generationTime;
  }

}
