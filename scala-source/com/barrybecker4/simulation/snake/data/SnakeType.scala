// Copyright by Barry G. Becker, 2016-2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.data

/*
object SnakeTypeWidths {

  /** The widths starting at the nose and edging at the tip of the tail  */
  protected final val BASIC_SNAKE_WIDTHS = Array(9.0, 18.0, 12.0, 13.0, 15.0, 17.0, 18.0, 19.0, 20.0,
    20.5, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 20.0,
    19.5, 19.0, 18.0, 17.0, 16.0, 15.0, 14.0, 13.0, 12.0, 10.0, 8.0, 6.0, 4.0)

  /** The widths starting at the nose and edging at the tip of the tail  */
  protected final val TEST_SNAKE_WIDTHS = Array(10.0, 17.0, 12.0, 14.0, 16.0, 18.0, 19.1, 20.2, 20.8, 21.0, 21.0,
    21.0, 21.0, 20.0, 19.0, 18.0, 17.0, 16.0, 14.0, 12.0, 10.0, 8.0, 6.0)

  protected val BUMP_TROUGH = 14
  protected val BUMP_1 = 20
  protected val BUMP_2 = 40
  protected val BUMP_PEAK = 48

  /** The widths starting at the nose and edging at the tip of the tail  */
  protected val BUMPY_SNAKE_WIDTHS = Array(9.0, 22.0, 10.0, 13.0, 17.0, 22.0, 30.0,
    BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1, BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1,
    BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1, BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK,
    BUMP_2, BUMP_1, BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK,
    36.0, 31.0, 26.0, 22.0, 18.0, 14.0, 10.0, 6.0, 2.0)

  /** The widths starting at the nose and edging at the tip of the tail  */
  protected val LONG_SNAKE_WIDTHS = Array(9.0, 18.0, 12.0, 13.0, 15.0, 17.0, 18.0,
    19.0, 20.0, 20.5, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0,
    21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0,
    21.0, 21.0, 21.0, 21.0, 21.0, 20.0, 19.5, 19.0, 18.0, 17.0, 16.0, 15.0,
    14.0, 13.0, 12.0, 10.0, 8.0, 6.0, 4.0)

  /** The widths starting at the nose and edging at the tip of the tail  */
  protected val STRANGE_SNAKE_WIDTHS = Array(9.0, 20.0, 10.0, 11.0, 12.0, 14.0, 24.0, 20.0, 23.0,
    26.0, 28.0, 30.0, 29.0, 27.0, 36.0, 24.0, 22.0, 21.0, 20.0, 21.0, 42.0, 24.0,
    26.0, 27.0, 26.0, 25.0, 23.0, 31.0, 19.0, 17.0, 25.0, 13.0, 11.0, 9.0, 6.0, 2.0)

  /** The widths starting at the nose and edging at the tip of the tail  */
  protected val FAT_SNAKE_WIDTHS = Array(9.0, 22.0, 10.0, 16.0, 22.0, 29.0, 35.0, 41.0,
    47.0, 52.0, 56.0, 60.0, 63.0, 65.0, 66.0, 67.0, 67.0, 66.0, 65.0, 63.0,
    60.0, 56.0, 52.0, 48.0, 43.0, 38.0, 33.0, 28.0, 23.0, 19.0, 16.0, 13.0, 10.0, 7.0, 4.0)
}
*/


/**
  * Different types of snakes to test.
  * @author Barry Becker
  */
enum SnakeType(val name: String, val snakeData: SnakeData):

  case TEST_SNAKE extends SnakeType("Test Snake", SnakeData(22, 26,
    Array(10.0, 17.0, 12.0, 14.0, 16.0, 18.0, 19.1, 20.2, 20.8, 21.0, 21.0,
    21.0, 21.0, 20.0, 19.0, 18.0, 17.0, 16.0, 14.0, 12.0, 10.0, 8.0, 6.0)))
  case BASIC_SNAKE extends SnakeType("Basic Snake", SnakeData(34, 20,
    Array(9.0, 18.0, 12.0, 13.0, 15.0, 17.0, 18.0, 19.0, 20.0,
      20.5, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 20.0,
      19.5, 19.0, 18.0, 17.0, 16.0, 15.0, 14.0, 13.0, 12.0, 10.0, 8.0, 6.0, 4.0)))
  case BUMPY_SNAKE extends SnakeType("Bumpy Snake", SnakeData(42, 22,
    Array(9.0, 22.0, 10.0, 13.0, 17.0, 22.0, 30.0,
      40, 48, 40, 20, 14, 20, 40, 48, 40, 20,
      14, 20, 40, 48, 40, 20, 14, 20, 40, 48,
      40, 20, 14, 20, 40, 48,
      36.0, 31.0, 26.0, 22.0, 18.0, 14.0, 10.0, 6.0, 2.0)))
  case LONG_SNAKE extends SnakeType("Long Snake", SnakeData(50, 20,
    Array(9.0, 18.0, 12.0, 13.0, 15.0, 17.0, 18.0,
    19.0, 20.0, 20.5, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0,
    21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0,
    21.0, 21.0, 21.0, 21.0, 21.0, 20.0, 19.5, 19.0, 18.0, 17.0, 16.0, 15.0,
    14.0, 13.0, 12.0, 10.0, 8.0, 6.0, 4.0)))
  case STRANGE_SNAKE extends SnakeType("Strange Snake", SnakeData(35, 22,
    Array(9.0, 20.0, 10.0, 11.0, 12.0, 14.0, 24.0, 20.0, 23.0,
    26.0, 28.0, 30.0, 29.0, 27.0, 36.0, 24.0, 22.0, 21.0, 20.0, 21.0, 42.0, 24.0,
    26.0, 27.0, 26.0, 25.0, 23.0, 31.0, 19.0, 17.0, 25.0, 13.0, 11.0, 9.0, 6.0, 2.0)))
  case FAT_SNAKE extends SnakeType("Fat Snake", SnakeData(34, 22,
    Array(9.0, 22.0, 10.0, 16.0, 22.0, 29.0, 35.0, 41.0,
      47.0, 52.0, 56.0, 60.0, 63.0, 65.0, 66.0, 67.0, 67.0, 66.0, 65.0, 63.0,
      60.0, 56.0, 52.0, 48.0, 43.0, 38.0, 33.0, 28.0, 23.0, 19.0, 16.0, 13.0, 10.0, 7.0, 4.0)))

end SnakeType