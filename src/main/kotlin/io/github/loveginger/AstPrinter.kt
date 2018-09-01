package io.github.loveginger

class AstPrinter : Expr.Visitor<String> {

  fun print(expr: Expr): String {
    return expr.accept(this)
  }

  override fun visitBinaryExpr(binary: Expr.Binary): String {
    return parenthesize(binary.operator.lexeme, binary.left, binary.right)
  }

  override fun visitGroupingExpr(grouping: Expr.Grouping): String {
    return parenthesize("group", grouping.expression)
  }

  override fun visitLiteralExpr(literal: Expr.Literal): String {
    if (literal.value == null) return "nil"
    return literal.value.toString()
  }

  override fun visitUnaryExpr(unary: Expr.Unary): String {
    return parenthesize(unary.operator.lexeme, unary.right)
  }

  private fun parenthesize(name: String, vararg exprs: Expr): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append("($name")
    for (expr in exprs) {
      stringBuilder.append(" ").append(print(expr))
    }
    stringBuilder.append(")")
    return stringBuilder.toString()
  }
}

fun main(args: Array<String>) {
  val astPrinter = AstPrinter()
  // (* (- 123) (group 45.67))
  val expr = Expr.Binary(
    Expr.Unary(Token(TokenType.MINUS, "-", null, 1), Expr.Literal("123")),
    Token(TokenType.STAR, "*", null, 1),
    Expr.Grouping(Expr.Literal("45.67"))
  )
  println(astPrinter.print(expr))
}