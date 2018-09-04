package io.github.loveginger

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

fun main(args: Array<String>) {
  when (args.size) {
    0 -> runPrompt()
    1 -> runFile(args[0])
    else -> {
      println("Usage: jlox [script]")
      System.exit(64)
    }
  }
}

private fun runPrompt() {
  val reader = BufferedReader(InputStreamReader(System.`in`, Charset.forName("UTF-8")))
  while (true) {
    print("> ")
    run(reader.readLine())
  }
}

private fun runFile(file: String) {

  if (Lox.hadError) {
    System.exit(65)
  }
}


private fun run(source: String) {
  val scanner = Scanner(source)
  val tokens = scanner.scanTokens()
  val parser = Parser(tokens)
  val expression = parser.parse()
  if(Lox.hadError) {
    return
  }
  println(AstPrinter().print(expression!!))
}

object Lox {
  var hadError = false

  fun error(line: Int, message: String) {
    report(line, "", message)
  }

  fun error(token: Token, message: String) {
    if (token.type === TokenType.EOF) {
      report(token.line, " at end", message)
    } else {
      report(token.line, " at '${token.lexeme}'", message)
    }
  }

  private fun report(line: Int, where: String, message: String) {
    System.err.println("[line $line] Error$where: $message")
    hadError = true
  }
}
