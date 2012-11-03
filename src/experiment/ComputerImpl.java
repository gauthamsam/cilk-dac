/*
 * @author gautham
 */
package experiment;


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import utils.Constants;

import api.Task;

/**
 * This class enables different tasks to be submitted by the remote clients using its remote reference
 * These tasks are run using the task's implementation of the execute method and the results are returned to the remote client.
 *
 * @author gautham
 */
public final class ComputerImpl extends UnicastRemoteObject implements Computer{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The space. */
	private Space space;	
	
	/**
	 * Instantiates a new implementation object for the Computer Interface.
	 *
	 * @throws RemoteException the remote exception
	 */
	public ComputerImpl() throws RemoteException{		
	}

	/**
	 * Different tasks can be submitted to this method
	 * The function checks to see if the task can be executed.
	 * If the task is ready, then it's executed and the intermediate results are stored in Space. Otherwise, split the task and put it back into Space.
	 * 
	 * @param <T> the generic type
	 * @param t the Task object
	 * @return Result the return value of the Task object's execute method
	 * @throws RemoteException the remote exception
	 */	
	@Override
	public <T> void execute(final Task<T> t) throws RemoteException {

		// If it's a regular task and if the base condition is not set (the task can be split into sub-tasks)
		final boolean canSplitTask = (t.getTaskType() == Constants.CHILD_TASK && ! t.isBaseCondition());
		
		//System.out.println("Computer: Elapsed time for task " + (result.getTaskId() + 1) + ": " + elapsedTime + " ms");
		
		// Creating a new thread for storing the tasks/results in Space so that the computer doesn't have to wait for the RMI call to return.
		Thread thread = new Thread() {
			@Override
			public void run() {
				long elapsedTime = 0;
					if (canSplitTask) {
						// Split the task into 'n' sub-tasks and 1 successor task and put them all in Space.
						long startTime = System.nanoTime();
						
						List<Task<T>> tasks = t.splitTask();
						Task<T> successorTask = t.createSuccessorTask();
						
						long endTime = System.nanoTime();
						elapsedTime = endTime - startTime;
						t.setTaskRunTime(elapsedTime);
						try {
							space.storeTasks(t, tasks, successorTask);
						} catch (RemoteException e) {							
							e.printStackTrace();
						}
					} else { // Execute the task and store the result on the Space
						long startTime = System.nanoTime();
						t.execute();
						long endTime = System.nanoTime();
						elapsedTime = endTime - startTime;
						t.setTaskRunTime(elapsedTime);
						try {
							space.storeResult(t);
						} catch (RemoteException e) {							
							e.printStackTrace();
						}
					}
					
					
			}
		};
		thread.start();			
	}
	
	

	/* (non-Javadoc)
	 * @see system.Computer#stop()
	 */
	@Override
	public void exit() throws RemoteException {
		System.out.println("Received command to stop.");
		System.exit(0);		
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {		
		String spaceDomainName = args[0];
		
		String spaceURL = "//" + spaceDomainName + "/" + Space.SERVICE_NAME;		
		Space remoteSpace = (Space) Naming.lookup(spaceURL);
		
		Computer computer = new ComputerImpl();
		remoteSpace.register(computer);
		computer.setSpace(remoteSpace);
		System.out.println("Computer ready.");
	}
	
	
	/* (non-Javadoc)
	 * @see system.Computer#setSpace(system.Computer2Space)
	 */
	@Override
	public void setSpace(Space space) throws Exception {
		this.space = space;		
	}
	
	
	
}

