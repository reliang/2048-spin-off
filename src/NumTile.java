import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


public class NumTile extends Tile {
	
	private int v;
	private int dv;
	private boolean combine;
	
	public NumTile(int v, int x, int y) {
		super(x, y);
		this.v = v;
		combine = false;
	}
	
	// constructs a box for testing
	public NumTile(int v) {
		super();
		this.v = v;
	}
	
	/*
	 * getter methods
	 */
	public int getv() {
		return v;
	}
	
	public int getdv() {
		return dv;
	}
	
	@Override
	public Type getType() {
		return Type.NUMTILE;
	}
	
	// keeps track of if the number has been combined
	public boolean getCombine() {
		return combine;
	}
	
	/*
	 * actions
	 */
	// always combines w/ same number so dv = v
	public void combine() {
		this.dv = v;
		combine = true;
	}
	
	// update v, x, y, dx, dy, dv after step()
	@Override
	public void update() {
		super.update();
		v = v + dv;
		dv = 0;
		combine = false;
	}

	@Override
	public void draw(int x, int y, Graphics g) {
		// inputs: x, y, width, height, arcWidth, arcHeight
		Color color = new Color(255, Math.max(0,
				(int) (255 - Math.sqrt(v) * 5)), 0);
		g.setColor(color);
		g.fillRoundRect(x, y, 100, 100, 20, 20);
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));

		String value = "" + v;
		// String value = "2048";
		int width = 10;
		int padSize = width - 2 * value.length();
		int padStart = value.length() + padSize / 2;
		value = String.format("%" + padStart + "s", value);
		value = String.format("%-" + width + "s", value);

		g.drawString("" + value, x, y + 65);
	}
}
