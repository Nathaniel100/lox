package io.github.loveginger.tool

import java.io.PrintWriter


fun main(args: Array<String>) {
  if (args.size != 1) {
    System.err.println("Usage: generate_ast <output directory>")
    System.exit(1)
  }
  val outputDir = args[0]
  defineAst(
    outputDir, "Expr", arrayListOf(
      "Binary: Expr left, Token operator, Expr right",
      "Grouping: Expr expression",
      "Literal: Any? value",
      "Unary: Token operator, Expr right"
    )
  )
}

private fun defineAst(outputDir: String, baseName: String, types: ArrayList<String>) {
  val path = "$outputDir/$baseName.kt"
  val writer = PrintWriter(path, "UTF-8")

  writer.println("/* Generate Automatically */")
  writer.println("package io.github.loveginger")
  writer.println()
  writer.println("abstract class $baseName {")

  // Visitor Interface
  defineVisitor(writer, baseName, types)

  // Abstract accept method
  writer.println()
  writer.println("  abstract fun <R> accept(visitor: Visitor<R>): R")
  writer.println()

  // AST Classes
  for (type in types) {
    val className = type.split(":")[0].trim()
    val fields = type.split(":")[1].trim()
    defineType(writer, baseName, className, fields)
  }
  writer.println("}")
  writer.close()
}

private fun defineVisitor(writer: PrintWriter, baseName: String, types: ArrayList<String>) {
  writer.println("  interface Visitor<out R> {")
  writer.println()
  for (type in types) {
    val className = type.split(":")[0].trim()
    writer.println("    fun visit$className$baseName(${className.toLowerCase()}: $className): R")
    writer.println()
  }
  writer.println("  }")
}

private fun defineType(writer: PrintWriter, baseName: String, className: String, fieldList: String) {
  val fieldStringBuilder = StringBuilder()
  val fields = fieldList.split(",")
  for (field in fields) {
    val type = field.trim().split(" ")[0]
    val name = field.trim().split(" ")[1]
    fieldStringBuilder.append("val $name: $type").append(", ")
  }
  val fieldInConstructor = fieldStringBuilder.toString().removeSuffix(", ")
  writer.println("  class $className($fieldInConstructor) : $baseName() {")
  writer.println("    override fun <R> accept(visitor: Visitor<R>) = visitor.visit$className$baseName(this)")
  writer.println("  }")
}
