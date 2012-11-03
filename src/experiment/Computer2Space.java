/*
 * @author gautham
 */
package experiment;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import api.Space;
import api.Task;

/**
 * The remote interface that the ComputeServers use to register themselves with the ComputeSpace.
 */
public interface Computer2Space extends Remote{
		
	/**
	 * Registers the Computer and creates a ComputerProxy which runs as a separate thread to process the submitted Tasks and to return the Results back to the ComputeSpace.
	 *
	 * @param computer the Computer to be registered
	 * @return space
	 * @throws RemoteException the remote exception
	 */
	Space register(Computer computer) throws RemoteException;	
	
	/**
	 * Store the newly created sub-tasks and the successor task in the appropriate data structures in Space.
	 *
	 * @param <T> the generic type
	 * @param parentTask the parent task
	 * @param childTasks the child tasks
	 * @param successorTask the successor task
	 * @throws RemoteException the remote exception
	 */
	<T> void storeTasks(Task<T> parentTask, List<Task<T>> childTasks, Task<T> successorTask) throws RemoteException;	
	
	
	
	/**
	 * Store the result of the currently executed task (may be a sub-task or a successor task) in Space
	 *
	 * @param <T> the generic type
	 * @param task the successor task
	 * @throws RemoteException the remote exception
	 */
	<T> void storeResult(Task<T> task) throws RemoteException;    
	
}
