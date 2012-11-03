/*
 * @author gautham
 */
package system;

import java.rmi.Remote;
import java.rmi.RemoteException;

import api.Task;

/**
 * This is the remote interface through which different tasks can be executed by the ComputeSpace.
 * These tasks are run using the task's implementation of the execute method and the results are returned to the ComputeSpace.
 * Each task can either be executed right away or be decomposed into multiple subtasks. In the earlier case, the result is stored in the space while in the latter case, the subtasks are stored.
 * Any object that implements this interface can be a remote object.
 */
public interface Computer extends Remote{
	
	/**
	 * A remote method to which different tasks can be submitted by the remote clients.
	 * These tasks are run using the task's implementation of the execute method and the results are returned to the remote client.
	 *
	 * @param <T> the generic type
	 * @param t the t
	 * @return result
	 * @throws RemoteException the remote exception
	 */
	public <T> void execute(Task<T> t) throws RemoteException;
	
	/**
	 * Stop the compute instance.
	 *
	 * @throws RemoteException the remote exception
	 */
	public void exit() throws RemoteException;
	

	/**
	 * Sets the remote reference to space.
	 *
	 * @param space the new space
	 * @throws Exception the exception
	 */
	public void setSpace(Computer2Space space) throws Exception;
}
