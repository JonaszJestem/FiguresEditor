import javax.swing.*;
import java.awt.*;

/**
 * EditorsFrame class provides GUI for drawing and editing simple figures.
 * EditorsFrame has menu for saving, opening and creating new files and 3 additional panels.
 * DrawPanel provides canvas for drawing figures,
 * ActionPanel provides buttons for drawing, modifying and coloring figures and deleting created figures,
 * EditPanel provides palette of colors, list of created figures and properties of active figures
 *
 * @version 1.0
 * @since 2017-04-20
 * @author Jonasz Wiacek
 */
class EditorsFrame extends JFrame {

    EditorsFrame() {
        super("Shapes Editor");

        /**
         * Set the layout of GUI, size and default close operation.
         */

        setLayout(new BorderLayout());
        setSize(new Dimension(880,640));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        /**
         * Adding panels to EditorsFrame.
         * @see MenuPanel
         * @see DrawPanel
         * @see ActionPanel
         * @see EditPanel
         */

        MenuPanel menuPanel = new MenuPanel();
        setJMenuBar(menuPanel);
        DrawPanel drawPanel = new DrawPanel();
        ActionPanel actionPanel = new ActionPanel();
        EditPanel editPanel = new EditPanel();

        /**
         * Creating connections between panels that need to cooperate.
         */
        drawPanel.setActionPanel(actionPanel);
        drawPanel.setEditPanel(editPanel);
        actionPanel.setDrawPanel(drawPanel);
        editPanel.setDrawPanel(drawPanel);
        menuPanel.setDrawPanel(drawPanel);

        /**
         * Placing panels in the frame.
         */
        add(actionPanel, BorderLayout.LINE_START);
        add(drawPanel, BorderLayout.CENTER);
        add(editPanel, BorderLayout.LINE_END);

        setVisible(true);

    }

    /**
     * Creates the GUI and shows it.
     */

    public static void main(String[] args) {
        EventQueue.invokeLater(EditorsFrame::new);
    }

}
