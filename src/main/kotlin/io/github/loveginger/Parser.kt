package io.github.loveginger

/**
 * Recursive Descent Parser
 */
class Parser(private val tokens: List<Token>) {
  private class ParseError : RuntimeException()

  private var current = 0

  //  expression -> equality ;
  //  equality -> comparison (("!=" | "==") comparison)* ;
  //  comparison -> addition ((">" | ">=" | "<" | <=) addition)* ;
  //  addition -> multiplication (("-" | "+") multiplication)* ;
  //  multiplication -> unary (("/" | "*") unary)* ;
  //  unary -> ("!" | "-") unary | primary ;
  //  primary -> NUMBER | STRING | "false" | "true" | "nil" | "(" expression ")" ;

  fun parse(): Expr? {
    return try {
      expression()
    } catch (e: ParseError) {
      null
    }
  }

  private fun expression(): Expr {
    return equality()
  }

  private fun equality(): Expr {
    var expr = comparison()

    while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      val operator = previous()
      val right = comparison()
      expr = Expr.Binary(expr, operator, right)
    }

    return expr;
  }

  private fun comparison(): Expr {
    var expr = addition()
    while (match(
        TokenType.GREATER,
        TokenType.GREATER_EQUAL,
        TokenType.LESS,
        TokenType.LESS_EQUAL
      )
    ) {
      val operator = previous()
      val right = addition()
      expr = Expr.Binary(expr, operator, right)
    }
    return expr
  }

  private fun addition(): Expr {
    var expr = multiplication()
    while (match(TokenType.MINUS, TokenType.PLUS)) {
      val operator = previous()
      val right = multiplication()
      expr = Expr.Binary(expr, operator, right)
    }
    return expr
  }

  private fun multiplication(): Expr {
    var expr = unary()
    while (match(TokenType.STAR, TokenType.SLASH)) {
      val operator = previous()
      val right = unary()
      expr = Expr.Binary(expr, operator, right)
    }
    return expr
  }

  private fun unary(): Expr {
    if (match(TokenType.BANG, TokenType.MINUS)) {
      val operator = previous()
      return Expr.Unary(operator, unary())
    }
    return primary()
  }

  private fun primary(): Expr {
    return when {
      match(TokenType.FALSE) -> Expr.Literal(false)
      match(TokenType.TRUE) -> Expr.Literal(true)
      match(TokenType.NIL) -> Expr.Literal(null)
      match(TokenType.NUMBER, TokenType.STRING) -> Expr.Literal(previous().literal)
      match(TokenType.LEFT_PAREN) -> {
        val expr = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
        Expr.Grouping(expr)
      }
      else -> throw error(peek(), "Expect expression.")
    }
  }

  private fun match(vararg tokenTypes: TokenType): Boolean {
    for (tokenType in tokenTypes) {
      if (check(tokenType)) {
        advance()
        return true
      }
    }
    return false
  }

  private fun check(tokenType: TokenType): Boolean {
    if (isAtEnd()) return false
    return peek().type == tokenType
  }

  private fun advance(): Token {
    if (!isAtEnd()) current++
    return previous()
  }

  private fun peek(): Token {
    return tokens[current]
  }

  private fun previous(): Token {
    return tokens[current - 1]
  }

  private fun isAtEnd(): Boolean {
    return peek().type == TokenType.EOF
  }

  private fun consume(tokenType: TokenType, message: String): Token {
    if (check(tokenType)) return advance()
    throw error(peek(), message)
  }

  private fun error(token: Token, message: String): ParseError {
    Lox.error(token, message)
    return ParseError()
  }

  private fun synchronize() {
    advance()

    while (!isAtEnd()) {
      if (previous().type == TokenType.SEMICOLON) break
      when (peek().type) {
        TokenType.CLASS, TokenType.VAR, TokenType.FUN, TokenType.WHILE, TokenType.FOR,
        TokenType.IF, TokenType.PRINT, TokenType.RETURN -> return
        else -> advance()
      }
    }
  }
}
