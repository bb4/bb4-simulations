/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat1.options;

import com.barrybecker4.simulation.habitat1.HabitatSimulator;
import com.barrybecker4.simulation.habitat1.creatures.Population;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
public class DynamicOptions extends JPanel
                            implements SliderGroupChangeListener {

    private List<CreatureSliderGroup> sliderGroups;

    /**
     * Constructor
     */
    public DynamicOptions(HabitatSimulator simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        sliderGroups = new ArrayList<CreatureSliderGroup>();
        for (Population creaturePop : simulator.getPopulations()) {
            CreatureSliderGroup group = new CreatureSliderGroup(creaturePop);
            group.addSliderChangeListener(this);
            sliderGroups.add(group);
            add(group);
        }

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(1, 1000));
        add(fill);
    }

    public void update() {
        //for (CreatureSliderGroup group : sliderGroups)  {
        //    group.update();
        //}
    }

    public void reset() {
        for (SliderGroup group : sliderGroups)  {
            group.reset();
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        for (CreatureSliderGroup group : sliderGroups)  {
            group.checkSliderChanged(sliderName, value);
        }
    }

}
