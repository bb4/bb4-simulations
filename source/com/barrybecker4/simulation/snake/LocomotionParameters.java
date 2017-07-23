/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake;

import com.barrybecker4.common.math.WaveType;

/**
 * Tweakable snake parameters that define locomotion.
 *
 * @author Barry Becker
 */
public class LocomotionParameters {

    // I used simulated annealing to come up with these optimal parameter values
    // When I originally started the snake's speed was about .21 using my best guess.
    // After optimization the snake's speed is about .33
    static final double WAVE_SPEED = 0.00478;  // .04  before optimization
    static final double WAVE_AMPLITUDE = 0.026877; // .04
    static final double WAVE_PERIOD = 3.6346; // 3.0

    private static final boolean USE_FRICTION = true;

    private static final double MASS_SCALE = 1.5;
    private static final double SPRING_K = 0.6;
    private static final double SPRING_DAMPING = 1.2;
    private static final double STATIC_FRICTION = 0.1;
    private static final double DYNAMIC_FRICTION = 0.1;

    /** hard left = 1.0; hard right = -1.0; straight = 0; */
    private double direction = 0.0;

    private WaveType waveType = WaveType.SINE_WAVE;

    private boolean useFriction = USE_FRICTION;

    /** the speed at which the muscular contraction wave travels down the body of the snake  */
    private double waveSpeed = WAVE_SPEED;

    /** scale factor for the force function (must be greater than 0 and less than 1.0) */
    private double waveAmplitude = WAVE_AMPLITUDE;

    //** The period of the sinusoidal force function   */
    private double wavePeriod = WAVE_PERIOD;

    /** scales the overall mass of the snake up or down */
    private double massScale = MASS_SCALE;

    /** the stiffness of the springs that make up the snakes body   */
    private double springK = SPRING_K;


    /**
     * the amount of spring damping present in the springs
     * this corresponds to how quickly the amplitude of the spring goes to 0
     */
    private double springDamping = SPRING_DAMPING;

    private double staticFriction = STATIC_FRICTION;
    private double dynamicFriction = DYNAMIC_FRICTION;

    /**
     * Constructor
     */
    public LocomotionParameters() {
    }

    public void setDirection(double dir) {
        direction = dir;
    }
    public double getDirection() {
        return direction;
    }

    public void setUseFriction(boolean use) {
        useFriction = use;
    }
    public boolean getUseFriction() {
        return useFriction;
    }

    public void setWaveSpeed(double speed) {
        waveSpeed = speed;
    }

    public double getWaveSpeed() {
        return waveSpeed;
    }

    public void setWaveAmplitude( double waveAmplitude ) {
        this.waveAmplitude = waveAmplitude;
    }

    public double getWaveAmplitude() {
        return waveAmplitude;
    }

    public void setWavePeriod( double wavePeriod ) {
        this.wavePeriod = wavePeriod;
    }

    public double getWavePeriod() {
        return wavePeriod;
    }

    public void setMassScale( double massScale ) {
        this.massScale = massScale;
    }

    public double getMassScale() {
        return massScale;
    }

    public void setSpringK( double springK ) {
        this.springK = springK;
    }

    public double getSpringK() {
        return springK;
    }

    public void setSpringDamping( double springDamping ) {
        this.springDamping = springDamping;
    }

    public double getSpringDamping()  {
        return springDamping;
    }

    public void setWaveType( WaveType waveType ) {
        this.waveType = waveType;
    }

    public WaveType getWaveType() {
        return waveType;
    }

    public void setStaticFriction( double staticFriction ) {
        this.staticFriction = staticFriction;
    }

    public double getStaticFriction() {
        return staticFriction;
    }

    public void setDynamicFriction( double dynamicFriction ) {
        this.dynamicFriction = dynamicFriction;
    }

    public double getDynamicFriction() {
        return dynamicFriction;
    }

}
