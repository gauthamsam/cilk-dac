/*
 * @author gautham
 */
package experiment;

import java.rmi.Remote;
import java.rmi.RemoteException;


import api.Task;

/**
 * This is the remote interface through which different tasks can be executed by the ComputeSpace.
 * These tasks are run using the task's implementation of the execute method and the results are returned to the ComputeSpace
 * Any object that implements this interface can be a remote object.
 */
public interface Computer extends Remote{
	/** The name under which the RMI registry binds the remote reference. */
	public static final String SERVICE_NAME = "Computer";
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
	 * Sets the space.
	 *
	 * @param remoteSpace the new space
	 * @throws Exception the exception
	 */
	public void setSpace(Space remoteSpace) throws Exception;
}
