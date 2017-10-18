import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class provides MenuBar that allows to create new files, open, save and exit
 */
class MenuPanel extends JMenuBar {

    private DrawPanel drawPanel;
    private JFileChooser fc;
    private int option;

    MenuPanel() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem newFile = new JMenuItem("New File");
        JMenuItem openFile = new JMenuItem("Open File");
        JMenuItem saveFile = new JMenuItem("Save File");
        JMenuItem saveAsImage = new JMenuItem("Save as image");
        JMenuItem exitProgram = new JMenuItem("Exit");

        /**
         * Prompt user to save file before doing any action if there are any drawn figures.
         * Save if user agreed then proceed with selected action
         * Proceed with action without saving if user didn't agree to save file
         * Do nothing if user cancelled action
         */

        newFile.addActionListener(e -> {
            if(drawPanel.getFigures().size()>0) {
                option = JOptionPane.showConfirmDialog(null,"Do you want to save changes?");

                if (option == 0) {
                    saveFile();
                    drawPanel.clearFigures();
                }
                if (option == 1) {
                    drawPanel.clearFigures();
                }
            }
        });

        saveFile.addActionListener(e -> saveFile());

        saveAsImage.addActionListener(e -> saveAsImage());

        openFile.addActionListener(e -> {
            if(drawPanel.getFigures().size()>0) {
                option = JOptionPane.showConfirmDialog(null, "Do you want to save changes?");

                if (option == 0) {
                    saveFile();
                    drawPanel.clearFigures();
                    openFile();
                }
                if (option == 1) {
                    drawPanel.clearFigures();
                    openFile();
                }
            }
            else {
                openFile();
            }
        });

        exitProgram.addActionListener(e -> {
            if(drawPanel.getFigures().size()>0) {
                option = JOptionPane.showConfirmDialog(null, "Do you want to save changes?");

                if (option == 0) {
                    saveFile();
                    System.exit(0);
                }
                if (option == 1) {
                    System.exit(0);
                }
            }
            else {
                System.exit(0);
            }
        });

        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About...");

        about.addActionListener(e -> JOptionPane.showMessageDialog(null, "Author: Jonasz Wiacek"));

        /**
         * Adds components to menu bar.
         */

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(saveAsImage);
        fileMenu.add(exitProgram);

        helpMenu.add(about);

        add(fileMenu);
        add(helpMenu);

    }

    /**
     * Sets reference to drawPanel
     * @param drawPanel reference to drawPanel
     */
    void setDrawPanel(DrawPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    /**
     * Opens selected file
     */
    private void openFile() {
        fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);

        /**
         * If user chose the file proceeds with reading lines from file.
         * Creates proper figures with properties read from file.
         * Saves figures into list with shapes and uses method setFigures() from DrawPanel to show figures on drawPanel
         */
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                FileReader fr = new FileReader(fc.getSelectedFile());
                BufferedReader br = new BufferedReader(fr);
                String currentFigure;
                String[] params;
                ArrayList<Shape> readFigures = new ArrayList<>();
                ArrayList<Color> readColors = new ArrayList<>();
                int i;

                while((currentFigure = br.readLine()) != null) {

                    i=0;
                    params = currentFigure.split(" ");

                    if(params[i].equals("Circle")) {
                        i++;
                        double x = Double.parseDouble(params[i++]);
                        double y = Double.parseDouble(params[i++]);
                        double w = Double.parseDouble(params[i++]);
                        double h = Double.parseDouble(params[i++]);
                        readColors.add(new Color(Integer.parseInt(params[i])));
                        readFigures.add(new Circle(x,y,w,h));
                    }
                    if(params[i].equals("Rect")) {
                        i++;
                        int x = Integer.parseInt(params[i++].replace(".0", ""));
                        int y = Integer.parseInt(params[i++].replace(".0", ""));
                        int w = Integer.parseInt(params[i++].replace(".0", ""));
                        int h = Integer.parseInt(params[i++].replace(".0", ""));
                        readColors.add(new Color(Integer.parseInt(params[i])));
                        readFigures.add(new Rect(x,y,w,h));
                    }
                    if(params[i].equals("Polygon")) {
                        i++;
                        ArrayList<Integer> x = new ArrayList<>();
                        ArrayList<Integer> y = new ArrayList<>();
                        int N = Integer.parseInt(params[i++]);
                        for(int j=0; j < N; j++) {
                            x.add(Integer.parseInt(params[i++]));
                        }
                        for(int j=0; j < N; j++) {
                            y.add(Integer.parseInt(params[i++]));
                        }

                        GeneralPath polygon = new GeneralPath();
                        polygon.moveTo(x.get(0), y.get(0));
                        for (int j = 1; j < x.size(); j++) {
                            polygon.lineTo(x.get(j), y.get(j));
                        }
                        polygon.closePath();

                        readFigures.add(polygon);
                        readColors.add(new Color(Integer.parseInt(params[i])));
                    }
                }

                drawPanel.setFigures(readFigures, readColors);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);

        /**
         * If user selects the file to save figures in proceed with saving figures into file
         * Each line contains:
         * - type of figure
         * - vertices of figure
         * - color of figure
         */

        if (returnVal == JFileChooser.APPROVE_OPTION) try {
            ArrayList<Shape> figures = drawPanel.getFigures();


            FileWriter fw = new FileWriter(fc.getSelectedFile());
            for(int i = 0; i < figures.size(); i++) {
                Shape f = figures.get(i);
                String color = Integer.toString(drawPanel.getColors().get(i).getRGB());

                if (f instanceof Circle) {
                    Circle c = (Circle) f;
                    String params = c.getClass().getName() + " " + c.getX() + " " + c.getY() + " " + c.getHeight() + " " + c.getWidth() + " " + color + System.lineSeparator();
                    fw.write(params);
                }
                else if (f instanceof Rect) {
                    Rect r = (Rect) f;
                    String params = r.getClass().getName() + " " + r.getX() + " " + r.getY() + " " + r.getHeight() + " " + r.getWidth() + " " + color + System.lineSeparator();
                    fw.write(params);
                } else if (f instanceof GeneralPath){
                    fw.write(getPoints((GeneralPath)f) +  color + System.lineSeparator());
                }
            }

            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void saveAsImage() {

        fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            BufferedImage image = new BufferedImage(drawPanel.getWidth(), drawPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            drawPanel.paint(g);
            File fileName = new File(fc.getSelectedFile() + ".png");
            try {
                ImageIO.write(image, "png", fileName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }



    }

    /**
     * Gets the vertices from polygon used to save polygon properties into file
     * @param path polygon to get vertices from
     * @return string with vertices separated with space
     */

    private String getPoints(GeneralPath path) {
        ArrayList<Point> pointList = new ArrayList<>();
        double[] cords = new double[6];

        for (PathIterator it = path.getPathIterator(null); !it.isDone(); it.next()) {
            it.currentSegment(cords);
            int x = ((Double) cords[0]).intValue();
            int y = ((Double) cords[1]).intValue();
            pointList.add(new Point(x, y));
        }

        StringBuilder sb = new StringBuilder("Polygon " + pointList.size() + " ");

        for (Point p : pointList) {
            sb.append(p.getX()).append(" ");
        }
        for (Point p : pointList) {
            sb.append(p.getY()).append(" ");
        }

        return sb.toString();
    }
}