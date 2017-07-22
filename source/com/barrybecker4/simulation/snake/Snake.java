/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake;

import com.barrybecker4.simulation.snake.data.ISnakeData;
import com.barrybecker4.simulation.snake.geometry.HeadSegment;
import com.barrybecker4.simulation.snake.geometry.Segment;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 *  Data structure and methods for representing a single dynamic snake
 *  The geometry of the snake is defined by ISnakeData
 *  General Improvements:
 *    - auto optimize with hill-climbing (let the snake learn how to move faster on its own)
 *    - add texture for snake skin
 *    - collision detection, walls, multiple snakes
 *    - goal directed path search
 *
 *  Performance Improvements:
 *    - profile (where is the time spent? rendering or computation)
 *    - only draw every nth frame
 *    - run OptimizeIt
 *  3/2011 frames per second for basic snake = 64.5
 *         Speed = 0.32
 *
 *  @author Barry Becker
 */
public class Snake {

    /** defines the snake geometry */
    private ISnakeData snakeData;

    /** the array of segments which make up the snake */
    private Segment[] segment = null;


    /**
     * Constructor
     * use a hardcoded static data interface to initialize
     * so it can be easily run in an applet without using resources.
     */
    public Snake(ISnakeData snakeData) {
        setData(snakeData);
    }

    /**
     * @param snakeData the data defining the snakes geometrical shape.
     */
    public void setData(ISnakeData snakeData) {
        this.snakeData = snakeData;
        initFromData();
    }

    public void reset() {
        resetFromData();
    }

    public int getNumSegments() {
        return snakeData.getNumSegments();
    }

    public Segment getSegment(int i) {
        return segment[i];
    }

    /** use this if you need to avoid reading from a file */
    private void initFromData()  {

        segment = new Segment[snakeData.getNumSegments()];
        resetFromData();
    }

    private void resetFromData() {

        double width1 = snakeData.getWidths()[0];
        double width2 = snakeData.getWidths()[1];
        int numSegments = snakeData.getNumSegments();
        double segmentLength = snakeData.getSegmentLength();

        double length = 80 + numSegments * segmentLength;
        Segment segment = new HeadSegment(width1, width2, segmentLength, length, 320.0, 0, this);
        this.segment[0] = segment;
        Segment segmentInFront = segment;
        width1 = width2;

        for ( int i = 1; i < numSegments-1; i++ ) {
            width2 = snakeData.getWidths()[i];

            segment = new Segment( width1, width2, segmentLength, segmentInFront, i, this );
            this.segment[i] = segment;
            segmentInFront = segment;
            width1 = width2;
        }
        this.segment[numSegments - 1] = new Segment(width1, width2, segmentLength, segmentInFront, numSegments-1, this);
    }

    /**
     * @return the center point of the snake
     */
    public Point2d getCenter() {
        Point2d center = new Point2d( 0.0, 0.0 );
        int ct = 0;
        for (int i = 0; i < snakeData.getNumSegments(); i += 2 ) {
            ct++;
            center.add( segment[i].getCenterParticle() );
        }
        center.scale( 1.0 / (double) ct );
        return center;
    }

    /**
     * shift/translate the whole snake by the specified vector
     */
    public void translate( Vector2d vec ) {
        for (int i = 0; i < snakeData.getNumSegments(); i++ ) {
            segment[i].translate( vec );
        }
    }

    /**
     * the snake is not considered stable if the angle between any edge segments exceeds Snake.MIN_EDGE_ANGLE
     * @return true if the snake has not gotten twisted too badly
     */
    public boolean isStable() {
        for (int i = 2; i < snakeData.getNumSegments(); i++ )
            if ( !segment[i].isStable() )
                return false;
        return true;
    }
}
