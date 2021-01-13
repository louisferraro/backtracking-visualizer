// Sean Szumlanski
// COP 3503, Summer 2019

// ===========================
// Pathfinder: TestCase02.java
// ===========================
// A brief test case to help ensure you've implemented the hoursSpent() method correctly.


import java.io.*;
import java.util.*;

public class TestCase02
{
	public static void main(String [] args)
	{
		double hours = Pathfinder.hoursSpent();

		if (hours <= 0.0)
			System.out.println("fail whale :(");
		else
			System.out.println("Hooray!");
	}
}
