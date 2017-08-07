package com.barrybecker4.simulation.reactiondiffusion;

import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class GrayScottModelTest {



    @Test
    public void  periodicMid() {
        assertEquals(10,  GrayScottModel.getPeriodicXValue(10, 100) );
    }

    @Test
    public void  testPeriodicLow() {
        assertEquals(93, GrayScottModel.getPeriodicXValue(-7, 100) );
    }

    @Test
    public void   testPeriodicHigh() {
        assertEquals(7,  GrayScottModel.getPeriodicXValue(107, 100));
    }

}
