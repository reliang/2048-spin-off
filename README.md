# 2048 Spin-Off

This spin-off of 2048 has two modes--original and mixed. The user has the option to play a version that is similar to the original game, or play a version that also includes black and white tiles alongside number tiles. Black tiles do not cancel for 5 turns, then disappears on its own. White tiles double any number it clashes into, and also has the ability to cancel black tiles. The goal of the game is to reach a 2048 tile. The scoring mimics the original 2048 game. I wrote the 2048 code without referencing that of the original game.

## Instructions

To run the game, run the file Game.java within the "src" folder. A separate window would pop up with the 2048 game.

Enter a name and choose a mode to play. The original mode is similar to the 2048 everyone is familiar with, and the mixed mode includes black and white tiles alongside number tiles.

Use arrows keys to slide the numbers. The game ends when the board is full, and there are no more possible moves. When the game ends, the final score is recorded, and the top 10 scores are kept in the high scores tab on the bottom. The scores are mainly stored in highScores.txt, provided in the "files" folder.


## Running the tests
Test files are given in the "test" folder. They employ the JUnit 4 library. Each of the three files test a different component of the game. IOTest.java also takes in the highScoresTest.txt file in the "files" folder and writes out to highScoresTestOutput.txt. The input highScoresTest.txt looks like this:

```
240, Catherine
200, Anne
50, Chris
50,Catherine
200, Anne
60, Spencer
210, Sharon
220,Sharon
100,Thy
300, Jake
10, Catherine
20, Chris
```
After putting the file into a new grid and adding the scores 240 and 210 for Catherine, the output file highScoresTestOutput.txt looks like this:
```
50,Chris
60,Spencer
100,Thy
200,Anne
210,Sharon
210,Catherine
220,Sharon
240,Catherine
300,Jake
350,Catherine
```

## Files
Below are a list of the files kept in the "src" folder, and a description of each.

  Game - contains the main function and compiles the JFrame. Places a JGrid 
  in the center of the frame. Also displays score.
  
  JGrid - a JPanel that creates a Grid object, listens to key events, and 
  passed commands to the grid accordingly. It also handles high scores by 
  reading from and writing to a file.
  
  Grid - An interface with subclasses NumGrid and MixedGrid. JGrid determines
  the type of grid to initialize based on user feedback.
  
  NumGrid - handles the logic of the original 2048 game, which only has number
  blocks. Some of its functions include generating new boxes, determining how 
  to move the boxes based on the swipe direction it is given, checking when the
  game is over, and computing the score.
  
  MixedGrid - similar to NumGrid, except it handles the logic of the tweaked 
  game, which contains white and black tiles.
  
  Tile - An abstract class that is extended by three subclasses--NumTile,
  BlackTile and WhiteTile. Stores x, y, dx and dy for each tile, and keeps 
  track of whether the tile has been killed through combining. Each of the
  subclasses handles their own draw methods.
  
  NumTile - Tiles used in normal 2048 games. Keeps track of its own value, v, 
  its change in value, dv, and a boolean that tells whether it has been 
  combined after the player makes a move.
  
  BlackTile - A tile that doesn't cancel for 5 turns, then disappears. Keeps
  track of the number of turns it has been alive.
  
  WhiteTile - A tile that doubles all number tiles and cancels black tiles. 
  Doesn't keep track of any states by itself, but its behavior is handled by
  the game logic in MixedGrid.
  
  Type - Enum class that denotes the type of each tile, includes types 
  NUMTILE, BLACKTILE and WHITETILE.
  
  Direction - Enum class that denotes the direction of each swipe, includes UP,
  DOWN, LEFT and RIGHT.

## Core Concepts
1. 2D Arrays -
     The 2048 grid is implemented with a 4-by-4 2D array. Each box in the grid
     can either be null or contain a Tile that represents a number, or a black
     or white block.
     
  2. Inheritance/Sub-typing -
     I used both interfaces and abstract classes to implement new types of
     blocks into 2048--black and white blocks. This was not originally in my
     game proposal, but I was inspired midway. The black blocks would not 
     cancel for 5 turns then disappear on their own, and the white blocks 
     can double any number block or cancel black blocks. To implement this,
     I created the abstract class, Tile, to keep track of the x and y locations
     of the blocks and to handle their change in location. This allows all the 
     blocks to move the same way. Then, each separate subclass (NumTile, 
     BlackTile, and WhiteTile) handles its own draw() function, since each one
     is drawn differently. NumTile also keeps track of its own value v and 
     change in value dv, and BlackTile keeps track of the number of turns it 
     has been alive so that it disappears after 5 turns. Afterawrds, to 
     implement the game logic, I created the interface Grid, and separated it 
     into two subclasses--NumGrid and MixedGrid. NumGrid handles the normal 
     2048 game, while MixedGrid handles the game logic with the added black and
     white blocks.

  3. I/O -
     The I/O is used to record high scores for the game. Each time a new game
     starts, the high scores are read from highScores.txt and the score and 
     corresponding name(s) are stored in a TreeMap that maps integers to string
     array lists. This allows the scores to be sorted as they are inserted. The
     array list also allows multiple users to be mapped to the same score, 
     while keeping the order that they were inserted in. This way, if both 
     people have the same high score, and one has to be removed from the list,
     the latter would be removed. After a game ends, the result is then written
     back into the same file. The format is "score,name".

  4. Testable Component -
     The game logic in NumGrid and Mixed Grid is broken down so each function
     can be easily tested. For example, to find the end-state of the grid after
     each movement, the comb() function in NumGrid determines how the boxes in 
     a row combine and move if the direction of swipe is towards the end of the 
     array. For comb() in NumGridTest, I created single arrays with boxes in 
     them, and tested edge cases such as having {empty, 2, 2, 2}, which should 
     become {empty, empty, 2, 4} after swiping right, since the pair to 
     the right combine rather then the pair on the left. Then the prep() 
     function goes through the whole grid and orients the rows or columns to 
     feed into comb(). I tested this in NumGridTest by adding a few boxes into 
     the grid at a time, then calling prep() with UP, DOWN, LEFT, or RIGHT and 
     checking the dx and dy of each box. The step() function then actually 
     changes the location of each box inside the 2D array. I tested this in 
     NumGrid by setting several boxes inside the grid, calling the prep in one
     of the four directions, then calling step and checking if they are now in 
     their new locations. After adding the black and white tiles and creating 
     MixedGrid, I mostly only tested the new comb function, since everything 
     else works the same. I also tested the I/O in IOTest.
