import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NumGridTest {
	private NumGrid grid;
	private NumGrid smallGrid;
	
	@Before
	public void setUp() {
		grid = new NumGrid();
		smallGrid = new NumGrid(2);
	}
	
	// generateBox() test
	@Test
	public void generateBox() {
		grid.generateBox();
		assertEquals(1, grid.numBox());
		grid.generateBox();
		assertEquals(2, grid.numBox());
		grid.generateBox();
		assertEquals(3, grid.numBox());
	}
	
	// setBox() test
	@Test
	public void setBox() {
		grid.setBox(4, 2, 3);
		assertTrue("box exists at (2, 3)", grid.existsBox(2, 3));
		grid.setBox(2, 0, 1);
		assertTrue("box exists at (0, 1)", grid.existsBox(0, 1));
	}
	
	// isFull() test -- generating 16 boxes makes full grid
	@Test
	public void isFull() {
		for (int i = 0; i < 16; i++) {
			grid.generateBox();
		}
		assertTrue("full grid", grid.isFull());
	}
	
	// comb() tests
	@Test
	public void combFourNumbers() {
		// box in a row: a, b, c, d
		// represent: 2, 2, 2, 2
		// correct result: empty, empty, 4, 4
		NumTile a = new NumTile(2);
		NumTile b = new NumTile(2);
		NumTile c = new NumTile(2);
		NumTile d = new NumTile(2);
		// box d doesn't move anywhere
		NumTile[] arr1 = {d};
		assertEquals(0, grid.comb(arr1));
		// box c is killed and combined with d
		NumTile[] arr2 = {c, d};
		assertEquals(1, grid.comb(arr2));
		assertTrue("c is killed", c.getKill());
		assertEquals(2, d.getdv());
		// box b moves 1 right
		NumTile[] arr3 = {b, c, d};
		assertEquals(1, grid.comb(arr3));
		// box a is killed and combined with b
		NumTile[] arr4 = {a, b, c, d};
		assertEquals(2, grid.comb(arr4));
		assertTrue("a is killed", a.getKill());
		assertEquals(2, b.getdv());
	}
	
	@Test
	public void combOneNumber() {
		// box in a row: a, null, null, null
		// represent: 2, empty, empty, empty
		// correct result: empty, empty, empty, 2
		NumTile a = new NumTile(2);
		// box a moves 3 right
		NumTile[] arr = {a, null, null, null};
		assertEquals(3, grid.comb(arr));
	}
	
	@Test
	public void combTwoNumbers() {
		// box in a row: a, null, b, null
		// represent: 2, empty, 2, empty
		// correct result: empty, empty, empty, 4
		NumTile a = new NumTile(2);
		NumTile b = new NumTile(2);
		// box b moves 1 right
		NumTile[] arr1 = {b, null};
		assertEquals(1, grid.comb(arr1));
		// box a is killed, combined with b
		NumTile[] arr2 = {a, null, b, null};
		assertEquals(3, grid.comb(arr2));
		assertTrue("a is killed", a.getKill());
		assertEquals(2, b.getdv());
	}
	
	@Test
	public void combThreeNumbers() {
		// box in a row: null, a, b, c
		// represent: empty, 2, 2, 2
		// correct result: empty, empty, 2, 4
		NumTile a = new NumTile(2);
		NumTile b = new NumTile(2);
		NumTile c = new NumTile(2);
		// box c doesn't move
		NumTile[] arr1 = {c};
		assertEquals(0, grid.comb(arr1));
		// box b is killed, combined with c
		NumTile[] arr2 = {b, c};
		assertEquals(1, grid.comb(arr2));
		assertTrue("b is killed", b.getKill());
		assertEquals(2, c.getdv());
		// box a is moved 1 right
		NumTile[] arr3 = {a, b, c};
		assertEquals(1, grid.comb(arr3));
		assertEquals(0, b.getdv());
		assertEquals(0, a.getdv());
	}
	
	@Test
	public void combTwoNumbersSpaced() {
		// box in a row: a, null, null, b
		// represent: 2, empty, empty, 2
		// correct result: empty, empty, empty, 4
		NumTile a = new NumTile(2);
		NumTile b = new NumTile(2);
		// box b doesn't move
		NumTile[] arr1 = {b};
		assertEquals(0, grid.comb(arr1));
		// box a is killed, combined with b
		NumTile[] arr2 = {a, null, null, b};
		assertEquals(3, grid.comb(arr2));
		assertTrue("a is killed", a.getKill());
		assertEquals(2, b.getdv());
	}
	
	// prep() tests
	@Test
	public void prepUpOneElement() {
		grid.setBox(2, 0, 3);
		grid.prep(Direction.UP);
		assertEquals(-3, grid.getBox(0, 3).getdy());
	}
	
	@Test
	public void prepUpTwoElements() {
		grid.setBox(2, 1, 1);
		grid.setBox(2, 2, 2);
		grid.prep(Direction.UP);
		assertEquals(-1, grid.getBox(1, 1).getdy());
		assertEquals(-2, grid.getBox(2, 2).getdy());
	}
	
	@Test
	public void prepUpThreeElements() {
		grid.setBox(2, 1, 1);
		grid.setBox(2, 2, 2);
		grid.setBox(2, 2, 3);
		grid.prep(Direction.UP);
		// box (1, 1) is moved up 1
		assertEquals(-1, grid.getBox(1, 1).getdy());
		// box (2, 2) is moved up 2 and combined with (2, 3)
		assertEquals(-2, grid.getBox(2, 2).getdy());
		assertEquals(2, grid.getBox(2, 2).getdv());
		// box (2, 3) is killed
		assertEquals(true, grid.getBox(2, 3).getKill());
	}
	
	@Test
	public void prepDownOneElement() {
		grid.setBox(2, 0, 1);
		grid.prep(Direction.DOWN);
		assertEquals(2, grid.getBox(0, 1).getdy());
	}
	
	@Test
	public void prepLeftOneElement() {
		grid.setBox(2, 2, 0);
		grid.prep(Direction.LEFT);
		assertEquals(-2, grid.getBox(2, 0).getdx());
	}
	
	@Test
	public void prepRightOneElement() {
		grid.setBox(2, 2, 0);
		grid.prep(Direction.RIGHT);
		assertEquals(1, grid.getBox(2, 0).getdx());
	}
	
	// gameOver() Test
	@Test
	public void gameOverTrue() {
		// start with a smaller 2x2 grid
		smallGrid.setBox(2, 0, 0);
		smallGrid.setBox(4, 0, 1);
		smallGrid.setBox(4, 1, 0);
		smallGrid.setBox(2, 1, 1);
		assertTrue("game over", smallGrid.gameOver());
	}
	
	@Test
	public void gameOverVerticalRepeats() {
		// start with a smaller 2x2 grid
		smallGrid.setBox(2, 0, 0);
		smallGrid.setBox(2, 0, 1);
		smallGrid.setBox(4, 1, 0);
		smallGrid.setBox(8, 1, 1);
		assertFalse("not game over", smallGrid.gameOver());
	}
	
	@Test
	public void gameOverHorizontalRepeats() {
		// start with a smaller 2x2 grid
		smallGrid.setBox(2, 0, 0);
		smallGrid.setBox(8, 0, 1);
		smallGrid.setBox(4, 1, 0);
		smallGrid.setBox(8, 1, 1);
		assertFalse("not game over", smallGrid.gameOver());
	}
	
	// step() tests
	@Test
	public void stepOneBox() {
		smallGrid.setBox(2, 1, 1);
		smallGrid.prep(Direction.LEFT);
		smallGrid.step();
		assertTrue("box is now at (0, 1)", smallGrid.existsBox(0, 1));
		NumTile curr1 = smallGrid.getBox(0, 1);
		assertEquals(0, curr1.getx());
		assertEquals(1, curr1.gety());
		assertEquals(0, curr1.getdx());
		assertEquals(0, curr1.getdy());
		smallGrid.prep(Direction.UP);
		smallGrid.step();
		assertTrue("box is now at (0, 0)", smallGrid.existsBox(0, 0));
		NumTile curr2 = smallGrid.getBox(0, 0);
		assertEquals(0, curr2.getx());
		assertEquals(0, curr2.gety());
		assertEquals(0, curr2.getdx());
		assertEquals(0, curr2.getdy());
		}
	
	@Test
	public void stepTwoBoxes() {
		smallGrid.setBox(2, 1, 1);
		smallGrid.setBox(2, 1, 0);
		smallGrid.prep(Direction.DOWN);
		smallGrid.step();
		assertTrue("boxes combined at (1, 1)", smallGrid.existsBox(1, 1));
		assertFalse("no box at (1, 0)", smallGrid.existsBox(1, 0));
		NumTile curr = smallGrid.getBox(1, 1);
		assertEquals(1, curr.getx());
		assertEquals(1, curr.gety());
		assertEquals(4, curr.getv());
		assertEquals(0, curr.getdx());
		assertEquals(0, curr.getdy());
		assertEquals(0, curr.getdv());
	}
}