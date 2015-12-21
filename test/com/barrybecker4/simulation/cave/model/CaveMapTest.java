package com.barrybecker4.simulation.cave.model;


/**
 * Unit tests for CaveMap
 */
public class CaveMapTest {

	public void runTests() {
		test2by2Construction();
		test3by3Construction();
		test5by5Construction();
		test4by1Construction();
		test1by4Construction();

		testNeighborCount();

		testNextPhase();
	}

	private void test2by2Construction() {
		CaveMap cave = new CaveMap(2, 2);
		verify("2 by 2 construction", "  \n0 \n", cave.toString());
	}

	private void test3by3Construction() {
		CaveMap cave = new CaveMap(3, 3);
		verify("3 by 3 construction", "   \n0  \n 0 \n", cave.toString());
	}

	private void test5by5Construction() {
		CaveMap cave = new CaveMap(5, 5);
		verify("5 by 5 construction", " 00  \n0 00 \n  0  \n  0  \n     \n", cave.toString());
	}

	private void test4by1Construction() {
		CaveMap cave = new CaveMap(4, 1);
		verify("4 by 1 construction", " 0  \n", cave.toString());
	}

	private void test1by4Construction() {
		CaveMap cave = new CaveMap(1, 4);
		verify("1 by 4 construction", " \n0\n \n \n", cave.toString());
	}

	/** verify that we get correct neighbor counts for different positions within the 5x5 cave. */
	private void testNeighborCount() {
		CaveMap cave = new CaveMap(5, 5);

		verify("count for 0,0", "7", "" + cave.neighborCount(0, 0));
		verify("count for 1,0", "6", "" + cave.neighborCount(1, 0));
		verify("count for 1,1", "5", "" + cave.neighborCount(1, 1));
		verify("count for 4,3", "3", "" + cave.neighborCount(4, 3));
	}

	private void testNextPhase() {
		CaveMap cave = new CaveMap(5, 5);
		cave.nextPhase();
		verify("5 by 5 nextPhase", "0  00\n 0  0\n00 00\n0 0 0\n00000\n", cave.toString());
	}

	private void verify(String name, String expected, String actual) {
		if (expected.equals(actual)) {
			System.out.println("Success: " + name);
		}
		else {
			System.out.println("Failure for " + name + ". The actual result");
			System.out.println(actual);
			System.out.println("did not equal");
			System.out.println(expected);
		}
	}

	public static void main(String[] args) {

		CaveMapTest tester = new CaveMapTest();
		tester.runTests();
	}
}
