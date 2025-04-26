// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.mpm


// Enhanced SimulationApp for the water simulation
object WaterSimulationApp {
  def main(args: Array[String]): Unit = {
    import javax.swing._
    import java.awt._
    import java.awt.event._

    SwingUtilities.invokeLater(() => {
      // Create the water simulation
      val environment = new WaterEnvironment("")
      environment.initialize()

      // Create visualization
      val renderer = new EnvironmentRenderer(environment)

      // Create frame
      val frame = new JFrame("MPM Water Simulation")
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
      frame.setSize(800, 830)

      // Create control panel
      val controlPanel = new JPanel()
      controlPanel.setLayout(new FlowLayout())

      val pauseButton = new JButton("Pause")
      pauseButton.addActionListener(_ => {
        if (environment.isPaused) {
          environment.resume()
          pauseButton.setText("Pause")
        } else {
          environment.pause()
          pauseButton.setText("Resume")
        }
      })

      val restartButton = new JButton("Restart")
      restartButton.addActionListener(_ => {
        environment.restart()
      })

      val faucetButton = new JButton("Toggle Faucet")
      faucetButton.addActionListener(_ => {
        if (environment.faucetRunning) {
          environment.stopFaucet()
        } else {
          environment.startFaucet((0.5, 0.9), (0.0, -1.0), 0.05)
        }
      })

      controlPanel.add(pauseButton)
      controlPanel.add(restartButton)
      controlPanel.add(faucetButton)

      // Add a panel for displaying parameters
      val paramsPanel = new JPanel()
      paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.Y_AXIS))

      // Add sliders for each parameter
      val sliders = environment.getUiParameters().map { param =>
        val slider = new JSlider(SwingConstants.HORIZONTAL,
          (param.minValue * 100).toInt,
          (param.maxValue * 100).toInt,
          (param.minValue * 100).toInt)

        slider.setMajorTickSpacing((param.step * 500).toInt)
        slider.setPaintTicks(true)

        val label = new JLabel(s"${param.displayName}: ${param.minValue}")

        slider.addChangeListener(e => {
          val value = slider.getValue() / 100.0

          // Update the simulation parameter using reflection
          try {
            val field = environment.getClass.getDeclaredField(param.name)
            field.setAccessible(true)
            field.set(environment, value)
            label.setText(s"${param.displayName}: $value")
          } catch {
            case e: Exception =>
              try {
                val field = environment.params.getClass.getDeclaredField(param.name)
                field.setAccessible(true)
                field.set(environment.params, value)
                label.setText(s"${param.displayName}: $value")
              } catch {
                case e: Exception => println(s"Could not set parameter ${param.name}: ${e.getMessage}")
              }
          }
        })

        val paramPanel = new JPanel()
        paramPanel.setLayout(new BorderLayout())
        paramPanel.add(label, BorderLayout.WEST)
        paramPanel.add(slider, BorderLayout.CENTER)

        paramPanel
      }

      sliders.foreach(paramsPanel.add)

      // Create layout
      val mainPanel = new JPanel(new BorderLayout())
      mainPanel.add(renderer, BorderLayout.CENTER)
      mainPanel.add(controlPanel, BorderLayout.NORTH)
      mainPanel.add(paramsPanel, BorderLayout.SOUTH)

      frame.add(mainPanel)
      frame.setVisible(true)

      // Start animation timer
      val timer = new Timer(16, new ActionListener {
        override def actionPerformed(e: ActionEvent): Unit = {
          environment.advance()
          renderer.repaint()
        }
      })
      timer.start()
    })
  }
}