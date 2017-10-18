import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import static java.lang.Math.*;
import static oracle.jrockit.jfr.events.Bits.intValue;

/**
 * Class DrawPanel creates panel acting as canvas to paint, move, color and show created figures.
 */
class DrawPanel extends JPanel {

    /**
     * xPos, yPos lists contains points that user choose.
     * Then points are transformed into chosen figure - that is provided in DrawingAdapter
     * @see DrawingAdapter
     *
     * xMove, yMove contains initial position of cursor - used to move figures.
     * @see MotionAdapter
     */
    private final ArrayList<Integer> xPos = new ArrayList<>();
    private final ArrayList<Integer> yPos = new ArrayList<>();
    private int xMove, yMove;

    /**
     * Figures list contains all the figures created by user.
     * Colors list contains colors of the figures.
     * ActiveFigure contains the index of selected figure.
     * DisableMarking allows to disable marking other figures
     */
    private ArrayList<Shape> figures = new ArrayList<>();
    private ArrayList<Color> colors = new ArrayList<>();
    private int activeFigure = -1;
    private boolean disableMarking = false;

    /**
     * References to other panels which are needed to cooperate with DrawPanel.
     * @see ActionPanel
     * @see EditPanel
     */
    private ActionPanel actionPanel;
    private EditPanel editPanel;



    DrawPanel() {
        /**
         * Creating blank panel with white background and adding listener that allows user to draw, move and scale figures.
         */
        super();
        setBackground(Color.white);

        addMouseListener(new DrawingAdapter());
        addMouseMotionListener(new MotionAdapter());
        addMouseWheelListener(new Scaling());
    }

    /**
     * Sets the reference to actionPanel. DrawPanel needs to know what buttons are selected to draw proper figures
     * @param actionPanel reference to actionPanel
     */
    void setActionPanel(ActionPanel actionPanel) {
        this.actionPanel = actionPanel;
    }

    /**
     * Sets the reference to editPanel. DrawPanel tells editPanel to update the list of figures and takes chosen color from editPanel.
     * @param editPanel reference to editPanel
     */
    void setEditPanel(EditPanel editPanel) { this.editPanel = editPanel; }

    /**
     * Returns figures needed for saving drawing into file.
     * @return list of {@code figures}
     */
    ArrayList<Shape> getFigures() {
        return figures;
    }

    /**
     * Returns colors needed for saving drawing into file.
     * @return list of {@code colors}
     */
    ArrayList<Color> getColors() {
        return colors;
    }

    /**
     * Clears points selected by user. Invoked when mode is changed or figure is drawn.
     */
    void clearPoints() {
        xPos.clear();
        yPos.clear();
    }

    /**
     * Clears all figures drawn. Also deletes colors connected with figures and drawn points.
     */
    void clearFigures() {
        xPos.clear();
        yPos.clear();

        figures.clear();
        colors.clear();
        activeFigure = -1;

        editPanel.updateLayers(figures);

        repaint();
    }

    /**
     * Sets the list of figures from opened file.
     * @param figures figures read from the file.
     * @param colors colors of figures read from the file.
     */
    void setFigures(ArrayList<Shape> figures, ArrayList<Color> colors) {
        this.figures = new ArrayList<>(figures);
        this.colors = new ArrayList<>(colors);
        activeFigure = -1;
        repaint();
    }

    /**
     * Removes selected figure. Allowed only in modify mode.
     */

    void removeActive() {
        if(activeFigure != -1 && figures.size()>0 && actionPanel.isModifyEnabled()) {
            figures.remove(activeFigure);
            colors.remove(activeFigure);
            editPanel.updateLayers(figures);
            activeFigure = -1;
            repaint();
        }
    }

    /**
     * Draws points selected by user. First point is red.
     * @param g2d used to draw points on DrawPanel
*            @see Graphics2D
     */

    private void drawPoints(Graphics2D g2d) {
        for(int i = 0; i<xPos.size(); i++) {
            if(i == 0) {
                g2d.setPaint(Color.red);
                g2d.fillRect(xPos.get(i), yPos.get(i), 4, 4);
            }
            else {
                g2d.setPaint(Color.white);
                g2d.fillRect(xPos.get(i), yPos.get(i), 3, 3);
                g2d.setPaint(Color.black);
                g2d.drawRect(xPos.get(i), yPos.get(i), 3, 3);
            }
        }
    }

    /**
     * Draws figures from list and blue frame on selected figure.
     * @param g2d used to draw figures on DrawPanel
     *            @see Graphics2D
     */

    private void drawFigures(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        /**
         * Draw figures with specific color.
         */
        for(int i = 0; i<figures.size(); i++) {
            g2d.setPaint(colors.get(i));
            g2d.fill(figures.get(i));
            g2d.draw(figures.get(i));
        }

        /**
         * If any figure is selected, draw blue rectangle around it to mark figure as selected.
         */
        if(activeFigure != -1 && (actionPanel.isModifyEnabled() || actionPanel.isColorEnabled())) {
            Shape f = figures.get(activeFigure);
            int x = intValue(f.getBounds().getX());
            int y = intValue(f.getBounds().getY());
            int w = intValue(f.getBounds().getWidth());
            int h = intValue(f.getBounds().getHeight());
            g2d.setPaint(Color.BLUE);
            g2d.drawRect(x,y,w,h);
        }
    }

    /**
     * Runs functions to draw figures and points.
     * @param g used to draw on DrawPanel
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawFigures(g2d);
        drawPoints(g2d);

        editPanel.updateLayers(figures);
        if(activeFigure!=-1) editPanel.updateProperties(figures.get(activeFigure));
    }

    /**
     * Adds new circle to figures list when two points are chosen.
     * First point is the center of the circle, distance between second and first point is the radius of the circle.
     */
    private void addNewCircle() {
        /**
         * xDistance is the distance in horizontal axis.
         * yDistance is the distance in vertical axis.
         * Radius is calculated using Pythagorean theorem.
         */
        int xDistance = abs(xPos.get(0) - xPos.get(1));
        int yDistance = abs(yPos.get(0) - yPos.get(1));
        double radius = sqrt(pow(xDistance, 2) + pow(yDistance, 2));
        Circle circle = new Circle(xPos.get(0) - radius, yPos.get(0) - radius, 2 * radius, 2 * radius);

        figures.add(circle);
        clearPoints();
    }

    /**
     * Adds new rectangle to figures list when two points are chosen.
     * Line between two selected points is the diagonal of rectangle.
     */

    private void addNewRectangle() {

        int xPos0 = xPos.get(0);
        int xPos1 = xPos.get(1);
        int yPos0 = yPos.get(0);
        int yPos1 = yPos.get(1);

        int xPos = (xPos0 < xPos1 ? xPos0 : xPos1);
        int yPos = (yPos0 < yPos1 ? yPos0 : yPos1);
        int width = abs(xPos0 - xPos1);
        int height = abs(yPos0 - yPos1);

        Rect rectangle = new Rect(xPos,yPos,width,height);

        figures.add(rectangle);
        clearPoints();
    }

    /**
     * Adds new polygon to figures list when the last point selected by user is near the first point.
     * Polygon is drawn using GeneralPath.
     * @see GeneralPath
     */

    private void addNewPolygon() {

        GeneralPath polygon = new GeneralPath();
        polygon.moveTo(xPos.get(0), yPos.get(0));
        for (int i = 1; i < xPos.size(); i++) {
            polygon.lineTo(xPos.get(i), yPos.get(i));
        }
        polygon.closePath();

        figures.add(polygon);
        clearPoints();
    }

    /**
     * Returns the state of marking (enabled/disabled)
     * @return {@code true} if marking is enabled, {@code false} otherwise.
     */

    private boolean isMarkingEnabled() {
        return !disableMarking;
    }

    /**
     * Detects if mouse is over the figure and marks this figure as active when mouse is clicked.
     * Invokes function to update properties of selected figure.
     * @param e provides position of the cursor
     */
    private void markAsActive(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        for(int i=figures.size()-1; i>=0; i--) {
            Shape f = figures.get(i);
            if (f instanceof Circle && ((Circle) f).isHit(x,y)) { activeFigure = i; editPanel.updateProperties(f); break; }
            else if (f instanceof Rect && ((Rect) f).isHit(x,y)) { activeFigure = i; editPanel.updateProperties(f); break; }
            else if (f instanceof GeneralPath && f.contains(x,y)) { activeFigure = i; editPanel.updateProperties(f); break; }
            else {
                activeFigure = -1;
                editPanel.updateProperties(null);
            }
        }
    }


    /**
     * Sets the specific figure of given index as active.
     * @param activeFigure index of figure that should be set as active.
     */
    void setActiveFigure(int activeFigure) {
        this.activeFigure = activeFigure;
        this.repaint();
    }

    /**
     * Returns the index of active figure.
     * @return active figure index.
     */
    int getActiveFigure() {
        return activeFigure;
    }

    /**
     * ContextMenu provides popup menu for changing color and setting active figure on top of the others
     */

    class ContextMenu extends JPopupMenu {
        JMenuItem changeColor, setFirst;
        ContextMenu() {
            /**
             * Creates menu item for changing color of the figure and adds listener that opens dialog with RGB sliders.
             * @see ColorChooser
             */
            changeColor = new JMenuItem("Change color");
            changeColor.addActionListener(e -> {
                if(activeFigure != -1 && (actionPanel.isModifyEnabled() || actionPanel.isColorEnabled())) {
                    ColorChooser dialog = new ColorChooser();
                    dialog.setVisible(true);
                }
            });

            /**
             * Creates menu item for setting figure of the top and adds listener to move figure on the top of the list.
             */
            setFirst = new JMenuItem("Set on the top");
            setFirst.addActionListener(e -> {
                if(activeFigure != -1 && actionPanel.isModifyEnabled()) {
                    figures.add(figures.get(activeFigure));
                    figures.remove(activeFigure);
                    colors.add(colors.get(activeFigure));
                    colors.remove(activeFigure);

                    setActiveFigure(figures.size()-1);
                }
            });
            /**
             * If the color mode is enabled shows only option to change colors.
             * When modify mode is enabled shows option for changing color and setting figure on the top.
             */
            if(actionPanel.isColorEnabled()) {
                add(changeColor);
            }
            if(actionPanel.isModifyEnabled()) {
                add(changeColor);
                add(setFirst);
            }
        }
    }

    /**
     * Class that allows to open window with three sliders to control the color of figure using RGB.
     */
    class ColorChooser extends JDialog {
        JSlider sliderR, sliderG, sliderB;
        int r , g , b;

        ColorChooser() {
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setLayout(new GridLayout(6, 1));

            /**
             * Getting the color of figure that is modified.
             */
            r = colors.get(activeFigure).getRed();
            g = colors.get(activeFigure).getGreen();
            b = colors.get(activeFigure).getBlue();

            /**
             * Setting the initial positions of sliders depending on the color of the figure.
             */

            sliderR = getSlider(r);
            sliderG = getSlider(g);
            sliderB = getSlider(b);

            /**
             * Showing the sliders that allows user to change color.
             */

            add(new JLabel("Red:"));
            add(sliderR);
            add(new JLabel("Green:"));
            add(sliderG);
            add(new JLabel("Blue:"));
            add(sliderB);
            pack();
            setVisible(true);
        }

        /**
         * Creates the slider with given initial value.
         * @param initialVal initial value of slider (should be 0-255 for RGB colors)
         * @return horizontal slider with given initial value
         */
        private JSlider getSlider(int initialVal) {
            JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 255, initialVal);
            slider.addChangeListener(new SliderListener());
            return slider;
        }

        /**
         * Listener that changes color of the active figure depending on the change of the sliders value.
         */

        private class SliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                if (slider == sliderR) {
                    r = slider.getValue();
                } else if (slider == sliderG) {
                    g = slider.getValue();
                } else if (slider == sliderB) {
                    b = slider.getValue();
                }
                colors.set(activeFigure,new Color(r,g,b));
                DrawPanel.this.repaint();
            }
        }
    }

    /**
     * DrawingAdapter provides the option to draw figures using the mouse.
     * Shows context menu when mouse button that is pressed is popup trigger.
     * @see ContextMenu
     */
    class DrawingAdapter extends MouseAdapter {

        /**
         * Adds points to list that are later used to draw figures.
         * @param e provides position of the mouse
         */
        private void addVertices(MouseEvent e) {
            xPos.add(e.getX());
            yPos.add(e.getY());
        }

        /**
         * Enables marking other figures when mouse released (moving active figure ended).
         * Shows context menu.
         * @param e provides position of the mouse
         */

        public void mouseReleased(MouseEvent e) {
            disableMarking = false;
            if(e.isPopupTrigger()) {
                ContextMenu menu = new ContextMenu();
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
            repaint();
        }

        /**
         * Provides options for drawing points, figures, marking figures and showing context menu.
         * @param e provides position of the mouse
         */
        public void mousePressed(MouseEvent e) {
            /**
             * Shows context menu.
             */

            if(e.isPopupTrigger()) {
                ContextMenu menu = new ContextMenu();
                menu.show(e.getComponent(), e.getX(), e.getY());
            }

            /**
             * Marks figure as active if any of figures is hit.
             * Disables selecting other figures when already pressed and selected one.
             */
            if(isMarkingEnabled()) markAsActive(e);
            disableMarking = true;

            repaint();

            /**
             * Saves the initial position of the mouse.
             * @see MotionAdapter
             */
            xMove = e.getX();
            yMove = e.getY();

            /**
             * If drawing mode is enabled adds points to the list and draws figure when enough points is collected.
             */

            if (actionPanel.isDrawEnabled()) {

                if (actionPanel.drawRectangle()) {
                    addVertices(e);
                    if (xPos.size() == 2) {
                        addNewRectangle();
                        colors.add(editPanel.getColor());
                    }

                }
                if (actionPanel.drawPolygon()) {
                    if (xPos.size() >= 3 && abs(e.getX() - xPos.get(0)) < 10 && abs(e.getY() - yPos.get(0)) < 10) {
                        addNewPolygon();
                        colors.add(editPanel.getColor());
                    } else {
                        addVertices(e);
                    }
                }
                if (actionPanel.drawCircle()) {
                    addVertices(e);
                    if (xPos.size() == 2) {
                        addNewCircle();
                        colors.add(editPanel.getColor());
                    }
                }
            }

            /**
             * If color mode is enabled colors the selected figure with color chosen in editPanel.
             */
            if(actionPanel.isColorEnabled() && activeFigure != -1) {
                colors.set(activeFigure, editPanel.getColor());
            }
            repaint();
        }
    }

    /**
     * Provides option to move figures.
     */
    class MotionAdapter extends MouseAdapter {

        /**
         * Marks figure as active when mouse pressed.
         * @param e provides the position of the mouse.
         */
        public void mousePressed(MouseEvent e) {
            markAsActive(e);
            repaint();
        }

        /**
         * Enables marking other figures when moving selected figure ended.
         */

        public void mouseReleased(MouseEvent e) {
            disableMarking = false;
            repaint();
        }

        /**
         * Moves the figure depending on the mouse position.
         */
        public void mouseDragged(MouseEvent e) {
            if(isMarkingEnabled()) markAsActive(e);
            disableMarking = true;
            repaint();

            /**
             * If the modify mode is enabled calculates the distance of the mouse move.
             */

            if (actionPanel.isModifyEnabled()) {
                int deltaX = e.getX() - xMove;
                int deltaY = e.getY() - yMove;

                /**
                 * If any figure is selected figure is moved depending on the calculated distance.
                 * Initial position of the mouse is changed to the actual position of mouse.
                 */

                if(activeFigure != -1) {
                    Shape f = figures.get(activeFigure);
                    if (f instanceof Circle) {
                            ((Circle) f).moveX(deltaX);
                            ((Circle) f).moveY(deltaY);
                            xMove += deltaX;
                            yMove += deltaY;
                    }
                    if (f instanceof Rect) {
                            ((Rect) f).translate(deltaX, deltaY);
                            xMove += deltaX;
                            yMove += deltaY;
                    }
                    if (f instanceof GeneralPath) {
                            AffineTransform transformer = AffineTransform.getTranslateInstance(deltaX, deltaY);
                            ((GeneralPath) f).transform(transformer);
                            xMove += deltaX;
                            yMove += deltaY;
                    }
                }
                repaint();
            }
        }
    }

    /**
     * Provides option to scale figures.
     */
    class Scaling implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            /**
             * Scale only when modify is enabled and there is selected figure.
             */
            if (actionPanel.isModifyEnabled() && e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL && activeFigure != -1) {

                /**
                 * Getting the wheel rotation and active figure.
                 */
                double scaleRate = e.getWheelRotation()*0.1;
                Shape f = figures.get(activeFigure);

                /**
                 * Getting the size of figure.
                 */

                double fWidth = f.getBounds().getWidth();
                double fHeight = f.getBounds().getHeight();

                /**
                 * Scale the figure using methods according to the type of selected figure
                 */

                if (f instanceof Circle) {
                    ((Circle) f).resize(scaleRate);
                    repaint();
                }
                else if (f instanceof Rect) {
                    int growX = (int) round(fWidth*scaleRate);
                    int growY = (int) round(fHeight*scaleRate);
                    ((Rect) f).grow(-growX,-growY);
                    repaint();
                }
                else if (f instanceof GeneralPath) {
                    Point oldCenter = new Point(f.getBounds().getWidth(), f.getBounds().getHeight());
                    AffineTransform scaler = new AffineTransform();
                    scaler.scale(1-scaleRate,1-scaleRate);
                    ((GeneralPath) f).transform(scaler);

                    AffineTransform transformer = new AffineTransform();
                    Point newCenter = new Point(f.getBounds().getWidth(), f.getBounds().getHeight());
                    transformer.translate(oldCenter.getX()-newCenter.getX(), oldCenter.getY()-newCenter.getY());
                    ((GeneralPath) f).transform(transformer);
                    repaint();
                }

            }
        }
    }
}


