package com.barrybecker4.simulation.cave.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for CaveMap
 */
public class CaveTest {

	private static final double FLOOR = 0.1;
	private static final double CEILING = 0.9;

	@Test
	public void test2by2Construction() {
		Cave cave = new Cave(2, 2, FLOOR, CEILING);
		//assertEquals("2 by 2 construction", "  \n0 \n", cave.toString());
		assertEquals("2 by 2 construction", "CC\nCC\n", cave.toString());
	}

	@Test
	public void test3by3Construction() {
		Cave cave = new Cave(3, 3, FLOOR, CEILING);
		assertEquals("3 by 3 construction", "CCC\nCCW\nCCC\n", cave.toString());
	}

	@Test
	public void test5by5Construction() {
		Cave cave = new Cave(5, 5, FLOOR, CEILING);
		assertEquals("5 by 5 construction", "CCCWW\nCCCCC\nCWCCC\nCCCCC\nCWCCC\n", cave.toString());
		//assertEquals("5 by 5 construction", " 00  \n0 00 \n  0  \n  0  \n     \n", cave.toString());
	}

	@Test
	public void test4by1Construction() {
		Cave cave = new Cave(4, 1, FLOOR, CEILING);
		assertEquals("4 by 1 construction", "CCCC\n", cave.toString());
	}

	@Test
	public void test1by4Construction() {
		Cave cave = new Cave(1, 4, FLOOR, CEILING);
		//assertEquals("1 by 4 construction", " \n0\n \n \n", cave.toString());
		assertEquals("1 by 4 construction", "C\nC\nC\nC\n", cave.toString());
	}

}
