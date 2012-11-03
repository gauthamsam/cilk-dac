/*
 * 
 */
package experiment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import client.Visualizer;



/**
 * This class represents an RMI client that creates a Job and decomposes it, constructing a set of Task objects
 * The client retrieves results from the Space that are computed for each Task by the Compute Servers, composing them into a solution to the original problem.
 * The RMI client requests a reference to a named remote object of the Compute Space. The reference (the remote object's stub instance) is what the client will use to make remote method calls to the remote object.
 * The client also encompasses the functionality of visualizing the results of the different tasks that are executed on a remote machine.
 */
public class Client {

	
	/** The space. */
	private static Space space;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws MalformedURLException the malformed url exception
	 * @throws RemoteException the remote exception
	 * @throws NotBoundException the not bound exception
	 * @throws ClassNotFoundException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException, IllegalArgumentException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException{
		
		Class<?> cls = Class.forName("experiment.SpaceImpl");
	    Method meth = cls.getMethod("main", String[].class);
	    meth.invoke(null, (Object)null); // static method doesn't have an instance
	    Thread.sleep(5000);
		space = SpaceImpl.getInstance();
		
		doExperiment(Integer.parseInt(args[0]));
		
		stopExecution();
	}
	
	private static void doExperiment(int operation) throws RemoteException, MalformedURLException, NotBoundException, IllegalArgumentException{
		
		switch(operation){
			case 1:
				// mJob is an instance of MandelbrotSet Job.
				Job<int[][]> mJob = new MandelbrotSetJob(new double[] {-0.7510975859375, 0.1315680625}, 0.01611,
						1024, 512);		
				int[][] counts = (int[][]) runTask(mJob);
				Visualizer.visualizeMandelbrotSetTask(counts, 512, 1024);
				break;
			case 2:
				double[][] cities = { { 1, 1 }, { 8, 1 }, { 8, 8 }, { 1, 8 }, { 2, 2 },
						{ 7, 2 }, { 7, 7 }, { 2, 7 }, { 3, 3 }, { 6, 3 }, {6, 6}, {3, 6} };
				// tspJob is an instance of the EuclideanTsp Job.
				Job<int[]> tspJob = new EuclideanTspJob(cities);		
				int[] tour = (int[]) runTask(tspJob);
				Visualizer.visualizeEuclideanTspTask(tour, cities, 512);
				break;
			case 3:
				int n = 16;
				Job<Integer> fibJob = new FibonacciJob(n);
				Integer result = (Integer) runTask(fibJob);
				System.out.println("Fibnonacci of " + n + " = " + result);
				break;
			case 4:
				stopExecution();
				break;
			default:
				throw new IllegalArgumentException("Operation invalid!");
		}

	}
	
	/**
	 * Stop execution of the Space which would in turn stop all the registered Compute Servers and then stop itself.
	 */
	private static void stopExecution(){
		try{
			space.stop();
		}
		catch(RemoteException re){
			System.out.println("Space successfully stopped.");
		}
	}
	
	/**
	 * Runs the given Job by first decomposing it into smaller Tasks and then composes the results of each of the tasks.
	
	 * @param job the Job to be performed. It is either a MandelbrotSet job or EuclideanTSP job
	 * @return object representing the overall result of the job.
	 * @throws RemoteException the remote exception
	 * @throws MalformedURLException the malformed url exception
	 * @throws NotBoundException the not bound exception
	 */
	private static Object runTask(Job<?> job) throws RemoteException, MalformedURLException, NotBoundException
	{		
		/* print task class name;
		* run task 5 times
		* collect/print the execution times
		* compute the average time		
		*/
		System.out.println("Job: " + job.getClass().getName());
		long startTime = System.nanoTime();
		job.generateTasks(space);
		Object obj = job.collectResults(space);
		long endTime = System.nanoTime();
		System.out.println("Elapsed Time for the job: " + (endTime - startTime) + " ns");
		return obj;
	}
	
}
