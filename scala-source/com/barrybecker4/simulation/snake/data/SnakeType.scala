// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.data


/**
  * Different types of snakes to test.
  * @author Barry Becker
  */
object SnakeType extends Enumeration {

  /** The widths starting at the nose and edging at the tip of the tail  */
  private val BASIC_SNAKE_WIDTHS = Array(9.0, 18.0, 12.0, 13.0, 15.0, 17.0, 18.0, 19.0, 20.0,
    20.5, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 20.0,
    19.5, 19.0, 18.0, 17.0, 16.0, 15.0, 14.0, 13.0, 12.0, 10.0, 8.0, 6.0, 4.0)

  /** The widths starting at the nose and edging at the tip of the tail  */
  private val TEST_SNAKE_WIDTHS = Array(10.0, 17.0, 12.0, 14.0, 16.0, 18.0, 19.1, 20.2, 20.8, 21.0, 21.0,
    21.0, 21.0, 20.0, 19.0, 18.0, 17.0, 16.0, 14.0, 12.0, 10.0, 8.0, 6.0)

  private val BUMP_TROUGH = 14
  private val BUMP_1 = 20
  private val BUMP_2 = 40
  private val BUMP_PEAK = 48
  /** The widths starting at the nose and edging at the tip of the tail  */
  private val BUMPY_SNAKE_WIDTHS = Array(9.0, 22.0, 10.0, 13.0, 17.0, 22.0, 30.0,
    BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1, BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1,
    BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1, BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK,
    BUMP_2, BUMP_1, BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK,
    36.0, 31.0, 26.0, 22.0, 18.0, 14.0, 10.0, 6.0, 2.0)

  /** The widths starting at the nose and edging at the tip of the tail  */
  private val LONG_SNAKE_WIDTHS = Array(9.0, 18.0, 12.0, 13.0, 15.0, 17.0, 18.0,
    19.0, 20.0, 20.5, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0,
    21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0,
    21.0, 21.0, 21.0, 21.0, 21.0, 20.0, 19.5, 19.0, 18.0, 17.0, 16.0, 15.0,
    14.0, 13.0, 12.0, 10.0, 8.0, 6.0, 4.0)

  /** The widths starting at the nose and edging at the tip of the tail  */
  private val STRANGE_SNAKE_WIDTHS = Array(9.0, 20.0, 10.0, 11.0, 12.0, 14.0, 24.0, 20.0, 23.0,
    26.0, 28.0, 30.0, 29.0, 27.0, 36.0, 24.0, 22.0, 21.0, 20.0, 21.0, 42.0, 24.0,
    26.0, 27.0, 26.0, 25.0, 23.0, 31.0, 19.0, 17.0, 25.0, 13.0, 11.0, 9.0, 6.0, 2.0)

  /** The widths starting at the nose and edging at the tip of the tail  */
  private val FAT_SNAKE_WIDTHS = Array(9.0, 22.0, 10.0, 16.0, 22.0, 29.0, 35.0, 41.0,
    47.0, 52.0, 56.0, 60.0, 63.0, 65.0, 66.0, 67.0, 67.0, 66.0, 65.0, 63.0,
    60.0, 56.0, 52.0, 48.0, 43.0, 38.0, 33.0, 28.0, 23.0, 19.0, 16.0, 13.0, 10.0, 7.0, 4.0)


  case class Val(name: String, snakeData: SnakeData) extends super.Val
  implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]

  val TEST_SNAKE = Val("Test Snake", SnakeData(22, 26, TEST_SNAKE_WIDTHS))
  val BASIC_SNAKE = Val("Basic Snake", SnakeData(34, 20, BASIC_SNAKE_WIDTHS))
  val BUMPY_SNAKE = Val("Bumpy Snake", SnakeData(42, 22, BUMPY_SNAKE_WIDTHS))
  val LONG_SNAKE = Val("Long Snake", SnakeData(50, 20, LONG_SNAKE_WIDTHS))
  val STRANGE_SNAKE = Val("Strange Snake", SnakeData(35, 22, STRANGE_SNAKE_WIDTHS))
  val FAT_SNAKE = Val("Fat Snake", SnakeData(34, 22, FAT_SNAKE_WIDTHS))

  val VALUES = Array(TEST_SNAKE, BASIC_SNAKE, BUMPY_SNAKE, LONG_SNAKE, STRANGE_SNAKE, FAT_SNAKE)
}