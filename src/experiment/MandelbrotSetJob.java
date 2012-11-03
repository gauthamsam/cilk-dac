/*
 * @author gautham
 */
package experiment;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import tasks.MandelbrotSetTask;
import api.Result;

/**
 * This class helps to produce a visualization of the some part of the Mandelbrot set which is probably one of the most well known fractals, and probably one of the most widely implemented fractal in fractal plotting programs.
 */
public class MandelbrotSetJob implements Job<int[][]> {

	/** The left corner coordinates of the square in the complex plane. */
	private double[] leftCornerCoordinates;
	
	/** The edge length of the square in the complex plane. */
	private double edgeLength;
	
	/** The numSquares denotes the number of pixels (n x n squares) representing the square region in the complex plane. */
	private int numSquares;
	
	/** The iteration limit that defines when the representative point of a region is considered to be in the Mandelbrot set. */
	private int iterationLimit;
	
	private long startTime;
	
	/**
	 * Instantiates a new Mandelbrot set task.
	 *
	 * @param leftCornerCoordinates the left corner coordinates of the square in the complex plane
	 * @param edgeLength the edge length of the square in the complex plane
	 * @param numSquares the number denoting the number of pixels (n x n squares) inside the square region in the complex plane
	 * @param iterationLimit the iteration limit that denotes the number of iterations to do before deciding that the representative point of a region is considered to be in the Mandelbrot set
	 */
	public MandelbrotSetJob(double[] leftCornerCoordinates, double edgeLength, int numSquares, int iterationLimit){
		this.leftCornerCoordinates = leftCornerCoordinates;
		this.edgeLength = edgeLength;
		this.numSquares = numSquares;
		this.iterationLimit = iterationLimit;		
	}
	
	/**
	 * Generates multiple tasks from this job. The client decomposes the problem (job), constructing a set of Task objects
	 * The MandelbrotSet job is decomposed into 'n' number of tasks, with each task taking care of a fixed number of rows as defined by NUM_ROWS_PER_TASK
	 */
	@Override
	public void generateTasks(Space space) {
		System.out.println("Generate Tasks");
		MandelbrotSetTask task = new MandelbrotSetTask(this.leftCornerCoordinates, this.edgeLength, this.numSquares, this.iterationLimit, 0, this.numSquares - 1);
		this.startTime = System.nanoTime();
		try{
			space.put(task);
		}
		catch(RemoteException re){
			re.printStackTrace();
		}			
			
	}

	/**
	 * Collects results from the Space, composing them into a solution to the original problem.
	 * Each result in the MandelbrotSet job is a one-dimensional array that contains the 'k' values of multiple rows (which correspond to the number of rows that each task works upon)
	 */
	@Override
	public int[][] collectResults(Space space) {
		int[][] count = new int[this.numSquares][this.numSquares];
		
		try {
			Result<Map<Integer, int[]>> result = space.take();
			long elapsedTime = System.nanoTime() - this.startTime;
			System.out.println("Elapsed Time for the task: " + elapsedTime + " ns");
			for(Entry<Integer, int[]> entry : result.getTaskReturnValue().entrySet()){
				int i = entry.getKey();
				int[] values = entry.getValue();
				for(int j = 0; j < values.length; j++){
					count[i][this.numSquares - j - 1] = values[j];
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
		
	}
	
}
