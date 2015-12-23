package com.barrybecker4.simulation.cave.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for CaveMap
 */
public class CaveTest {

	private static final double DENSITY = 0.35;
	@Test
	public void test2by2Construction() {
		Cave cave = new Cave(2, 2, DENSITY);
		assertEquals("2 by 2 construction", "  \n0 \n", cave.toString());
	}

	@Test
	public void test3by3Construction() {
		Cave cave = new Cave(3, 3, DENSITY);
		assertEquals("3 by 3 construction", "   \n0  \n 0 \n", cave.toString());
	}

	@Test
	public void test5by5Construction() {
		Cave cave = new Cave(5, 5, DENSITY);
		assertEquals("5 by 5 construction", " 00  \n0 00 \n  0  \n  0  \n     \n", cave.toString());
	}

	@Test
	public void test4by1Construction() {
		Cave cave = new Cave(4, 1, DENSITY);
		assertEquals("4 by 1 construction", " 0  \n", cave.toString());
	}

	@Test
	public void test1by4Construction() {
		Cave cave = new Cave(1, 4, DENSITY);
		assertEquals("1 by 4 construction", " \n0\n \n \n", cave.toString());
	}

}
