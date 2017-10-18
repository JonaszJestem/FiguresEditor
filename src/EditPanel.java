import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * EditPanel provides panel for choosing color, list of drawn figures and properties of selected figure.
 */

class EditPanel extends JPanel {

    /**
     * activeColor is label that shows us active color
     * color contains value of active color
     * list contains all the layers
     * layers contains list of figures
     *
     * drawPanel is the reference to drawPanel
     * figureProperties is a panel that draws active figure properties
     * @see FigureProperties
     */

    private JLabel activeColor;
    private Color color = Color.black;

    private final JList<String> list;
    private final DefaultListModel<String> layers;

    private DrawPanel drawPanel;
    private FigureProperties figureProperties;

    EditPanel() {
        super();
        setLayout(new GridLayout(3,1));
        Dimension d = this.getPreferredSize();
        d.width = 200;
        setPreferredSize(d);
        setBorder(BorderFactory.createLineBorder(Color.black));

        /**
         * Create panel with color palette.
         */
        ColorPanel colorPanel = new ColorPanel();

        /**
         * Create list with layers and add scrollbar.
         */
        layers = new DefaultListModel<>();
        list = new JList(layers);
        list.addMouseListener(new Marking());
        ScrollPane scrollList = new ScrollPane();
        scrollList.add(list);

        /**
         * Create panel with figure properties.
         */
        figureProperties = new FigureProperties();

        add(colorPanel);
        add(scrollList);
        add(figureProperties);
    }

    /**
     * Updates layers' list when figures list changed.
     * @param figures figures list
     */
    void updateLayers(ArrayList<Shape> figures) {
        layers.clear();
        for(Shape f: figures) {
            String name;
            if (f.getClass().getName().equals("java.awt.geom.GeneralPath")) {
                name = "Polygon";
            } else {
                name = f.getClass().getName();
            }
            layers.addElement(name);
        }
    }

    /**
     * Updates properties of active figure when active figure changed
     * @param f reference to active figure
     */

    void updateProperties(Shape f) {
        this.figureProperties.updateProperties(f);
    }

    /**
     * Updates active color when new color is selected from palette.
     */
    private void updateActiveColor() {
        activeColor.setBackground(color);
    }

    /**
     * Returns active color
     * @return {@code color} active color
     */

    public Color getColor() {
        return color;
    }

    /**
     * Sets the reference to drawPanel
     * @param drawPanel reference to drawPanel
     */

    void setDrawPanel(DrawPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    /**
     * Class enables marking figures by clicking on the elements of list of figures.
     */

    class Marking extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int selected = list.getSelectedIndex();
            drawPanel.setActiveFigure(selected);
            list.setSelectedIndex(selected);
        }
    }

    /**
     * Class enables to create panel with properties of selected figure
     */

    class FigureProperties extends JPanel {
        JLabel figureType,height,width,red,green,blue;

        FigureProperties() {
            setLayout(new GridLayout(6,1));
            /**
             * Properties of figure
             */
            figureType = new JLabel("Figure Type: ", SwingConstants.CENTER);
            height = new JLabel("Height: ", SwingConstants.CENTER);
            width = new JLabel("Width: ", SwingConstants.CENTER);
            red = new JLabel("Red: ", SwingConstants.CENTER);
            green = new JLabel("Green: ", SwingConstants.CENTER);
            blue = new JLabel("Blue: ", SwingConstants.CENTER);

            add(figureType);
            add(height);
            add(width);
            add(red);
            add(green);
            add(blue);
        }

        /**
         * Update properties when given another active figure
         * @param f reference to active figure, null if no figure is selected.
         */
        void updateProperties(Shape f) {
            if(f != null) {
                this.figureType.setText("Figure Type: " + f.getClass().getName());
                if(f.getClass().getName() == "java.awt.geom.GeneralPath") {
                    this.figureType.setText("Figure Type: Polygon");
                }
                this.height.setText("Height: " + f.getBounds().getHeight());
                this.width.setText("Width: " + f.getBounds().getWidth());
                this.red.setText("Red: " + drawPanel.getColors().get(drawPanel.getActiveFigure()).getRed());
                this.green.setText("Green: " + drawPanel.getColors().get(drawPanel.getActiveFigure()).getGreen());
                this.blue.setText("Blue: " + drawPanel.getColors().get(drawPanel.getActiveFigure()).getBlue());
            }
            else {
                this.figureType.setText("Figure Type: -");
                this.height.setText("Height: -");
                this.width.setText("Width: -");
                this.red.setText("Red: -");
                this.green.setText("Green: -");
                this.blue.setText("Blue: -");
            }
        }
    }

    /**
     * Class that allows to create panel with color palette as image.
     */
    class ColorPanel extends JPanel {
        ColorPanel() {
            setLayout(new BorderLayout());
            activeColor = new JLabel("<html><font color=white>Active Color</font></html>", JLabel.CENTER);
            activeColor.setBackground(color);
            activeColor.setOpaque(true);

            ColorPalette cp = new ColorPalette();

            add(activeColor, BorderLayout.PAGE_START);
            add(cp, BorderLayout.CENTER);
        }

    }

    /**
     * Class that read color palette from image and allows getting colors from pixels.
     */
    class ColorPalette extends JPanel {

        private BufferedImage image;

        ColorPalette() {
            /**
             * Try to read image from file ./colors.jpg
             */
            try {
                image = ImageIO.read(getClass().getResourceAsStream("colors.jpg"));
            }
            catch(IOException e) {
                System.out.println("No image found.");
            }

            /**
             * Add listeners for getting colors from pixels of image when mouse is pressed on them.
             */
            addMouseMotionListener(new GettingColor());
            addMouseListener(new GettingColor());
        }

        class GettingColor extends MouseAdapter {
            /**
             * Gets the color from selected pixel, updates active color.
             * @param e provides position of mouse click.
             */
            public void mousePressed(MouseEvent e) {
                if(e.getX()<image.getWidth() && e.getY() < image.getHeight()) {
                    color = new Color(image.getRGB(e.getX() * 10, e.getY() * 10));
                    updateActiveColor();
                    drawPanel.repaint();
                }
            }

            public void mouseDragged(MouseEvent e) {
                if(e.getX()<image.getWidth() && e.getY() < image.getHeight()) {
                    color = new Color(image.getRGB(e.getX() * 10, e.getY() * 10));
                    updateActiveColor();
                    drawPanel.repaint();
                }
            }
        }

        /**
         * Paints scaled image of color palette
         * @param g used to print image
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            /**
             * Calculating scale of image
             * 200.0 is the width of EditPanel
             */
            double scale = 200.0/image.getWidth();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            AffineTransform at = AffineTransform.getScaleInstance(scale,scale);
            g2d.drawRenderedImage(image, at);
        }
    }
}
