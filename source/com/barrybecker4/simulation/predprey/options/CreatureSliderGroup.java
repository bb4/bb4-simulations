/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.predprey.options;

import com.barrybecker4.simulation.predprey.creatures.Population;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderProperties;

/**
 * Represents a set of sliders to associate with a creature population.
 *
 * @author Barry Becker
 */
public class CreatureSliderGroup extends SliderGroup {

    private Population creaturePop;

    private static final String POPULATION_LABEL = " Population";
    private static final String BIRTH_RATE_LABEL = " Birth Rate";
    private static final String DEATH_RATE_LABEL = " Death Rate";

    public CreatureSliderGroup(Population creaturePop) {
        this.creaturePop = creaturePop;
        commonInit(createSliderProperties());
    }

    private SliderProperties[] createSliderProperties()  {

        SliderProperties[] props = new SliderProperties[3];

        String creatureName = creaturePop.getName();
        props[0] = new SliderProperties(creatureName + POPULATION_LABEL,
                0, 2000, creaturePop.getInitialPopulation());
        props[1] = new SliderProperties(creatureName + BIRTH_RATE_LABEL,
                0, creaturePop.getMaxBirthRate(), creaturePop.getInitialBirthRate(), 1000);
        props[2] = new SliderProperties(creatureName + DEATH_RATE_LABEL,
                0, creaturePop.getMaxDeathRate(), creaturePop.getInitialDeathRate(), 1000.0);

        return props;
    }

    public void update() {
        this.setSliderValue(0, creaturePop.getPopulation());
    }

    /**
     * One of the sliders was potentially moved.
     * Check for match based on name.
     */
    public void checkSliderChanged(String sliderName, double value) {

        for (SliderProperties props : this.getSliderProperties()) {
            if (sliderName.equals(props.getName()))  {
                if (sliderName.endsWith(POPULATION_LABEL))  {
                    creaturePop.setPopulation( value );
                }
                else if (sliderName.endsWith(BIRTH_RATE_LABEL)) {
                    creaturePop.birthRate = value;
                }
                else if (sliderName.endsWith(DEATH_RATE_LABEL)) {
                    creaturePop.deathRate = value;
                }
                else {
                    throw new IllegalStateException("Unexpected sliderName:" + sliderName);
                }
            }
        }
    }

}

