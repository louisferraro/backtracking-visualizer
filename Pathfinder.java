// louis ferraro - lo582399
// COP 3503 - Summer 19
import java.io.*;
import java.util.*;

public class Pathfinder
{
	// Used to toggle "animated" output on and off (for debugging purposes).
	private static boolean animationEnabled = false;

	// "Animation" rate (frames per second).
	private static double frameRate = 4.0;

	// toggle to print print moves
	private static boolean printMode = false;

	// toggle to print visited array
	private static boolean printVisited = false;

	// Setters
	public static void enableAnimation() { Pathfinder.animationEnabled = true; }
	public static void disableAnimation() { Pathfinder.animationEnabled = false; }
	public static void setFrameRate(double fps) { Pathfinder.frameRate = frameRate; }

	// Maze constants.
	private static final char WALL       = '#';
	private static final char PERSON     = '@';
	private static final char EXIT       = 'e';
	private static final char BREADCRUMB = '.';  // visited
	private static final char SPACE      = ' ';  // unvisited
	private static final char left      = 'l';
	private static final char right      = 'r';
	private static final char down      = 'd';
	private static final char up      = 'u';
	public static ArrayList<Character> move = new ArrayList<Character>();
	public static HashSet<String> set = new HashSet<String>();

	// Takes a 2D char maze and returns true if it can find a path from the
	// starting position to the exit. Assumes the maze is well-formed according
	// to the restrictions above.
	public static HashSet<String> findPaths(char [][] maze)
	{
		int height = maze.length;
		int width = maze[0].length;

		// The visited array keeps track of visited positions. It also keeps
		// track of the exit, since the exit will be overwritten when the '@'
		// symbol covers it up in the maze[][] variable. Each cell contains one
		// of three values:
		//
		//   '.' -- visited
		//   ' ' -- unvisited
		//   'e' -- exit
		char [][] visited = new char[height][width];
		for (int i = 0; i < height; i++)
			Arrays.fill(visited[i], SPACE);

		// Find starting position (location of the '@' character).
		int startRow = -1;
		int startCol = -1;

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if (maze[i][j] == PERSON)
				{
					startRow = i;
					startCol = j;
				}
			}
		}

		// Let's goooooooo!!
		findPaths(maze, visited, startRow, startCol, height, width);
		return set;
	}

	private static boolean findPaths(char [][] maze, char [][] visited,
	                                 int currentRow, int currentCol,
	                                 int height, int width)
	{
		// This conditional block prints the maze when a new move is made.
		if (Pathfinder.animationEnabled)
			printAndWait(maze, visited, height, width, "Searching...", Pathfinder.frameRate);

		// Hooray!
		if (visited[currentRow][currentCol] == 'e')
		{
			// we've found a valid path, so store it in hashset and go back to the position
			// where we could take another legal move and see if we can find another valid path
			String validPath = turnIntoString(move);
			// if that path is already in the set, return false
			if (set.add(validPath) == false)
				return false;
			return true;
		}

		// left, right, up, down......[0] [1]
		int [][] moves = new int[][] {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
		for (int i = 0; i < moves.length; i++)
		{
			int newRow = currentRow + moves[i][0];
			int newCol = currentCol + moves[i][1];

			// Check move is in bounds, not a wall, and not marked as visited.
			if (!isLegalMove(maze, visited, newRow, newCol, height, width))
				continue;

			// adds moves into an ArrayList to be later formatted into String
			storeMoves(currentRow, currentCol, newRow, newCol);

			if (printMode)
				printMoves();

			// Change state. Before moving the person forward in the maze, we
			// need to check whether we're overwriting the exit. If so, save the
			// exit in the visited[][] array so we can actually detect that
			// we've gotten there.
			if (maze[newRow][newCol] == EXIT)
				visited[newRow][newCol] = EXIT;

			maze[currentRow][currentCol] = BREADCRUMB;
			visited[currentRow][currentCol] = BREADCRUMB;
			maze[newRow][newCol] = PERSON;

			// Perform recursive descent, if I found a path to exit keep going with
			// the exit still intact so I can find it again on another path
			if (findPaths(maze, visited, newRow, newCol, height, width))
				visited[newRow][newCol] = EXIT;

			// Undo state change, need to update visited to spaces so when I backtrack I can
			// try to find new paths
			visited[currentRow][currentCol] = SPACE;
			maze[newRow][newCol] = SPACE; // backtracked positions
			maze[currentRow][currentCol] = PERSON;
			removeMoves();

			// This conditional block prints the maze when a move gets undone
			// (which is effectively another kind of move).
			if (Pathfinder.animationEnabled)
			{
				if (printMode)
					printMoves();
				printAndWait(maze, visited, height, width, "Backtracking...", frameRate);
			}
		}

		return false;
	}

	// Returns true if moving to row and col is legal (i.e., we have not visited
	// that position before, and it's not a wall).
	private static boolean isLegalMove(char [][] maze, char [][] visited,
	                                   int row, int col, int height, int width)
	{
		// we dont go out of bounds in the maze that isn't blocked off with walls
		if (row < 0 || col < 0 || row >= height || col >= width)
			return false;
		if (maze[row][col] == WALL || visited[row][col] == BREADCRUMB)
			return false;

		return true;
	}

	// This effectively pauses the program for waitTimeInSeconds seconds.
	private static void wait(double waitTimeInSeconds)
	{
		long startTime = System.nanoTime();
		long endTime = startTime + (long)(waitTimeInSeconds * 1e9);

		while (System.nanoTime() < endTime)
			;
	}

	// Prints maze and waits. frameRate is given in frames per second.
	private static void printAndWait(char [][] maze, char [][] visited, int height, int width,
	                                 String header, double frameRate)
	{
		if (header != null && !header.equals(""))
			System.out.println(header);

		if (height < 1 || width < 1)
			return;

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				System.out.print(maze[i][j]);
			}

			System.out.println();
		}
		System.out.println();
		wait(1.0 / frameRate);

		if (printVisited)
		{
			for (int m = 0; m < height; m++)
			{
				for (int n = 0; n < width; n++)
				{
					System.out.print(visited[m][n]);
				}

				System.out.println();
			}
			System.out.println();
			wait(1.0 / frameRate);
		}
	}

	// Read maze from file. This function dangerously assumes the input file
	// exists and is well formatted according to the specification above.
	private static char [][] readMaze(String filename) throws IOException
	{
		Scanner in = new Scanner(new File(filename));

		int height = in.nextInt();
		int width = in.nextInt();

		char [][] maze = new char[height][];

		// After reading the integers, there's still a new line character we
		// need to do away with before we can continue.
		in.nextLine();

		for (int i = 0; i < height; i++)
		{
			// Explode out each line from the input file into a char array.
			maze[i] = in.nextLine().toCharArray();
		}

		return maze;
	}

	public static String turnIntoString(ArrayList<Character> setOfMoves)
	{
		StringBuilder builder = new StringBuilder();
		for (char i : setOfMoves)
			builder.append(i);

		// remove the space at the end
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	public static void storeMoves(int currentRow, int currentCol, int newRow, int newCol)
	{
		if (newCol > currentCol) // moved right
		{
				move.add(right);
				move.add(' ');
		}
		if (newCol < currentCol) // moved left
		{
				move.add(left);
				move.add(' ');
		}
		if (newRow < currentRow) // moved up
		{
				move.add(up);
				move.add(' ');
		}
		if (newRow > currentRow) // moved down
		{
				move.add(down);
				move.add(' ');
		}
	}

	public static void removeMoves()
	{
		move.remove(move.size() - 1);
		move.remove(move.size() - 1); // account for spaces
	}

	public static void printMoves()
	{
		for (char i : move)
		{
			System.out.print(i);
		}
		System.out.println();
	}

	public static double difficultyRating()
	{
		return 3.7;
	}

	public static double hoursSpent()
	{
		return 12.0;
	}

	public static void main(String [] args) throws IOException
	{
		// Load maze and turn on "animation."
		char [][] maze = readMaze("input_files/maze1.txt");
		Pathfinder.enableAnimation();

		// Go!!
		set = Pathfinder.findPaths(maze);
	}
}
