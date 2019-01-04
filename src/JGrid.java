import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;


@SuppressWarnings("serial")
public class JGrid extends JPanel {
	/*
	 * draws grid as JPanel, puts together the logic, handles IO
	 */
	
	private static final int GRID_WIDTH = 400;
	private static final int GRID_HEIGHT = 400;
	
	private boolean gameOver;
	private JLabel status;
	private String name;
	private static final int scoreLimit = 10;
	private int scoreCount;
	private Map<Integer, ArrayList<String>> scores = 
		new TreeMap<Integer, ArrayList<String>>();
	private static final String file = "files/highScores.txt";
	Grid grid;
	
	// for animation
	private static final int INTERVAL = 5;
	private Timer timer;
	private boolean animating;
	
	public JGrid(JLabel status) {
		
		// Border around grid
		setBorder(BorderFactory.createLineBorder(Color.ORANGE));
		
		// Timer for animation
		timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
		
		// Enable keyboard focus on the court area.
		// When this component has the keyboard focus, key events are handled by
		// its key listener.
		setFocusable(true);
		
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (animating) {
					// do nothing
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) { 
					animate(Direction.LEFT);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					animate(Direction.RIGHT);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					animate(Direction.DOWN);
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					animate(Direction.UP);
				}
			}
		});
		
		grid = new NumGrid();
		
		try {
			readScores(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		this.status = status;
	}

	/*
	 * method that resets game
	 */
	public void reset() {
		String name = JOptionPane.showInputDialog(this, "Name: ", "Player");
		// make sure no empty names
		while (name == null) {
			name = JOptionPane.showInputDialog(this, "Name: ", "Player");
		}
		while (name.isEmpty()) {
			name = JOptionPane.showInputDialog(this, "Name: ", "Player");
		}
		this.name = name;
		Object[] options = { "Original", "Mixed" };
		int choice = JOptionPane.showOptionDialog(this, "Game Mode",
				"Selection", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (choice == 0) {
			grid = new NumGrid();
		} else {
			grid = new MixedGrid();
			JOptionPane.showMessageDialog(this, "Black tile - does not cancel " +
					"for 5 turns, then disappears on its own. \n" +
					"White tile - doubles any " +
					"number and can cancel black tiles", "Instructions",
					JOptionPane.PLAIN_MESSAGE);
		}
		gameOver = false;
		grid.generateBox();
		status.setText("Score: 0");
		repaint();
		
		// Make sure that this component has the keyboard focus
        requestFocusInWindow();
	}

	/*
	 * helper function to handle key pressed
	 */
	private void animate(Direction d) {
		if (!gameOver) {
			grid.prep(d);
			
			animating = true;
			timer.start();
		}
	}

	private void nextTurn() {

		grid.step();
		if (!grid.isFull()) {
			grid.generateBox();
			repaint();
		}
		status.setText("Score: " + grid.getScore());

		if (grid.gameOver()) {
			gameOver = true;
			addScore(grid.getScore());
			try {
				writeScores(file);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("write file failed");
			}
			JOptionPane.showMessageDialog(this, "Game Over");
		}

	}

	/**
	 * This method is called every time the timer defined in the constructor
	 * triggers.
	 */
	void tick() {
		if (animating) {
			repaint();
		} else {
			timer.stop();
			nextTurn();
		}
	}

	/*
	 * function to read score format of file: "score,name" multiple scores
	 * input: file of high scores
	 * output: void, stores all scores to TreeMap
	 * 
	 * invariants:
	 * - same name, same score can only appear once
	 * - scores sorted primarily by value, secondarily by order added
	 * - first to drop lowest score
	 * - if lowest score is earned by multiple users, last-added is dropped
	 */
	public void readScores(String filename) throws FileNotFoundException,
			IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String s = in.readLine();
		int count = 0;
		while (s != null) {
			int comma = s.indexOf(',');
			if (comma != -1) {
				// score
				String left = s.substring(0, comma);
				// name
				String right = s.substring(comma + 1, s.length());
				String ltrimmed = left.trim();
				String rtrimmed = right.trim();
				int currScore = Integer.parseInt(ltrimmed);
				ArrayList<String> names;
				if (scores.containsKey(currScore)) {
					names = scores.get(currScore);
					// if same score, same name, don't add score
					if (!names.contains(rtrimmed)) {
						names.add(rtrimmed);
						scores.put(currScore, names);
						count++;
					}
				} else {
					names = new ArrayList<String>();
					names.add(rtrimmed);
					scores.put(currScore, names);
					count++;
				}				
			}
			s = in.readLine();
		}
		scoreCount = count;
		// remove excess entries
		// if multiple names to least score, remove last-added name
		while (scoreCount > scoreLimit) {
			int leastScore = ((TreeMap<Integer, ArrayList<String>>) scores)
					.firstKey();
			ArrayList<String> names = scores.get(leastScore);
			if (names.size() > 1) {
				names.remove(names.size() - 1);
				scores.put(leastScore, names);
			} else {
				scores.remove(leastScore);
			}
			scoreCount--;
		}
	}
	
	/*
	 * function to add score
	 * input: score to put in, number scores in collection
	 * output: void, changes collection of scores
	 */
	public void addScore(int score) {
		ArrayList<String> names;
		// if score exists, add to same mapping
		if (scores.containsKey(score)) {
			// if same score, same name, don't add score
			names = scores.get(score);
			if (names != null && !names.contains(name)) {
				names.add(name);
				scoreCount++;
			}
		} else {
			names = new ArrayList<String>();
			names.add(name);
			scoreCount++;
		}
		scores.put(score, names);
		// limit num scores to scoreLimit
		while (scoreCount > scoreLimit) {
			int leastScore = ((TreeMap<Integer, ArrayList<String>>) scores)
					.firstKey();
			ArrayList<String> arr = scores.get(leastScore);
			if (arr.size() > 1) {
				arr.remove(names.size() - 1);
				scores.put(leastScore, arr);
			} else {
				scores.remove(leastScore);
			}
			scoreCount--;
		}
	}
	
	/*
	 * function to write scores
	 */
	public void writeScores(String filename) throws IOException {
		BufferedWriter b = new BufferedWriter(new FileWriter(filename));
		for (Map.Entry<Integer, ArrayList<String>> entry : scores.entrySet()) {
			int score = entry.getKey();
			ArrayList<String> names = entry.getValue();
			for (String name : names) {
				b.append(score + "," + name + "\n");
			}
		}
		b.close();
	}
	
	/*
	 * creates high scores page
	 */
	public String message() {
		String str = "";
        int count = scoreCount;
        for (Map.Entry<Integer, ArrayList<String>> entry : scores.entrySet()) {
			int score = entry.getKey();
			ArrayList<String> names = entry.getValue();
			for (String name : names) {
				str = count + ". " + name + ": " + score + "\n" + str;
				count--;
			}
		}
        return str;
	}
	
	/*
	 * IO test methods
	 */
	public Map<Integer, ArrayList<String>> getScores() {
		return new TreeMap<Integer, ArrayList<String>>(scores);
	}
	
	public int getScoreCount() {
		return scoreCount;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * functions to implement JPanel
	 */
	@Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid.draw(g)) {
        	animating = false;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GRID_WIDTH, GRID_HEIGHT);
    }	
}
