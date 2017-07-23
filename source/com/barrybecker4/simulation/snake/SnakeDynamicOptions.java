/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake;

import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class SnakeDynamicOptions extends JPanel
                          implements SliderGroupChangeListener {

    private SnakeSimulator snakeSim;

    private static final String DIRECTION_SLIDER = "Direction";
    private static final String WAVE_SPEED_SLIDER = "Wave Speed";
    private static final String WAVE_AMPLITUDE_SLIDER = "Wave Amplitude";
    private static final String WAVE_PERIOD_SLIDER = "Wave Period";
    private static final String MASS_SCALE_SLIDER = "Mass Scale";
    private static final String SPRING_CONST_SLIDER = "Spring Constant";
    private static final String SPRING_DAMPING_SLIDER = "Spring Damping";
    private static final String SCALE_SLIDER = "Scale";
    private static final String TIMESTEP_SLIDER = "Time Step Size";

    private SliderGroup sliderGroup;


    /**
     * Constructor
     */
    SnakeDynamicOptions(SnakeSimulator snake) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        snakeSim = snake;

        sliderGroup = new SliderGroup(createSliderProperties());
        sliderGroup.addSliderChangeListener(this);

        add(sliderGroup);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(10, 1000));
        add(fill);
    }

    private SliderProperties[] createSliderProperties() {
        LocomotionParameters params = new LocomotionParameters();
        SliderProperties[] sliderProps;
        sliderProps = new SliderProperties[]{
                //                                     MIN  MAX   INITIAL   SCALE
                new SliderProperties(DIRECTION_SLIDER, -1.0, 1.0, params.getDirection(), 100),
                new SliderProperties(WAVE_SPEED_SLIDER, 0.0001, 0.1, params.getWaveSpeed(), 1000),
                new SliderProperties(WAVE_AMPLITUDE_SLIDER, 0.000, 0.3, params.getWaveAmplitude(), 100),
                new SliderProperties(WAVE_PERIOD_SLIDER, .5, 5.0, params.getWavePeriod(), 100),
                new SliderProperties(MASS_SCALE_SLIDER, 0.1, 6.0, params.getMassScale(), 100),
                new SliderProperties(SPRING_CONST_SLIDER, 0.1, 4.0, params.getSpringK(), 100),
                new SliderProperties(SPRING_DAMPING_SLIDER, 0.1, 4.0, params.getSpringDamping(), 100),
                new SliderProperties(SCALE_SLIDER, 0.2, 2.0, snakeSim.getScale(), 100),
                new SliderProperties(TIMESTEP_SLIDER, 0.001, 0.5, SnakeSimulator.INITIAL_TIME_STEP, 1000)};
         return sliderProps;
    }


    public void reset() {
        sliderGroup.reset();
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        LocomotionParameters params = snakeSim.getLocomotionParams();

        if (sliderName.equals(DIRECTION_SLIDER)) {
            params.setDirection(value);
        }
        else if (sliderName.equals(WAVE_SPEED_SLIDER)) {
            params.setWaveSpeed(value);
        }
        else if (sliderName.equals(WAVE_AMPLITUDE_SLIDER)) {
            params.setWaveAmplitude(value);
        }
        else if (sliderName.equals(WAVE_PERIOD_SLIDER)) {
            params.setWavePeriod(value);
        }
        else if (sliderName.equals(MASS_SCALE_SLIDER)) {
            params.setMassScale(value);
        }
        else if (sliderName.equals(SPRING_CONST_SLIDER)) {
            params.setSpringK(value);
        }
        else if (sliderName.equals(SPRING_DAMPING_SLIDER)) {
            params.setSpringDamping(value);
        }
        else if (sliderName.equals(SCALE_SLIDER)) {
            snakeSim.setScale(value);
        }
        else if (sliderName.equals(TIMESTEP_SLIDER)) {
            snakeSim.setTimeStep(value);
        }
    }
}
