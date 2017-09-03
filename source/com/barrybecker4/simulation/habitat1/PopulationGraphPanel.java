/** Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat1;

import com.barrybecker4.simulation.habitat1.creatures.Populations;
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Shows the time series graph of all the animal populations in the habitat.
 * @author Barry Becker
 */
public class PopulationGraphPanel extends JPanel {

    private MultipleFunctionRenderer graphRenderer_;

    PopulationGraphPanel(Populations populations)  {
        graphRenderer_ = populations.createFunctionRenderer();
    }

    public void paint(Graphics g) {
        graphRenderer_.setSize(getWidth(), getHeight());
        graphRenderer_.paint(g);
    }

}
