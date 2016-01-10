package com.barrybecker4.simulation.cave.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for CaveMap
 */
public class CaveProcessorTest {

	@Test
	public void testNextPhase() {
		CaveProcessor processor = new CaveProcessor(5, 5, 0.1, 0.9, 3.0, 2.0, CaveProcessor.KernelType.BASIC, false);
		processor.nextPhase();
		assertEquals("5 by 5 nextPhase", "W00WW\n0W00W\nWW0WW\nW . W\nWWWWW\n", processor.toString());
	}

}

