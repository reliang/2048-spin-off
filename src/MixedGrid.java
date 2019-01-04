import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;


public class MixedGrid implements Grid {
	
	/*
	 * provides grid, contains game logic
	 */
	private int size;
	private Tile[][] grid;
	private int score;
	
	public MixedGrid() {
		size = 4;
		score = 0;
		grid = new Tile[size][size];
	}
	
	public MixedGrid(int size) {
		this.size = size;
		score = 0;
		grid = new Tile[size][size];
	}
	
	/*
	 * method to generate a new box
	 * input: none, since box is generated randomly
	 * output: void, grid is changed directly
	 * 
	 * - must ensure box isn't generated in a spot that is taken
	 * - can generate white, black or num tiles
	 * - 81% chance of 2 tile, 9% chance of 4 tile
	 * - 5% chance of black tile, 5% chance of white tile
	 */
	public void generateBox() {
		int x = generateCoords();
		int y = generateCoords();
		double randomNum = Math.random();
		Tile b;
		// if repeated, continue generating
		while (existsBox(x, y)) {
			x = generateCoords();
			y = generateCoords();
		}
		if (randomNum < 0.81) {
			b = new NumTile(2, x, y);
		} else if (randomNum < 0.9) {
			b = new NumTile(4, x, y);
		} else if (randomNum < 0.95) {
			b = new BlackTile(x, y);
		} else {
			b = new WhiteTile(x, y);
		}
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
				Tile[] col = grid[i];
				Tile[] reversed = new Tile[size];
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
				Tile[] col = grid[i];
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
				Tile[] row = new Tile[size];
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
				Tile[] row = new Tile[size];
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
	public int comb(Tile[] arr) {
		if (arr == null || arr[0] == null) {
			throw new IllegalArgumentException("array or first box is null!");
		}
		int nbindex = nextBox(arr);
		int spaces = 0;
		// combine what is needed, increment 1 extra space if killed
		switch (arr[0].getType()) {
		case NUMTILE:
			NumTile currNum = (NumTile) arr[0];
			if (nbindex != -1) {
				switch (arr[nbindex].getType()) {
				case NUMTILE:
					NumTile num = (NumTile) arr[nbindex];
					if (currNum.getv() == num.getv() && !num.getKill()
							&& !num.getCombine()) {
						currNum.kill();
						num.combine();
						spaces++;
					}
					break;
				case WHITETILE: 
					WhiteTile white = (WhiteTile) arr[nbindex];
					if (!white.getKill()) {
						white.kill();
						currNum.combine();
					}
					break;
				}
			}
			break;
		case BLACKTILE: 
			BlackTile currBlack = (BlackTile) arr[0];
			if (currBlack.getTurns() == 4) {
				currBlack.kill();
			}
			if (nbindex != -1) {
				switch (arr[nbindex].getType()) {
				case WHITETILE:
					WhiteTile white = (WhiteTile) arr[nbindex];
					if (!white.getKill()) {
						currBlack.kill();
						white.kill();
					}
				}
			}
			break;
		case WHITETILE:
			WhiteTile currWhite = (WhiteTile) arr[0];
			// combine if next non-null box is the same number
			nbindex = nextBox(arr);
			if (nbindex != -1) {
				switch (arr[nbindex].getType()) {
				case NUMTILE:
					NumTile num = (NumTile) arr[nbindex];
					if (!num.getKill() && !num.getCombine()) {
						currWhite.kill();
						num.combine();
						spaces++;
					}
					break;
				case BLACKTILE:
					BlackTile black = (BlackTile) arr[nbindex];
					if (!black.getKill()) {
						black.kill();
						currWhite.kill();
					}
					break;
				}
			}
			break;
		}
		// then calculate number spaces to move starting from second element
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] == null) {
				spaces++;
			} else if (arr[i].getKill()) {
				spaces++;
			}
		}
		return spaces;
	}
	
	/*
	 * helper method for comb that finds # steps to next non-null box
	 */
	private int nextBox(Tile[] arr) {
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
		Tile[][] newGrid = new Tile[size][size];
		int changeScore = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Tile curr = grid[i][j];
				if (curr != null && !curr.getKill()) {
					// get dx, dy
					int dx = curr.getdx();
					int dy = curr.getdy();
					// update score
					switch (curr.getType()) {
					case NUMTILE:
						NumTile currNum = (NumTile) curr;
						changeScore += 2 * currNum.getdv();
						break;
					}
					// reset dx, dy, dv
					curr.update();
					// put Box in its new position
					newGrid[i + dx][j + dy] = curr;
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
			// check for vertical moves
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size - 1; j++) {
					switch(grid[i][j].getType()) {
					case NUMTILE:
						switch (grid[i][j + 1].getType()) {
						case NUMTILE:
							NumTile currNum = (NumTile) grid[i][j];
							NumTile nextNum = (NumTile) grid[i][j + 1];
							if (currNum.getv() == nextNum.getv()) {
								return false;
							}
							break;
						case WHITETILE:
							return false;
						}
						break;
					case WHITETILE:
						switch(grid[i][j + 1].getType()) {
						case NUMTILE:
							return false;
						case BLACKTILE:
							return false;
						}
						break;
					}
				}
			}
			// check for horizontal moves
			for (int j = 0; j < size; j++) {
				for (int i = 0; i < size - 1; i++) {
					switch(grid[i][j].getType()) {
					case NUMTILE:
						switch (grid[i + 1][j].getType()) {
						case NUMTILE:
							NumTile currNum = (NumTile) grid[i][j];
							NumTile nextNum = (NumTile) grid[i + 1][j];
							if (currNum.getv() == nextNum.getv()) {
								return false;
							}
							break;
						case WHITETILE:
							return false;
						}
						break;
					case WHITETILE:
						switch(grid[i + 1][j].getType()) {
						case NUMTILE:
							return false;
						case BLACKTILE:
							return false;
						}
						break;
					}
				}
			}
			return true;
		}
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
	 * draw method used in JGrid, boolean signals if it is done drawing
	 */
	public boolean draw(Graphics g) {
		boolean finished = true;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Tile curr = grid[i][j];
				if (curr != null) {
					boolean b = curr.draw(g);
					finished = finished && b;
				}
			}
		}
		
		return finished;
	}
}
