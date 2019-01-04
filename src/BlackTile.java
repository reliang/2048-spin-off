import java.awt.Color;
import java.awt.Graphics;


public class BlackTile extends Tile {
	/*
	 * this tile doesn't cancel out for five turns
	 * unless it is combined with a white tile
	 * on the fifth turn, it disappears by itself
	 * 
	 * - must check in Grid if turns is at 5
	 */
	
	private int turns;
	
	public BlackTile(int x, int y) {
		super(x, y);
		turns = 0;
	}
	
	public BlackTile() {
		super();
		turns = 0;
	}
	
	/*
	 * getter methods
	 */
	public int getTurns() {
		return turns;
	}
	
	@Override
	public Type getType() {
		return Type.BLACKTILE;
	}
	
	/*
	 * actions
	 */
	
	@Override
	public void update() {
		super.update();
		turns++;
	}

	@Override
	public void draw(int x, int y, Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRoundRect(x, y, 100, 100, 20, 20);
	}
}
