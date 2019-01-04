import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JLabel;

import org.junit.Before;
import org.junit.Test;


public class IOTest {
	private JGrid grid;
	
	@Before
	public void setUp() throws FileNotFoundException, IOException {
		grid = new JGrid(new JLabel());
		grid.setName("Catherine");
		grid.readScores("files/highScoresTest.txt");
	}
	
	// readScores() tests
	@Test
	public void readScoresBasics() {
		Map<Integer, ArrayList<String>> scores = grid.getScores();
		ArrayList<String> names240 = scores.get(240);
		assertTrue("Catherine has a 240", names240.contains("Catherine"));
		ArrayList<String> names50 = scores.get(50);
		assertEquals(2, names50.size());
		assertTrue("Chris has a 50", names50.contains("Chris"));
		assertTrue("Catherine has a 50", names50.contains("Catherine"));
		assertTrue("Chris has a 20", scores.containsKey(20));
		assertEquals(10, grid.getScoreCount());
	}
	
	@Test
	public void readScoresNoRepeats() {
		Map<Integer, ArrayList<String>> scores = grid.getScores();
		ArrayList<String> names200 = scores.get(200);
		assertTrue("Anne has a 200", names200.contains("Anne"));
		assertEquals(1, names200.size());
	}
	
	@Test
	public void readScoresTrimmed() {
		Map<Integer, ArrayList<String>> scores = grid.getScores();
		assertFalse("Score of 10 is dropped", scores.containsKey(10));
	}
	
	// addScore() tests
	@Test
	public void addScoreBasic() {
		grid.addScore(350);
		Map<Integer, ArrayList<String>> scores = grid.getScores();
		assertFalse("Score of 20 is dropped", scores.containsKey(20));
		assertTrue("Score of 350 is added", scores.containsKey(350));
		assertEquals(10, grid.getScoreCount());
	}
	
	@Test
	public void addScoreRepeated() {
		grid.addScore(240);
		Map<Integer, ArrayList<String>> scores = grid.getScores();
		ArrayList<String> names = scores.get(240);
		assertEquals(1, names.size());
		assertEquals(10, grid.getScoreCount());
	}
	
	@Test
	public void addScoreMultipleLeastScores() {
		grid.addScore(350);
		grid.addScore(210);
		Map<Integer, ArrayList<String>> scores = grid.getScores();
		assertEquals(10, grid.getScoreCount());
		assertFalse("Score 20 doesn't exist", scores.containsKey(20));
		assertTrue("Score 50 still exists", scores.containsKey(50));
		ArrayList<String> names = scores.get(50);
		assertEquals(1, names.size());
		assertTrue("Catherine is deleted", names.contains("Chris"));
	}
	
	// writeScores() test
	@Test
	public void writeScores() throws IOException {
		grid.addScore(350);
		grid.addScore(210);
		grid.writeScores("files/highScoresTestOutput.txt");
	}
}
