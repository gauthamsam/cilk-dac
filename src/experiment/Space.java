/*
 * @author gautham
 */
package experiment;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


import api.Result;

import api.Task;

/**
 * The remote interface through which different tasks are submitted and results obtained
 * It acts as a channel for passing messages between Client and ComputeServers.
   The implementation defines mechanisms to wait for an available Task, to hold them and assign it to the ComputeServers and then process the Result objects.
 */
public interface Space extends Remote {

	/** The name under which the RMI registry binds the remote reference. */
	public static final String SERVICE_NAME = "Space";
	
	/**
	 * A remote method used by the Clients to put the Task into the ComputeSpace.
	 *
	 * @param <T> the generic type
	 * @param task the actual task
	 * @throws RemoteException the remote exception
	 */
	<T> void put( Task<T> task ) throws RemoteException;
	
	
	/**
	 * A remote method to take the Result that has been computed by the ComputeServers. This method blocks until a Result is available to return to the client
	 *
	 * @param <T> the generic type
	 * @return result
	 * @throws RemoteException the remote exception
	 * @throws InterruptedException the interrupted exception
	 */
	<T> Result<T> take() throws RemoteException, InterruptedException;
	
	
	/**
	 * A remote method to stop the execution of the ComputeSpace.
	 *
	 * @throws RemoteException the remote exception
	 */
	void stop() throws RemoteException;
	
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
