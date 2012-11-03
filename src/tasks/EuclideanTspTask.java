/*
 * @author gautham
 */
package tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import utils.Constants;

import api.Result;
import api.Task;

/**
 * This class represents a unit of task involved in solving a Traveling Salesman Problem (TSP), where the cities are points in the 2D Euclidean plane.
 */
public final class EuclideanTspTask extends Task<Map<int[], Double>>{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The cities in 2D Euclidean plane that are part of the TSP. */
	private double[][] cities;
	
	/** The taskPermutation denotes the permutation of cities given to this task. */
	private int[] taskPermutation;
	
	
	/** prefix represents the path taken (permutation) from the root task up to the current task */
	private int[] prefix;
	
	/** The level of this task. */
	private int level;
	
	
	/**
	 * Instantiates a new euclidean tsp task.
	 *
	 * @param taskType the task type
	 */
	public EuclideanTspTask(int taskType){
		super(UUID.randomUUID(), taskType);
	}
	
	/**
	 * Instantiates a new Euclidean TSP task.
	 *
	 * @param cities the cities
	 * @param prefix the prefix
	 * @param permutation the original permutation of the cities given to this Task
	 * @param level the level
	 */
	public EuclideanTspTask(double[][] cities, int[] prefix, int[] permutation, int level){
		super(UUID.randomUUID(), Constants.CHILD_TASK);
		this.cities = cities;
		this.prefix = prefix;
		this.taskPermutation = permutation;			
		this.level = level;
	}
	
	/**
	 * Executes the Euclidean TSP Task.
	 * The method of finding the minimal distance tour is efficient; the program will fix one point as the starting point and iterate over all the remaining permutations of the cities, and returns a permutation of least cost. 
	 * @return a map of the minimal tour among the permutations computed by this task and the cost involved for that tour. A map is used to store the minimum distance computed for each task so that it need not be re-computed each time when composing tasks.  	
	 */
	@Override
	public Result<Map<int[], Double>> execute() {
		Result<Map<int[], Double>> result = new Result<Map<int[], Double>>();		
		
		if(this.taskType == Constants.CHILD_TASK){
			// Variable to hold the minimum distance between all the cities.
			double minDistance = Double.MAX_VALUE;
			// set n = 2 since the first permutation is not needed.
			int n = 2;
			// a map of the minimal tour among the permutations computed by this task and the cost involved for that tour.
			Map<int[], Double> minTourMap = new HashMap<int[], Double>(1);
			int[] tour = new int[cities.length];
			// currentDistance holds the distance traveled for the given permutation of the cities
			double currentDistance = 0;
			double initDistance = 0;
			for(int i = 0; i < this.prefix.length - 1; i++){
				initDistance += calculateDistance(this.cities[this.prefix[i]], this.cities[this.prefix[i + 1]]);
			}			
			
			while(true){			
				this.taskPermutation = getPermutation(this.taskPermutation, n++);
				if(this.taskPermutation == null){ // All the permutations have been computed. No more left.
					break;
				}
				currentDistance = initDistance;
				// distance between the last city in prefix and the first city in taskPermutation
				currentDistance += calculateDistance(this.cities[this.prefix[this.prefix.length - 1]], this.cities[this.taskPermutation[0]]);
				for(int j = 0; j < this.taskPermutation.length - 1; j++){
					currentDistance += calculateDistance(this.cities[this.taskPermutation[j]], this.cities[this.taskPermutation[j + 1]]);				
				}
				// distance between the last city in taskPermutation and the first city in prefix
				currentDistance += calculateDistance(this.cities[this.taskPermutation[this.taskPermutation.length - 1]], this.cities[this.prefix[0]]);
				
				if(minDistance > currentDistance){
					minDistance = currentDistance;
					// copy the current task's permutation to an array starting from index = prefix.length
					copyArray(this.taskPermutation, tour, this.prefix.length);
				}
				
			}		
			// copy the current task's prefix to the array starting from index 0.
			copyArray(this.prefix, tour, 0);
			minTourMap.put(tour, minDistance);
			result.setTaskReturnValue(minTourMap);
			
		}
		else{
			// Successor task
			int[] minTour = null;
			double minDistance = Double.MAX_VALUE;
			Map<int[], Double> map = new HashMap<int[], Double>();
			// Get the map representing the minimal tour from all the sub-tasks corresponding to this successor task  
			for(Task<Map<int[], Double>> task : this.getInputList()){
				Map<int[], Double> minTourMap = task.getResult().getTaskReturnValue();				
				for(Entry<int[], Double> entry : minTourMap.entrySet()){
					double distance = entry.getValue();
					if(distance < minDistance){
						minDistance = distance;
						minTour = entry.getKey();						
					}
				}				
			}
			map.put(minTour, minDistance);
			result.setTaskReturnValue(map);
		}
		this.setResult(result);
		return result;
	}
	
	/**
	 * Copy array.
	 *
	 * @param source the source
	 * @param dest the dest
	 * @param startIndexOfDest the start index of dest
	 */
	private void copyArray(int[] source, int[] dest, int startIndexOfDest){
		for(int i = 0; i < source.length; i++){
			dest[startIndexOfDest++] = source[i];
		}
	}
	/**
	 * Generate the nth permutations in lexicographic order.
	 *
	 * @param permutation the (n-1)th permutation array; when n = 1, it is just 0 to permutation length - 1
	 * @param n the nth permutation to be computed
	 * @return the nth permutation
	 */
	private int[] getPermutation(int[] permutation, int n) {
		if(n == 1){ // the first permutation is just 0 to permutation length - 1 (0 to no. of cities - 1)
			for (int i = 0; i < permutation.length; i++)
		    	permutation[i] = i;
			return permutation;
		}
		int k, l;
        // Find the largest index k such that a[k] < a[k + 1]. If no such index exists, the permutation is the last permutation.
        for (k = permutation.length - 2; k >=0 && permutation[k] >= permutation[k+1]; k--);
        if(k == -1){
        	return null;
        }
        // Find the largest index l such that a[k] < a[l]. Since k + 1 is such an index, l is well defined and satisfies k < l.
        for (l = permutation.length - 1; permutation[k] >= permutation[l]; l--);
        // Swap a[k] with a[l].
        swap(permutation, k, l);
        // Reverse the sequence from a[k + 1] up to and including the final element a[n].
        for (int j = 1; k + j < permutation.length - j; j++){
        	swap(permutation, k + j, permutation.length - j);
        }
        return permutation;
	}
	
	/**
	 * Swap the elements of the array in place.
	 *
	 * @param arr the array
	 * @param i the ith position
	 * @param j the jth position
	 */
	private void swap(int[] arr, int i, int j){
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}
	
	
	/**
	 * Calculate the Euclidean distance.
	 *
	 * @param pointA the starting point
	 * @param pointB the ending point
	 * @return distance the distance between the points
	 */
	private double calculateDistance(double[] pointA, double[] pointB){		
		double temp1 = Math.pow((pointA[0] - pointB[0]), 2);
		double temp2 = Math.pow((pointA[1] - pointB[1]), 2);
		double distance = Math.sqrt(temp1 + temp2);
		return distance;
	}

	/**
	 * Gets the distance.
	 *
	 * @param permutation the permutation
	 * @return the distance
	 */
	private double getDistance(int[] permutation){
		double currentDistance = 0;
		
		currentDistance = calculateDistance(cities[this.taskPermutation[0]], cities[permutation[0]]);
		for(int j = 0; j < permutation.length - 1; j++){
			currentDistance += calculateDistance(cities[permutation[j]], cities[permutation[j + 1]]);				
		}
		currentDistance += calculateDistance(cities[permutation[permutation.length - 1]], cities[this.taskPermutation[0]]);
		return currentDistance;
	}
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		double[][] cities = { { 1, 1 }, { 8, 1 }, { 8, 8 }, { 1, 8 }, { 2, 2 },
				{ 7, 2 }, { 7, 7 }, { 2, 7 }, { 3, 3 }, { 6, 3 }, {6, 6}, {3, 6} };
		int[] cityOrder = {10, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11};
		EuclideanTspTask task = new EuclideanTspTask(cities, new int[] {0}, cityOrder, 0);
		int[] arr = {6, 2, 1, 5, 9, 0, 4, 8, 11, 7, 3};
		System.out.println(task.getDistance(arr));
	}

	/* (non-Javadoc)
	 * @see api.Task#isBaseCondition()
	 */
	@Override
	public boolean isBaseCondition() {
		return this.level == Constants.EUCLIDEANTSP_BASE_LEVEL;
	}

	/**
	 * Each TSP task is split into (no. cities.length - level - 1) tasks
	 * @see api.Task#splitTask()
	 */
	@Override
	public List<Task<Map<int[], Double>>> splitTask() {
		int numTasks = this.cities.length - 1 - this.level;		
		int[] temp = Arrays.copyOf(this.taskPermutation, this.taskPermutation.length);
		
		List<Task<Map<int[], Double>>> tasks = new ArrayList<Task<Map<int[],Double>>>();
		for(int i = 0, j = 0; i < numTasks; i++, j = 0){
			int[] prefix = new int[this.prefix.length + 1];
			for(; j < this.prefix.length; j++){
				prefix[j] = this.prefix[j];
			}
			prefix[j] = this.taskPermutation[i];
			
			if(i != 0){ // Swap the first element and the next smallest element
				swap(temp, 0, i);
			}
			
			int[] taskPermutation = Arrays.copyOfRange(temp, 1, temp.length);
			
			Task<Map<int[], Double>> task = new EuclideanTspTask(this.cities, prefix, taskPermutation, this.level + 1);
			task.setArgNo(i);
			tasks.add(task);
		}
		
		return tasks;
	}
	
	
	/* (non-Javadoc)
	 * @see api.Task#createSuccessorTask()
	 */
	@Override
	public Task<Map<int[], Double>> createSuccessorTask() {
		Task<Map<int[], Double>> successorTask = new EuclideanTspTask(Constants.SUCCESSOR_TASK);
		Task[] inputList = new Task[this.cities.length - 1 - this.level];		
		successorTask.setInputList(inputList);
		
		// Successor's successor should be the current task's successor.
		successorTask.setSuccessorTaskId((Object)this.getSuccessorTaskId());
		successorTask.setJoinCounter(inputList.length);		
		
		return successorTask;
	}
	
	/*
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("taskId: " + this.taskId + ", ");
		sb.append("taskType: " + this.taskType + ", ");
		sb.append("prefix: " + Arrays.toString(this.prefix) + ", ");
		sb.append("taskPermutation: " + Arrays.toString(this.taskPermutation) + ", ");
		sb.append("successor: " + this.successorTaskId);
		sb.append("}");
		return sb.toString();
	}
	*/
}
