import java.awt.Color;
import java.awt.Graphics;


public class WhiteTile extends Tile {
	/*
	 * this tile doubles num tiles and cancels black tiles
	 * 
	 * - doesn't do anything to another white tile
	 */
	
	public WhiteTile(int x, int y) {
		super(x, y);
	}
	
	public WhiteTile() {
		super();
	}
	
	/*
	 * getter method
	 */
	@Override
	public Type getType() {
		return Type.WHITETILE;
	}

	/*
	 * actions
	 */
	@Override
	public void draw(int x, int y, Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRoundRect(x, y, 100, 100, 20, 20);
	}
}
