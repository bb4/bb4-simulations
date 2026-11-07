// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

/**
  * A tiny JSON model, parser and pretty printer.
  * It exists so the benchmark results file can be read and rewritten without adding a library dependency
  * (the benchmark must run against different versions of bb4-optimization without dependency surprises).
  * Only what the results file needs is supported, but it is a complete JSON grammar.
  */
sealed trait Json

object Json {

  case object JNull extends Json
  final case class JBool(value: Boolean) extends Json
  final case class JNum(value: Double) extends Json
  final case class JStr(value: String) extends Json
  final case class JArr(items: List[Json]) extends Json
  final case class JObj(fields: List[(String, Json)]) extends Json {
    def get(name: String): Option[Json] = fields.collectFirst { case (k, v) if k == name => v }
  }

  /** @throws IllegalArgumentException if the text is not valid JSON */
  def parse(text: String): Json = {
    val parser = new Parser(text)
    val value = parser.parseValue()
    parser.skipWhitespace()
    if (!parser.atEnd) throw new IllegalArgumentException(s"Unexpected trailing content at position ${parser.pos}")
    value
  }

  /** @return pretty printed JSON (2 space indent) that ends with a newline */
  def write(json: Json): String = {
    val sb = new StringBuilder
    writeValue(json, sb, 0)
    sb.append('\n')
    sb.toString
  }

  private def indent(sb: StringBuilder, level: Int): Unit = {
    var i = 0
    while (i < level) {
      sb.append("  ")
      i += 1
    }
  }

  private def writeValue(json: Json, sb: StringBuilder, level: Int): Unit = json match {
    case JNull => sb.append("null")
    case JBool(b) => sb.append(if (b) "true" else "false")
    case JNum(n) => sb.append(formatNumber(n))
    case JStr(s) => writeString(s, sb)
    case JArr(items) =>
      if (items.isEmpty) sb.append("[]")
      else {
        sb.append("[\n")
        items.zipWithIndex.foreach { case (item, i) =>
          indent(sb, level + 1)
          writeValue(item, sb, level + 1)
          if (i < items.size - 1) sb.append(',')
          sb.append('\n')
        }
        indent(sb, level)
        sb.append(']')
      }
    case JObj(fields) =>
      if (fields.isEmpty) sb.append("{}")
      else {
        sb.append("{\n")
        fields.zipWithIndex.foreach { case ((key, value), i) =>
          indent(sb, level + 1)
          writeString(key, sb)
          sb.append(": ")
          writeValue(value, sb, level + 1)
          if (i < fields.size - 1) sb.append(',')
          sb.append('\n')
        }
        indent(sb, level)
        sb.append('}')
      }
  }

  private def formatNumber(n: Double): String =
    if (n == Math.rint(n) && Math.abs(n) < 1.0e15) n.toLong.toString else n.toString

  private def writeString(s: String, sb: StringBuilder): Unit = {
    sb.append('"')
    s.foreach {
      case '"' => sb.append("\\\"")
      case '\\' => sb.append("\\\\")
      case '\n' => sb.append("\\n")
      case '\r' => sb.append("\\r")
      case '\t' => sb.append("\\t")
      case c if c < ' ' => sb.append("\\u%04x".format(c.toInt))
      case c => sb.append(c)
    }
    sb.append('"')
  }

  private final class Parser(text: String) {
    var pos: Int = 0

    def atEnd: Boolean = pos >= text.length

    def skipWhitespace(): Unit =
      while (!atEnd && Character.isWhitespace(text.charAt(pos))) pos += 1

    private def fail(message: String): IllegalArgumentException =
      new IllegalArgumentException(s"$message at position $pos")

    private def expect(c: Char): Unit = {
      if (atEnd || text.charAt(pos) != c) throw fail(s"Expected '$c'")
      pos += 1
    }

    def parseValue(): Json = {
      skipWhitespace()
      if (atEnd) throw fail("Unexpected end of input")
      text.charAt(pos) match {
        case '{' => parseObject()
        case '[' => parseArray()
        case '"' => JStr(parseString())
        case 't' => parseLiteral("true", JBool(true))
        case 'f' => parseLiteral("false", JBool(false))
        case 'n' => parseLiteral("null", JNull)
        case _ => parseNumber()
      }
    }

    private def parseLiteral(literal: String, value: Json): Json = {
      if (!text.startsWith(literal, pos)) throw fail(s"Expected '$literal'")
      pos += literal.length
      value
    }

    private def parseNumber(): Json = {
      val start = pos
      while (!atEnd && "+-0123456789.eE".indexOf(text.charAt(pos)) >= 0) pos += 1
      if (start == pos) throw fail("Unexpected character")
      try JNum(text.substring(start, pos).toDouble)
      catch {
        case _: NumberFormatException => throw fail("Invalid number")
      }
    }

    private def parseString(): String = {
      expect('"')
      val sb = new StringBuilder
      var done = false
      while (!done) {
        if (atEnd) throw fail("Unterminated string")
        val c = text.charAt(pos)
        pos += 1
        if (c == '"') done = true
        else if (c == '\\') {
          if (atEnd) throw fail("Unterminated escape")
          val e = text.charAt(pos)
          pos += 1
          e match {
            case '"' => sb.append('"')
            case '\\' => sb.append('\\')
            case '/' => sb.append('/')
            case 'b' => sb.append('\b')
            case 'f' => sb.append('\f')
            case 'n' => sb.append('\n')
            case 'r' => sb.append('\r')
            case 't' => sb.append('\t')
            case 'u' =>
              if (pos + 4 > text.length) throw fail("Truncated unicode escape")
              val code =
                try Integer.parseInt(text.substring(pos, pos + 4), 16)
                catch {
                  case _: NumberFormatException => throw fail("Invalid unicode escape")
                }
              sb.append(code.toChar)
              pos += 4
            case _ => throw fail("Invalid escape")
          }
        } else sb.append(c)
      }
      sb.toString
    }

    private def parseArray(): Json = {
      expect('[')
      skipWhitespace()
      val items = scala.collection.mutable.ListBuffer[Json]()
      if (!atEnd && text.charAt(pos) == ']') {
        pos += 1
        JArr(Nil)
      } else {
        var more = true
        while (more) {
          items += parseValue()
          skipWhitespace()
          if (atEnd) throw fail("Unterminated array")
          text.charAt(pos) match {
            case ',' => pos += 1
            case ']' =>
              pos += 1
              more = false
            case _ => throw fail("Expected ',' or ']'")
          }
        }
        JArr(items.toList)
      }
    }

    private def parseObject(): Json = {
      expect('{')
      skipWhitespace()
      val fields = scala.collection.mutable.ListBuffer[(String, Json)]()
      if (!atEnd && text.charAt(pos) == '}') {
        pos += 1
        JObj(Nil)
      } else {
        var more = true
        while (more) {
          skipWhitespace()
          val key = parseString()
          skipWhitespace()
          expect(':')
          fields += ((key, parseValue()))
          skipWhitespace()
          if (atEnd) throw fail("Unterminated object")
          text.charAt(pos) match {
            case ',' => pos += 1
            case '}' =>
              pos += 1
              more = false
            case _ => throw fail("Expected ',' or '}'")
          }
        }
        JObj(fields.toList)
      }
    }
  }
}
