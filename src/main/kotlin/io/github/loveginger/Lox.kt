package io.github.loveginger

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.system.exitProcess

private var hadError = false

fun main(args: Array<String>) {
  when (args.size) {
    0 -> runPrompt()
    1 -> runFile(args[0])
    else -> {
      println("Usage: jlox [script]")
      exitProcess(64)
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

  if (hadError) {
    exitProcess(65)
  }
}


private fun run(source: String) {
  val scanner = Scanner(source)
  val tokens = scanner.scanTokens()

  for (token in tokens) {
    println(token)
  }
}

fun error(line: Int, message: String) {
  report(line, "", message)
}

private fun report(line: Int, where: String, message: String) {
  System.err.println("[line $line] Error$where: $message")
  hadError = true
}