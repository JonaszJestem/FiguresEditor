import javafx.scene.control.ToggleButton;

import javax.swing.JToggleButton;

import javax.swing.*;
import java.awt.*;


/**
 * ActionPanel provides panel with buttons to:
 * Change mode: draw mode, modify mode, color mode;
 * Select figure type to draw: circle, rectangle, polygon;
 * Delete active figure or delete all figures.
 */
class ActionPanel extends JPanel {

    /**
     * Buttons used to change mode.
     */
    private final ButtonGroup modeButtons;
    private final JToggleButton drawMode;
    private final JToggleButton modifyMode;
    private final JToggleButton colorMode;

    private final JButton clear, deleteActive;

    /**
     * Buttons used to select figures.
     */

    private ButtonGroup figureButtons;
    private JToggleButton drawCircle, drawRectangle, drawPolygon;

    /**
     * Reference to drawPanel used to clear figures.
     */
    private DrawPanel drawPanel;

    ActionPanel() {
        super();
        setLayout(new GridLayout(15,1));

        /**
         * Group mode buttons
         */
        modeButtons = new ButtonGroup();
        drawMode = new JToggleButton("Draw");
        modifyMode = new JToggleButton("Modify");
        colorMode = new JToggleButton("Color");

        modeButtons.add(drawMode);
        modeButtons.add(modifyMode);
        modeButtons.add(colorMode);

        /**
         * Add clear buttons and assign proper actions for them
         */
        clear = new JButton("Clear");
        clear.addActionListener(e -> {
            if (drawPanel.getFigures().size() > 0) {
                int option = JOptionPane.showConfirmDialog(null, "Do you really want to clear all your figures?");
                if(option==0) {
                    drawPanel.clearFigures();
                }
            }
        });
        deleteActive = new JButton("Delete active");
        deleteActive.addActionListener(e -> drawPanel.removeActive());

        /**
         * Group buttons to choose figures
         */
        figureButtons = new ButtonGroup();
        drawCircle = new JToggleButton("Circle");
        drawRectangle = new JToggleButton("Rectangle");
        drawPolygon = new JToggleButton("Polygon");

        figureButtons.add(drawCircle);
        figureButtons.add(drawRectangle);
        figureButtons.add(drawPolygon);

        /**
         * Set figure buttons not visible by default.
         */
        setButtonsVisible(false);
        clear.setVisible(false); deleteActive.setVisible(false);

        /**
         * Sets figure buttons visible or hide it depending on selected mode.
         */
        drawMode.addActionListener(e -> {setButtonsVisible(true); clear.setVisible(false); deleteActive.setVisible(false); drawPanel.clearPoints(); drawPanel.repaint();});
        modifyMode.addActionListener(e -> {setButtonsVisible(false); clear.setVisible(true); deleteActive.setVisible(true); drawPanel.clearPoints(); drawPanel.repaint();});
        colorMode.addActionListener(e -> {setButtonsVisible(false); clear.setVisible(false); deleteActive.setVisible(false); drawPanel.clearPoints(); drawPanel.repaint();});

        /**
         * Add components to ActionPanel.
         */

        add(drawMode);
        add(modifyMode);
        add(colorMode);
        add(new JPanel());
        add(drawCircle);
        add(drawRectangle);
        add(drawPolygon);
        add(new JPanel());
        add(clear);
        add(deleteActive);
    }

    /**
     * Sets the reference to drawPanel
     * @param drawPanel reference to drawPanel
     */
    void setDrawPanel(DrawPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    /**
     * Sets figure buttons visible or not visible
     * @param isVisible boolean that tells if buttons should be visible or not
     */

    private void setButtonsVisible(boolean isVisible) {
        drawCircle.setVisible(isVisible);
        drawRectangle.setVisible(isVisible);
        drawPolygon.setVisible(isVisible);

    }

    /**
     * Methods to check what buttons are selected
     * @return {@code true} if button is selected, {@code false} otherwise
     */

    boolean isDrawEnabled() {
        return drawMode.isSelected();
    }
    boolean isModifyEnabled() {
        return modifyMode.isSelected();
    }
    boolean isColorEnabled() {
        return colorMode.isSelected();
    }

    boolean drawRectangle() {
        return drawRectangle.isSelected();
    }
    boolean drawCircle() {
        return drawCircle.isSelected();
    }
    boolean drawPolygon() {
        return drawPolygon.isSelected();
    }
}
