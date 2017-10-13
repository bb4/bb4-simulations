// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst1;

import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderProperties;

/**
 * Represents a set of sliders to associate with a creature population.
 *
 * @author Barry Becker
 */
public class CreatureSliderGroup extends SliderGroup {

    private Population creaturePop;
    private static final String BIRTH_RATE_LABEL = " Birth Rate";

    public CreatureSliderGroup(Population creaturePop) {
        this.creaturePop = creaturePop;
        commonInit(createSliderProperties());
    }

    private SliderProperties[] createSliderProperties()  {

        SliderProperties[] props = new SliderProperties[1];

        String creatureName = creaturePop.getName();

        props[0] = new SliderProperties(creatureName + BIRTH_RATE_LABEL,
                1.9, creaturePop.getMaxBirthRate(), creaturePop.getInitialBirthRate(), 1000.0);

        return props;
    }

    /**
     * One of the sliders was potentially moved.
     * Check for match based on name.
     */
    public void checkSliderChanged(String sliderName, double value) {

        for (SliderProperties props : this.getSliderProperties()) {
            if (sliderName.equals(props.getName()))  {
                if (sliderName.endsWith(BIRTH_RATE_LABEL)) {

                    creaturePop.birthRate = value;
                }
                else {
                    throw new IllegalStateException("Unexpected sliderName:" + sliderName);
                }
            }
        }
    }

}

