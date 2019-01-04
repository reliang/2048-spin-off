import java.awt.Graphics;


public abstract class Tile {
	/*
	 * abstract class for tile, includes subclasses
	 * - NumTile (normal number box)
	 * - WhiteTile (white tile that doubles every number it touches)
	 * - BlackTile (black tile that doesn't cancel out for 5 turns)
	 */
	
	private int x;
	private int y;
	private int dx;
	private int dy;
	private boolean kill;
	
	// for animation
	private int xpos;
	private int ypos;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
		xpos = x * 100;
		ypos = y * 100;
		kill = false;
	}
	
	public Tile() {
		// doesn't take in coords, for testing
	}
	
	/*
	 * getter methods
	 */
	public int getx() {
		return x;
	}
	
	public int gety() {
		return y;
	}
	
	public int getdx() {
		return dx;
	}
	
	public int getdy() {
		return dy;
	}
	
	public boolean getKill() {
		return kill;
	}
	
	// abstract
	public abstract Type getType();
	
	/*
	 * setter methods
	 */	
	public void setdx(int dx) {
		this.dx = dx;
	}
	
	public void setdy (int dy) {
		this.dy = dy;
	}
	
	/*
	 * actions
	 */	
	
	// shows this box has been smushed with another
	public void kill() {
		kill = true;
	}
	
	// abstract
	public void update() {
		x = x + dx;
		y = y + dy;
		dx = 0;
		dy = 0;
	}
	
	// draw, returns boolean to signal if it is done drawing
	public boolean draw(Graphics g) {
		if (xpos == x * 100 + dx * 100 && ypos == y * 100 + dy * 100) {
			draw(xpos, ypos, g);
			return true;
		} else if (xpos < x * 100 + dx * 100) {
			xpos += 25;
			draw(xpos, ypos, g);
			return xpos == x * 100 + dx * 100;
		} else if (xpos > x * 100 + dx * 100) {
			xpos -= 25;
			draw(xpos, ypos, g);
			return xpos == x * 100 + dx * 100;
		} else if (ypos < y * 100 + dy * 100) {
			ypos += 25;
			draw(xpos, ypos, g);
			return ypos == y * 100 + dy * 100;
		} else {
			ypos -= 25;
			draw(xpos, ypos, g);
			return ypos == y * 100 + dy * 100;
		}
	}
	
	public abstract void draw(int x, int y, Graphics g);
}
