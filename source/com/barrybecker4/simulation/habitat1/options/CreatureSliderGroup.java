/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat1.options;

import com.barrybecker4.simulation.habitat1.creatures.CreatureType;
import com.barrybecker4.simulation.habitat1.creatures.Population;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderProperties;

/**
 * Represents a set of sliders to associate with a creature population.
 *
 * @author Barry Becker
 */
public class CreatureSliderGroup extends SliderGroup {

    private Population creaturePop_;

    private static final String SIZE_LABEL = " size";
    private static final String GESTATION_LABEL = " gestation period";
    private static final String STARVATION_LABEL = " starvation time";
    private static final String NUTRITION_LABEL = " nutritional value";
    private static final String NORM_SPEED_LABEL = " normal speed";
    private static final String MAX_SPEED_LABEL = " top speed";

    private static final double MIN_FACTOR = 0.2;
    private static final double MAX_FACTOR = 6;

    public CreatureSliderGroup(Population creaturePop) {
        creaturePop_ = creaturePop;
        commonInit(createSliderProperties());
    }

    private SliderProperties[] createSliderProperties()  {

        CreatureType type = creaturePop_.getType();
        double normSpeed = type.getNormalSpeed();
        SliderProperties[] props = normSpeed==0? new SliderProperties[4] : new SliderProperties[6];


        String creatureName = type.getName();
        setBackground(type.getColor());

        double size = type.getSize();
        props[0] = new SliderProperties(creatureName + SIZE_LABEL,
                                        MIN_FACTOR * size, MAX_FACTOR * size, size, 200);
        int gestation = type.getGestationPeriod();
        props[1] = new SliderProperties(creatureName + GESTATION_LABEL,
                                        1, (int)(MAX_FACTOR * gestation), gestation);
        int starveTime = type.getStarvationThreshold();
        props[2] = new SliderProperties(creatureName + STARVATION_LABEL,
                                       (int)(MIN_FACTOR * starveTime), (int)(MAX_FACTOR * starveTime), starveTime);
        int nutrition = type.getNutritionalValue();
        props[3] = new SliderProperties(creatureName + NUTRITION_LABEL,
                                         1, (int)(MAX_FACTOR * nutrition), nutrition);
        if (normSpeed > 0) {
            props[4] = new SliderProperties(creatureName + NORM_SPEED_LABEL,
                                             0, MAX_FACTOR * normSpeed, normSpeed, 1000.0);
            double maxSpeed = type.getMaxSpeed();
            props[5] = new SliderProperties(creatureName + MAX_SPEED_LABEL,
                                             0, MAX_FACTOR * maxSpeed, maxSpeed, 1000.0);
        }
        return props;
    }

    /**
     * One of the sliders was potentially moved.
     * Check for match based on name.
     */
    public void checkSliderChanged(String sliderName, double value) {

        CreatureType type = creaturePop_.getType();

        for (SliderProperties props : this.getSliderProperties()) {
            if (sliderName.equals(props.getName()))  {
                if (sliderName.endsWith(SIZE_LABEL))  {
                    type.setSize(value);
                }
                else if (sliderName.endsWith(GESTATION_LABEL)) {
                    type.setGestationPeriod((int)value);
                }
                else if (sliderName.endsWith(STARVATION_LABEL)) {
                    type.setStarvationThreshold((int)value);
                }
                else if (sliderName.endsWith(NUTRITION_LABEL)) {
                    type.setNutritionalValue((int)value);
                }
                else if (sliderName.endsWith(NORM_SPEED_LABEL)) {
                    type.setNormalSpeed(value);
                }
                else if (sliderName.endsWith(MAX_SPEED_LABEL )) {
                    type.setMaxSpeed(value);
                }
                else {
                    throw new IllegalStateException("Unexpected sliderName:" + sliderName);
                }
            }
        }
    }

}

