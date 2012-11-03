/*
 * @author gautham
 */
package experiment;

/**
 * The Job represents the work to be done from the Client's perspective.
 *
 * @param <T> a type parameter, T, which represents the result type of the job computation.
 */
public interface Job<T> {

	/**
	 * Generates multiple tasks from this job. The client decomposes the problem (job), constructing a set of Task objects
	 *u
	 * @param space the space
	 */
	public void generateTasks(Space space);
	
	/**
	 * Collects results from the Space, composing them into a solution to the original problem.
	 *
	 * @param space the space
	 * @return t
	 */
	public T collectResults(Space space);
	
}
