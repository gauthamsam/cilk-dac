/*
 * @author gautham
 */
package jobs;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;

import tasks.EuclideanTspTask;
import api.Result;
import api.Space;

/**
 * This class represents the entire work involved in solving a Traveling Salesman Problem (TSP), where the cities are points in the 2D Euclidean plane. * 
 */
public class EuclideanTspJob implements Job<int[]>{

	/** The cities in 2D Euclidean plane that are part of the TSP. */
	private double[][] cities;
	
	/**
	 * Instantiates a new Euclidean TSP task.
	 *
	 * @param cities the cities in 2D Euclidean plane that are part of the TSP; it codes the x and y coordinates of city[i]: cities[i][0] is the x-coordinate of city[i] and cities[i][1] is the y-coordinate of city[i]
	 */
	public EuclideanTspJob(double[][] cities){
		this.cities = cities;		
	}	
	
	
	/** Denotes the job start time. It used to record the time taken for execution of the task.*/
	private long startTime;
	
	
	/* (non-Javadoc)
	 * @see jobs.Job#generateTasks(api.Space)
	 */
	@Override
	public void generateTasks(Space space) {
		// prefix represents the path taken (permutation) from the root task up to the current task 
		int[] prefix = {0};
		// permutation represents the array that needs to be permuted for computing the minimal tour
		int[] permutation = new int[cities.length - 1];
		for(int i = 0; i < permutation.length; i++){
			permutation[i] = i + 1;
		}
		// The first task has level = 0
		EuclideanTspTask task = new EuclideanTspTask(cities, prefix, permutation, 0);
		this.startTime = System.nanoTime();
		try{
			space.put(task);
		}
		catch(RemoteException e){
			e.printStackTrace();
		}
	}

	/**
	 * Collects the computed result that represents the composed result of the execution of all the tasks
	 * The result in the EuclideanTSP job is a map of the minimal tour among the permutations computed by the tasks and the cost involved for that tour.
	 * A map is used to store the minimum distance computed for each task so that it need not be re-computed each time when composing tasks.  
	 * @param space the space
	 * @return int[]
	 */
	@Override
	public int[] collectResults(Space space) {
		System.out.println("Collect Results");
		int[] minTour = null;		
		Map<int[], Double> minTourMap = null;
		try {
			Result<Map<int[], Double>> result = space.take();
			long elapsedTime = System.nanoTime() - this.startTime;
			System.out.println("Elapsed Time for the task: " + elapsedTime + " ns");
			minTourMap = result.getTaskReturnValue();
			minTour = minTourMap.keySet().iterator().next();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		System.out.println("Min Tour: " + Arrays.toString(minTour));
		return minTour;
	}
	
}
