import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;


public class Game implements Runnable {
	public void run() {
		final JFrame frame = new JFrame("2048");
		frame.setLocation(300, 300);
		
		// Status panel
		final JPanel status_panel = new JPanel();
		frame.add(status_panel, BorderLayout.NORTH);
		final JLabel status = new JLabel("Score: 0");
		status_panel.add(status);
		
		// Main playing area
		final JGrid grid = new JGrid(status);
		frame.add(grid, BorderLayout.CENTER);

		// Reset button
		final JPanel control_panel = new JPanel();
		frame.add(control_panel, BorderLayout.SOUTH);
		final JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.reset();
            }
        });
        control_panel.add(reset);
        
        // High Scores button
		final JButton highScores = new JButton("High Scores");
        highScores.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JOptionPane.showMessageDialog(grid, grid.message());
            	grid.requestFocusInWindow();
            }
        });
        control_panel.add(highScores);
        
        // Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		grid.reset();
	}

	/**
	 * Main method run to start and run the game. Initializes the GUI elements
	 * specified in Game and runs it. IMPORTANT: Do NOT delete! You MUST include
	 * this in your final submission.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
    }
}
