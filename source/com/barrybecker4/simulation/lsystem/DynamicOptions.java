// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem;

import com.barrybecker4.simulation.lsystem.model.LSystemModel;
import com.barrybecker4.ui.components.TextInput;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class DynamicOptions extends JPanel
                     implements ActionListener, KeyListener, SliderGroupChangeListener {

    private LSystemModel algorithm_;
    private LSystemExplorer simulator_;
    private JCheckBox useFixedSize_;
    private TextInput expression_;

    private static final String NUM_ITERATIONS_SLIDER = "Num Iterations";
    private static final String ANGLE_SLIDER = "Angle";
    private static final String SCALE_SLIDER = "Sale";
    private static final String SCALE_FACTOR_SLIDER = "Sale Factor";

    private SliderGroup sliderGroup_;
    private JTextArea formulaText_;


    private static final SliderProperties[] SLIDER_PROPS = {

        new SliderProperties(NUM_ITERATIONS_SLIDER,   0,    10,    LSystemModel.DEFAULT_ITERATIONS),
        new SliderProperties(ANGLE_SLIDER,   0,    180,    LSystemModel.DEFAULT_ANGLE, 100),
        new SliderProperties(SCALE_SLIDER,   0.01,    2.2,   LSystemModel.DEFAULT_SCALE,  1000.0),
        new SliderProperties(SCALE_FACTOR_SLIDER,   0.2,    2.0,   LSystemModel.DEFAULT_SCALE_FACTOR,  1000.0),
    };


    /**
     * Constructor
     */
    DynamicOptions(LSystemModel algorithm, LSystemExplorer simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        algorithm_ = algorithm;
        simulator_ = simulator;

        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);

        JPanel checkBoxes = createCheckBoxes();
        add(createExpressionInput());
        add(Box.createVerticalStrut(10));
        add(sliderGroup_);
        add(checkBoxes);
        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(1, 1000));
        add(fill);
        add(createFormulaText());
    }

    private JPanel createExpressionInput() {
        JPanel p = new JPanel(new FlowLayout());
        expression_ = new TextInput("Expression", LSystemModel.DEFAULT_EXPRESSION, 18);
        expression_.addKeyListener(this);
        p.add(expression_);
        return p;
    }

    private JPanel createCheckBoxes() {

        useFixedSize_ = new JCheckBox("Fixed Size", simulator_.getUseFixedSize());
        useFixedSize_.addActionListener(this);

        JPanel checkBoxes = new JPanel(new GridLayout(0, 1));
        checkBoxes.add(useFixedSize_);

        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }

    private JPanel createFormulaText() {

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());

        formulaText_ = new JTextArea();
        formulaText_.setEditable(false);
        formulaText_.setBackground(getBackground());
        updateFormulaText();

        textPanel.add(formulaText_, BorderLayout.CENTER);
        return textPanel;
    }

    private void updateFormulaText() {

        StringBuilder text = new StringBuilder();
        formulaText_.setText(text.toString());
    }

    public void reset() {
        sliderGroup_.reset();
    }

    /**
     * One of the buttons was pressed.
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == useFixedSize_) {
            simulator_.setUseFixedSize(useFixedSize_.isSelected());
        }
    }

    /**
     * One of the sliders was moved.
     *
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        if (sliderName.equals(NUM_ITERATIONS_SLIDER)) {
            algorithm_.setNumIterations((int)value);
        }
        else if (sliderName.equals(ANGLE_SLIDER)) {
            algorithm_.setAngle(value);
        }
        else if (sliderName.equals(SCALE_SLIDER)) {
            algorithm_.setScale(value);
        }
        else if (sliderName.equals(SCALE_FACTOR_SLIDER)) {
            algorithm_.setScaleFactor(value);
        }
    }


    /**
     * Implement keyListener interface.
     * @param key key that was pressed
     */
    public void keyTyped( KeyEvent key )  {}
    public void keyPressed(KeyEvent key) {}
    public void keyReleased(KeyEvent key) {
        char keyChar = key.getKeyChar();
        if ( keyChar == '\n' ) {
            algorithm_.setExpression(expression_.getValue());
        }
    }

}
