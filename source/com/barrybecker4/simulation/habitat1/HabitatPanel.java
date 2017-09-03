package com.barrybecker4.simulation.habitat1;

import com.barrybecker4.simulation.habitat1.creatures.Populations;

import javax.swing.*;
import java.awt.*;

/**
 * Shows the time series graph of all the animal populations in the habitat.
 * @author Barry Becker
 */
public class HabitatPanel extends JPanel {

    private HabitatRenderer renderer;

    HabitatPanel(Populations populations) {
        renderer = new HabitatRenderer(populations);
    }

    public void paint(Graphics g) {
        renderer.setSize(getWidth(), getHeight());
        renderer.paint(g);
    }

}
