/*
 * @author gautham
 */

package api;

import java.io.Serializable;
import java.util.List;

/**
 * An abstract class that acts as a link between the Computer implementation and the work that it needs to do, providing the way to start the work.
 * The client decomposes the original problem into a set of Task objects and they therefore represent the unit of work that is to be done by the Computers.  
 * This class defines both the child tasks and the successor tasks.
 * @param <T> a type parameter, T, which represents the result type of the task's computation.
 */
public abstract class Task<T> implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The task id. */
	protected Object taskId;
	
	/** The argument number for the successor task. */
	protected int argNo;
	
	/** The task type that differentiates between a child task and a successor task. */
	protected int taskType;
	
	/** The join counter that denotes the number of arguments that the successor task accepts. */
	protected int joinCounter;
	
	/** The successor task id. */
	protected Object successorTaskId;
	
	/** The input list that the successor is waiting for. */
	protected Task<T>[] inputList;
	
	/** The result of execution of the task. */
	protected Result<T> result;
	
	/** The time taken to run the task on the computer. */
	protected long elapsedTime;		
	
	
	/**
	 * Instantiates a new task.
	 *
	 * @param taskId the task id
	 */
	public Task(Object taskId){
		this.taskId = taskId;
	}
	
	/**
	 * Instantiates a new task.
	 *
	 * @param taskId the task id
	 * @param taskType the task type
	 */
	public Task(Object taskId, int taskType){
		this.taskId = taskId;
		this.taskType = taskType;
	}
	
	/**
	 * Gets the input list.
	 *
	 * @return the input list
	 */
	public Task<T>[] getInputList() {
		return this.inputList;
	}

	/**
	 * Sets the input list.
	 *
	 * @param inputList the new input list
	 */
	public void setInputList(Task<T>[] inputList) {
		this.inputList = inputList;
	}

	/**
	 * Sets the arg no.
	 *
	 * @param argNo the new arg no
	 */
	public void setArgNo(int argNo){
		this.argNo = argNo;
	}
	
	/**
	 * Gets the arg no.
	 *
	 * @return the arg no
	 */
	public int getArgNo(){
		return this.argNo;
	}
		

	/**
	 * Gets the successor task id.
	 *
	 * @return the successor task id
	 */
	public Object getSuccessorTaskId() {
		return successorTaskId;
	}

	/**
	 * Sets the successor task id.
	 *
	 * @param successorTaskId the new successor task id
	 */
	public void setSuccessorTaskId(Object successorTaskId) {
		this.successorTaskId = successorTaskId;
	}

	/**
	 * Gets the join counter.
	 *
	 * @return the join counter
	 */
	public int getJoinCounter() {
		return joinCounter;
	}

	/**
	 * Sets the join counter.
	 *
	 * @param joinCounter the new join counter
	 */
	public void setJoinCounter(int joinCounter) {
		this.joinCounter = joinCounter;
	}	

	/**
	 * Gets the task id.
	 *
	 * @return the task id
	 */
	public Object getTaskId() {
		return taskId;
	}

	/**
	 * Sets the task id.
	 *
	 * @param taskId the new task id
	 */
	public void setTaskId(Object taskId) {
		this.taskId = taskId;
	}

	/**
	 * Gets the task type.
	 *
	 * @return the task type
	 */
	public int getTaskType() {
		return taskType;
	}

	/**
	 * Sets the task type.
	 *
	 * @param taskType the new task type
	 */
	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}
	
	
	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public Result<T> getResult() {
		return result;
	}

	/**
	 * Sets the result.
	 *
	 * @param result the new result
	 */
	public void setResult(Result<T> result) {
		this.result = result;
	}
	
	/**
	 * Sets the task run time.
	 *
	 * @param elapsedTime the new task run time
	 */
	public void setTaskRunTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	/**
	 * Gets the task run time.
	 *
	 * @return the task run time
	 */
	public long getTaskRunTime() {
		return elapsedTime;
	}	


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return String.valueOf(this.taskId);
	}
	/**
	 * Splits the task up to a desired number of subtasks as defined by the implementation classes. 
	 *
	 * @return list
	 */
	public abstract List<Task<T>> splitTask();
	
	/**
	 * Creates the successor task.
	 *
	 * @return task
	 */
	public abstract Task<T> createSuccessorTask();	
	
	/**
	 * Executes a given task.
	 * This method returns the result of the implementing task's computation and thus its return type is T.
	 *
	 * @return Object of type T
	 */
	public abstract Result<T> execute();
	
	/**
	 * Checks if the base condition is met to decide whether or not to decompose the task
	 *
	 * @return true, if is base condition
	 */
	public abstract boolean isBaseCondition();		
	
}
