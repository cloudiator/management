package io.github.cloudiator.management.user.domain;

import java.util.function.Function;

public interface TokenGenerator extends Function<String, Token> {

}
