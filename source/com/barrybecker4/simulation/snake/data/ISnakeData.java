/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake.data;


/**
 * Snake geometry data
 * it is defined by the width of the transverse cross-sectional edges (of which there are num segments+1)
 * the magnitude of each segment is the same as its longer width
 *
 *  @author Barry Becker
 */
public interface ISnakeData {

    int getNumSegments();

    double getSegmentLength();

    double[] getWidths();
}
