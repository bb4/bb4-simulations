/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.config;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import org.junit.Test;

import javax.vecmath.Vector2d;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Barry Becker
 */
public class TestSource {

    /** class under test. */
    private Source src;

    private static final Location LOCATION_START = new ByteLocation(1,1);
    private static final Location LOCATION_STOP = new ByteLocation(1,1);

    private static final Vector2d VELOCITY1 = new Vector2d(1,1);

    @Test
    public void testOnTimeNoRepeat() {
        src = new Source(LOCATION_START, VELOCITY1);
        verifyStatus(src, 0.0, true);
        verifyStatus(src, 0.1, true);

        src = new Source(LOCATION_START, LOCATION_STOP, VELOCITY1, 2.0, -1, -1);
        verifyStatus(src, 0.0, false);
        verifyStatus(src, 1.9, false);
        verifyStatus(src, 2.0, true);
        verifyStatus(src, 20.0, true);
    }

    @Test
    public void testOnTimeDuration() {
        src = new Source(LOCATION_START, LOCATION_STOP, VELOCITY1, 2.0, 1.0, -1);
        verifyStatus(src, 0.0, false);
        verifyStatus(src, 1.9, false);
        verifyStatus(src, 2.0, true);
        verifyStatus(src, 2.5, true);
        verifyStatus(src, 3.0, false);
        verifyStatus(src, 3.3, false);
        verifyStatus(src, 20.0, false);
    }

    @Test
    public void testOnTimeRepeat() {
        src = new Source(LOCATION_START, LOCATION_STOP, VELOCITY1, 2.0, 1.0, 3.0);

        // starts at 2.0, continues to 3.0, off until 5.0, on again until 6.0, off until 8.0,...
        verifyStatus(src, 0.0, false);
        verifyStatus(src, 1.9, false);
        verifyStatus(src, 2.0, true);
        verifyStatus(src, 2.5, true);
        verifyStatus(src, 3.0, false);
        verifyStatus(src, 3.3, false);
        verifyStatus(src, 4.3, false);

        verifyStatus(src, 5.3, true);
        verifyStatus(src, 6.3, false);
        verifyStatus(src, 10.1, false);
        verifyStatus(src, 11.1, true);
        verifyStatus(src, 11.2, true);
    }


    private void verifyStatus(Source src, double t, boolean expectedValue) {
        boolean isOn = src.isOn(t);
        String err = (isOn == expectedValue)? "" : " : ERROR";
        ////System.out.println("checking t="+t +" exp="+expectedValue +" got=" + isOn  + err);
        if (expectedValue) {
            assertTrue("We expected it to be on at "+ t, isOn);
        }
        else {
            assertFalse("We expected it to be off at " + t, isOn);
        }
    }

}
