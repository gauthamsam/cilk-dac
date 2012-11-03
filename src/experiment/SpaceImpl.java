
/*
 * @author gautham
 */

package experiment;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


import utils.Constants;
import api.Result;

import api.Task;


/**
 * This acts as a channel for passing messages between Client and
 * ComputeServers. It defines mechanisms to hold Tasks that are created by the
 * Client jobs, to assign it to the ComputeServers and then process the Result
 * objects.
 */
public class SpaceImpl extends UnicastRemoteObject implements Space
		{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** A blocking dequeue that stores the Tasks that are ready to be executed. */
	private BlockingDeque<Task> readyTasks;
	
	/**
	 * A blocking dequeue that stores the Results submitted by the ComputeServers.
	 */
	private BlockingDeque<Result<?>> resultQueue;
	
	/** The waiting tasks. */
	private Map<Object, Task> waitingTasks;

	/** A mapping between the computerId and the actual Computer Object. */
	private Map<Integer, ComputerProxy> computerMap;

	/** The computer id. */
	private int computerId;

	
	private static Space space;
	
	public static Space getInstance() throws RemoteException{
		if(space == null){
			space = new SpaceImpl();
		}
		return space;
	}
	/**
	 * Instantiates a new space impl.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	private SpaceImpl() throws RemoteException {
		super();
		readyTasks = new LinkedBlockingDeque<Task>();
		resultQueue = new LinkedBlockingDeque<Result<?>>();
		computerMap = Collections.synchronizedMap(new HashMap<Integer, ComputerProxy>());
		waitingTasks = Collections.synchronizedMap(new HashMap<Object, Task>());
	}

	/*
	 * Registers the Computer and creates a ComputerProxy which runs as a
	 * separate thread to process the submitted Tasks and to return the Results
	 * back to the ComputeSpace
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see system.Computer2Space#register(system.Computer)
	 */
	
	public synchronized Space register(Computer computer) throws RemoteException {
		computerId++;
		ComputerProxy proxy = new ComputerProxy(computer, computerId);
		computerMap.put(computerId, proxy);
		System.out.println("Registering computer " + computerId);
		proxy.start();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see api.Space#put(api.Task)
	 */
	@Override
	public <T> void put(Task<T> task) throws RemoteException {
		System.out.println("Space put.");
		readyTasks.addFirst(task);
		//this.taskMap.put(task.getTaskId(), task);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see api.Space#take()
	 */
	@Override
	public Result<?> take() throws RemoteException, InterruptedException {
		return resultQueue.take();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see api.Space#stop()
	 */
	@Override
	public void stop() throws RemoteException {
		System.out.println("Stopping all the registered Computers.");
		System.out.println("--------------------------------------");
		for (Entry<Integer, ComputerProxy> entry : computerMap.entrySet()) {
			System.out.println("Stopping computer " + entry.getKey());
			ComputerProxy computer = entry.getValue();
			computer.exit();
		}

		System.out.println("--------------------------------------");
		System.out.println("Stopping Space.");
		System.exit(0);
	}

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		// Construct & set a security manager to allow downloading of classes
		// from a remote codebase
		System.setSecurityManager(new RMISecurityManager());
		// instantiate a space object
		Space space = SpaceImpl.getInstance();
		// construct an rmiregistry within this JVM using the default port
		Registry registry = LocateRegistry.createRegistry(1099);
		// bind space in rmiregistry.
		registry.rebind(Space.SERVICE_NAME, space);
		System.out.println("Space is ready.");

	}

	/*
	 * This thread's run method loops forever, removing tasks from the task
	 * queue, invoking the associated Computer's execute method with the task as
	 * its argument, and putting the returned Result object in the result queue
	 * for retrieval by the client.
	 */
	/**
	 * It represents the remote proxy to the ComputeServer.
	 */
	private class ComputerProxy extends Thread {

		/** The computer. */
		private Computer computer;

		/** The computer id. */
		private int computerId;

		/**
		 * Instantiates a new computer proxy.
		 * 
		 * @param c the c
		 * @param computerId the computer id
		 */
		public ComputerProxy(Computer c, int computerId) {
			this.computer = c;
			this.computerId = computerId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			Task<?> t = null;
			while (true) {
				try {
					t = readyTasks.takeFirst();
					// The proxy doesn't wait for the computer to execute the task and put the result back to Space.
					computer.execute(t);					
				} catch (RemoteException e) {
					/*
					 * The Space accommodates faulty computers: If a computer
					 * that is running a task returns a RemoteException, the
					 * task is assigned to another computer.
					 */
					System.out.println("Remote Exception while executing task "
							+ t.getClass().getName() + " from Computer "
							+ this.computerId);
					// Adding the task back to the task queue
					System.out.println("Adding the task back to the task queue to be assigned to another Computer");
					readyTasks.addFirst(t);					
					break;
				} catch (InterruptedException e) {
					System.out.println("Interrupted Exception");
				}
			}
		}

		/**
		 * Stop the computer instance
		 */
		public void exit() {
			try {
				computer.exit();
			} catch (RemoteException e) {

			}
		}
	}

	/* (non-Javadoc)
	 * @see system.Computer2Space#storeResult(api.Task)
	 */
	@Override
	public <T> void storeResult(Task<T> task) {
		//System.out.println("Store result ---------------------");
		//System.out.println("Task " + task);
		String type = task.getTaskType() == Constants.CHILD_TASK ? "Regular Task" : "Successor Task";
		System.out.println(task.getTaskId() + "; " + type + "; " + Arrays.toString(task.getInputList()) + "; " + task.getSuccessorTaskId() + "; " + task.getTaskRunTime());
		Object successorTaskId = task.getSuccessorTaskId();
		Task<T> successorTask = this.waitingTasks.get(successorTaskId);
		// if successorTask == null, then that's the last task to be executed
		if(successorTask == null){
			storeFinalResult(task.getResult());
			return;
		}
		//System.out.println("Successor in storeResult: " + successorTaskId);
		
		Task<T>[] inputs = successorTask.getInputList();
		
		inputs[task.getArgNo()] = task;
		int joinCounter = successorTask.getJoinCounter() - 1;
		//System.out.println("Join counter: " + joinCounter);
		successorTask.setJoinCounter(joinCounter);		
		
		// This task has been executed. Hence remove it from the ready queue.
		//this.readyTasks.remove(task.getTaskId());
		if(joinCounter == 0){ // If the successor task has all its arguments set, move it from the waiting list to ready list
			this.waitingTasks.remove(successorTaskId);
			this.readyTasks.addFirst(successorTask);
		}
		

	}

	
	/* (non-Javadoc)
	 * @see system.Computer2Space#storeTasks(api.Task, java.util.List, api.Task)
	 */
	@Override
	public <T> void storeTasks(Task<T> parentTask, List<Task<T>> childTasks, Task<T> successorTask)
			throws RemoteException {
		
		String type = parentTask.getTaskType() == Constants.CHILD_TASK ? "Regular Task" : "Successor Task";
		System.out.println(parentTask.getTaskId() + "; " + type + "; " + Arrays.toString(parentTask.getInputList()) + "; " + parentTask.getSuccessorTaskId() + "; " + parentTask.getTaskRunTime());
		//System.out.println("Successor " + successorTask);
		this.waitingTasks.put(successorTask.getTaskId(), successorTask);
		
		// if the parent task is not the root task
		if(parentTask.getSuccessorTaskId() != null){
			/* 
			 * Update the parent successor's inputList.
			 * In that list, the parent task must be substituted with the child tasks' successor task 
			 */
			Task<T> parentSuccessor = this.waitingTasks.get(parentTask.getSuccessorTaskId());
			Task<T>[] parentInputList = parentSuccessor.getInputList();
			successorTask.setArgNo(parentTask.getArgNo());
			parentInputList[parentTask.getArgNo()] = successorTask;
			
			//System.out.println("Input No. " + parentTask.getArgNo() + " of " + parentSuccessor + " is " + successorTask);							
		}
				
		for(Task<T> t : childTasks){
			// Set the successor task for the newly created tasks
			t.setSuccessorTaskId(successorTask.getTaskId());
			
			// The regular tasks must go the ready list while the successor tasks must go to the waiting list
			if (t.getTaskType() == Constants.CHILD_TASK){				
				this.readyTasks.addFirst(t);				
			}
			else{
				this.waitingTasks.put(t.getTaskId(), t);
			}
		}		
	}

	
	private <T> void storeFinalResult(Result<T> result){
		System.out.println("Storing final result");
		// process the result		
		try {
			resultQueue.put(result);			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	
}
