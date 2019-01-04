import java.awt.Graphics;


public interface Grid {
	/*
	 * subclasses include
	 * - NumGrid
	 * - MixedGrid
	 * 
	 * offers two modes to the game
	 */
	
	// generates box at random coordinate
	public void generateBox();
	
	// computes dx, dy, dv (if applicable)
	public void prep(Direction d);
	
	// updates tiles according to dx, dy, dv (if applicable)
	public void step();
	
	// checks whether game is over
	public boolean gameOver();
	
	// checks if the grid is full
	public boolean isFull();
	
	// gets current score
	public int getScore();
	
	// draws grid, returns whether it is done drawing
	public boolean draw(Graphics g);
}
