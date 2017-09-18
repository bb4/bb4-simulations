// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.data


/**
  * Different types of snakes to test.
  * @author Barry Becker
  */
class SnakeType extends Enumeration {

  case class Val(name: String, snakeData: SnakeData) extends super.Val
  implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]

  val TEST_SNAKE = Val("Test Snake", new TestSnakeData())
  val BASIC_SNAKE = Val("Basic Snake", new BasicSnakeData())
  val BUMPY_SNAKE = Val("Bumpy Snake", new BumpySnakeData())
  val LONG_SNAKE = Val("Long Snake", new LongSnakeData())
  val STRANGE_SNAKE = Val("Strange Snake", new StrangeSnakeData())
  val FAT_SNAKE = Val("Fat Snake", new FatSnakeData())
}