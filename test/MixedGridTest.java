import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MixedGridTest {
	private MixedGrid grid;
	private MixedGrid smallGrid;
	
	@Before
	public void setUp() {
		grid = new MixedGrid();
		smallGrid = new MixedGrid(2);
	}
	
	// comb() tests
	@Test
	public void combWhiteFirst() {
		// box in a row: null, a, null, b
		// represent: empty, white, empty, 2
		// correct result: empty, empty, empty, 4
		WhiteTile a = new WhiteTile();
		NumTile b = new NumTile(2);
		// box b doesn't move anywhere
		Tile[] arr1 = {b};
		assertEquals(0, grid.comb(arr1));
		// box a is killed and combined with b
		Tile[] arr2 = {a, null, b};
		assertEquals(2, grid.comb(arr2));
		assertTrue("box a is killed", a.getKill());
		assertEquals(2, b.getdv());
	}
	
	@Test
	public void combWhiteSecond() {
		// box in a row: a, null, null, b
		// represent: 2, empty, empty, white
		// correct result: empty, empty, empty, 4
		NumTile a = new NumTile(2);
		WhiteTile b = new WhiteTile();
		// box b doesn't move anywhere
		Tile[] arr1 = {b};
		assertEquals(0, grid.comb(arr1));
		// box a combines with box b
		Tile[] arr2 = {a, null, null, b};
		assertEquals(3, grid.comb(arr2));
		assertTrue("box b is killed", b.getKill());
		assertEquals(2, a.getdv());
	}
	
	@Test
	public void combCombineOnce() {
		// box in a row: null, a, b, c
		// represent: empty, 4, 2, white
		// correct result: empty, empty, 4, 4
		NumTile a = new NumTile(4);
		NumTile b = new NumTile(2);
		WhiteTile c = new WhiteTile();
		// box c doesn't move anywhere
		Tile[] arr1 = {c};
		assertEquals(0, grid.comb(arr1));
		// box b combines with box c
		Tile[] arr2 = {b, c};
		assertEquals(1, grid.comb(arr2));
		assertTrue("box c is killed", c.getKill());
		assertEquals(2, b.getdv());
		// box a moves 1 right
		Tile[] arr3 = {a, b, c};
		assertEquals(1, grid.comb(arr3));
		assertEquals(4, a.getv());
	}
	
	@Test
	public void combTwoWhite() {
		// box in a row: null, a, b, null
		// represent: empty, white, white, empty
		// correct result: empty, empty, white, white
		WhiteTile a = new WhiteTile();
		WhiteTile b = new WhiteTile();
		// box b moves 1 right
		Tile[] arr1 = {b, null};
		assertEquals(1, grid.comb(arr1));
		// box a moves 1 right
		Tile[] arr2 = {a, b, null};
		assertEquals(1, grid.comb(arr2));
		assertFalse("a not killed", a.getKill());
		assertFalse("b not killed", b.getKill());
	}
	
	@Test
	public void combBlackFirst() {
		// box in a row: null, a, b, null
		// represent: empty, black, 2, null
		// correct result: empty, empty, black, 2
		BlackTile a = new BlackTile();
		NumTile b = new NumTile(2);
		// box b moves 1 right
		Tile[] arr1 = {b, null};
		assertEquals(1, grid.comb(arr1));
		// box a moves 1 right
		Tile[] arr2 = {a, b, null};
		assertEquals(1, grid.comb(arr2));
		assertFalse("a not killed", a.getKill());
		assertFalse("b not killed", b.getKill());
	}
	
	@Test
	public void combBlackSecond() {
		// box in a row: a, null, b, null
		// represent: 2, empty, black, empty
		// correct result: empty, empty, 2, black
		NumTile a = new NumTile(2);
		BlackTile b = new BlackTile();
		// box b moves 1 right
		Tile[] arr1 = {b, null};
		assertEquals(1, grid.comb(arr1));
		// box a moves 2 right
		Tile[] arr2 = {a, null, b, null};
		assertEquals(2, grid.comb(arr2));
		assertEquals(0, a.getdv());
	}
	
	@Test
	public void combTwoBlack() {
		// box in a row: a, null, b, null
		// represent: black, empty, black, empty
		// correct result: empty, empty, black, black
		BlackTile a = new BlackTile();
		BlackTile b = new BlackTile();
		// box b moves 1 right
		Tile[] arr1 = {b, null};
		assertEquals(1, grid.comb(arr1));
		// box a moves 2 right
		Tile[] arr2 = {a, null, b, null};
		assertEquals(2, grid.comb(arr2));
		assertFalse("a not killed", a.getKill());
		assertFalse("b not killed", b.getKill());
	}
	
	@Test
	public void combBlackAndWhite() {
		// box in a row: a, null, b, null
		// represent: black, empty, white, empty
		// correct result: empty, empty, empty, empty
		BlackTile a = new BlackTile();
		WhiteTile b = new WhiteTile();
		// box b moves 1 right
		Tile[] arr1 = {b, null};
		assertEquals(1, grid.comb(arr1));
		// box a cancels with b
		Tile[] arr2 = {a, null, b, null};
		assertEquals(3, grid.comb(arr2));
		assertTrue("a is killed", a.getKill());
		assertTrue("b is killed", b.getKill());
	}
	
	@Test
	public void combWhiteAndBlack() {
		// box in a row: a, null, b, null
		// represent: white, empty, black, empty
		// correct result: empty, empty, empty, empty
		WhiteTile a = new WhiteTile();
		BlackTile b = new BlackTile();
		// box b moves 1 right
		Tile[] arr1 = {b, null};
		assertEquals(1, grid.comb(arr1));
		// box a cancels with b
		Tile[] arr2 = {a, null, b, null};
		assertEquals(3, grid.comb(arr2));
		assertTrue("a is killed", a.getKill());
		assertTrue("b is killed", b.getKill());
	}
}
