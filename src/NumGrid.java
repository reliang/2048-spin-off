import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;


public class NumGrid implements Grid {
	
	/*
	 * provides grid, contains game logic
	 */
	private int size;
	private NumTile[][] grid;
	private int score;
	
	public NumGrid() {
		size = 4;
		score = 0;
		grid = new NumTile[size][size];
	}
	
	public NumGrid(int size) {
		this.size = size;
		score = 0;
		grid = new NumTile[size][size];
	}
	
	/*
	 * method to generate a new box
	 * input: none, since box is generated randomly
	 * output: void, grid is changed directly
	 * 
	 * - must ensure box isn't generated in a spot that is taken
	 */
	public void generateBox() {
		int value;
		if (Math.random() < 0.9) {
			value = 2;
		} else {
			value = 4;
		}
		int x = generateCoords();
		int y = generateCoords();
		// if repeated, continue generating
		while (existsBox(x, y)) {
			x = generateCoords();
			y = generateCoords();
		}
		NumTile b = new NumTile(value, x, y);
		grid[x][y] = b;
	}
	
	/*
	 * helper method for generateBox that generates coordinates
	 */
	private int generateCoords() {
		return (int) Math.floor(Math.random() * size);
	}
	
	/*
	 * method that computes dv, dx, dy of each box in grid based on swipe
	 * input: Direction of swipe
	 * output: void, everything is stored to grid
	 * 
	 * - breaks game into columns/rows depending on swipe direction
	 * - orients each column/row so swipe direction is towards larger index
	 * - feeds array into helper(comb) to compute dv, dx, dy
	 * - order of feeding: last element, last 2 elements, last 3, all 4
	 * - doesn't feed in an array if the first box is null
	 * - helper computes for the first element in the array its dx/dy
	 */
	public void prep(Direction d) {
		if (d == null) {
			return;
		}
		switch (d) {
		case UP:
			// columns: bottom to top
			for (int i = 0; i < size; i++) {
				NumTile[] col = grid[i];
				NumTile[] reversed = new NumTile[size];
				// reverse so swipe direction is toward larger index
				for (int j = 0; j < size; j++) {
					reversed[(size - 1) - j] = col[j];
				}
				for (int j = size - 1; j >= 0; j--) {
					if (reversed[j] != null) {
						int spaces = comb(Arrays.copyOfRange(reversed, j, size));
						// up is negative since y increases going down
						reversed[j].setdy(-spaces);
					}
				}
			}
			break;
		case DOWN:
			// columns: top to bottom
			for (int i = 0; i < size; i++) {
				NumTile[] col = grid[i];
				// swipe direction is naturally toward larger index
				for (int j = size - 1; j >= 0; j--) {
					if (col[j] != null) {
						int spaces = comb(Arrays.copyOfRange(col, j, size));
						col[j].setdy(spaces);
					}
				}
			}
			break;
		case LEFT:
			// rows: right to left
			for (int j = 0; j < size; j++) {
				NumTile[] row = new NumTile[size];
				for (int i = 0; i < size; i++) {
					// fill-in in reversed order
					// so swipe direction is towards larger index
					row[(size - 1) - i] = grid[i][j];
				}
				for (int i = size - 1; i >= 0; i--) {
					if (row[i] != null) {
						int spaces = comb(Arrays.copyOfRange(row, i, size));
						row[i].setdx(-spaces);
					}
				}
			}
			break;
		case RIGHT:
			// rows: left to right
			for (int j = 0; j < size; j++) {
				NumTile[] row = new NumTile[size];
				for (int i = 0; i < size; i++) {
					// swipe direction is naturally towards larger index
					row[i] = grid[i][j];
				}
				for (int i = size - 1; i >= 0; i--) {
					if (row[i] != null) {
						int spaces = comb(Arrays.copyOfRange(row, i, size));
						row[i].setdx(spaces);
					}
				}
			}
			break;
		}
	}
	
	/*
	 * method used in prep, determines dv, dx, dy for a row/column
	 * input: Box[], finds # moves for first box in array using remaining boxes
	 * output: the number of spaces to move box
	 * 
	 * order of feeding a row/column in:
	 * - feed last element (if it isn't null)
	 * - feed last 2 elements (if 2nd to last element isn't null)
	 * - feed last 3 elements (if 3rd to last element isn't null)
	 * - feed all 4 elements (if first element isn't null)
	 */
	public int comb(NumTile[] arr) {
		if (arr == null || arr[0] == null) {
			throw new IllegalArgumentException("array or first box is null!");
		}	
		// combine if next non-null box is the same number
		int nb = nextBox(arr);
		if (nb != -1) {
			if (arr[0].getv() == arr[nb].getv() && !arr[nb].getKill()) {
				arr[0].kill();
				arr[nb].combine();
			}
		}
		// then calculate number spaces to move starting from second element
		int spaces = 0;
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] == null) {
				spaces++;
			} else if (arr[i].getKill()) {
				spaces++;
			}
		}
		// move 1 more space if combined
		if (arr[0].getKill()) {
			return spaces + 1;
		}
		return spaces;
	}
	
	/*
	 * helper method for comb that finds # steps to next non-null box
	 */
	private int nextBox(NumTile[] arr) {
		// if only includes first box, no next box
		if (arr.length == 1) {
			return -1;
		}
		// starts from the second element in array
		int nb = 1;
		while (nb < arr.length && arr[nb] == null) {
			// if even the last element is null
			if (nb == arr.length - 1 && arr[nb] == null) {
				return -1;
			}
			nb++;
		}
		return nb;
	}
	
	/*
	 * method that updates grid according to dv, dx, dy
	 * input: none
	 * output: void, grid is changed directly
	 * 
	 * Position: repositions each box to the position of (x + dx, y + dy)
	 * 
	 * Score:
	 * - score is value of the tile that results from combining
	 * - ex. combining two 2-tiles gives a score of 4
	 * - double dv = combined score
	 * - adding 2*dv from each tile gives overall change in score
	 */
	public void step() {
		NumTile[][] newGrid = new NumTile[size][size];
		int changeScore = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				NumTile curr = grid[i][j];
				if (curr != null && !curr.getKill()) {
					// get dx, dy, dv
					int dx = curr.getdx();
					int dy = curr.getdy();
					int dv = curr.getdv();
					// reset dx, dy, dv
					curr.update();
					// put Box in its new position
					newGrid[i + dx][j + dy] = curr;
					changeScore += 2 * dv;
				}
			}
		}
		score += changeScore;
		grid = newGrid;
	}
	
	/*
	 * method that signals the end of the game
	 * input: none
	 * output: boolean, true if there are no more possible moves
	 */
	public boolean gameOver() {
		// grid is not full
		if (!isFull()) {
			return false;
		} else {
			// check for vertical repeats
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size - 1; j++) {
					if (grid[i][j].getv() == grid[i][j + 1].getv()) {
						return false;
					}
				}
			}
			// check for horizontal repeats
			for (int j = 0; j < size; j++) {
				for (int i = 0; i < size - 1; i++) {
					if (grid[i][j].getv() == grid[i + 1][j].getv()) {
						return false;
					}
				}
			}
			return true;
		}
	}
	
	/*
	 * methods for testing
	 */
	public int numBox() {
		int count = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (grid[i][j] != null) {
					count++;
				}
			}
		}
		return count;
	}
	
	public void setBox(int v, int x, int y) {
		grid[x][y] = new NumTile(v, x, y);
	}
	
	public NumTile getBox(int x, int y) {
		return grid[x][y];
	}
	
	public boolean existsBox(int x, int y) {
		if (grid[x][y] != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isFull() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (grid[i][j] == null) {
					return false;
				}
			}
		}
		return true;
	}
	
	public int getScore() {
		return score;
	}
	
	/*
	 * draw method used in JGrid
	 */
	public boolean draw(Graphics g) {
		boolean finished = true;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				NumTile curr = grid[i][j];
				if (curr != null) {
					boolean b = curr.draw(g);
					finished = finished && b;
				}
			}
		}
		return finished;
	}
}
