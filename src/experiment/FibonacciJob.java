package experiment;

import java.rmi.RemoteException;

import tasks.FibonacciTask;
import utils.Constants;
import api.Result;

public class FibonacciJob implements Job<Integer> {

	
	private int n;

	private long startTime; 
	
	public FibonacciJob(int n){
		this.n = n;
	}
	
	@Override
	public void generateTasks(Space space) {
		FibonacciTask task = new FibonacciTask(n, Constants.CHILD_TASK);
		this.startTime = System.nanoTime();
		try{
			space.put(task);
		}
		catch(RemoteException e){
			e.printStackTrace();
		}		
		
	}

	@Override
	public Integer collectResults(Space space) {
		Result<Integer> result = null;
		try {
			result = space.take();
			long elapsedTime = System.nanoTime() - this.startTime;
			System.out.println("Elapsed Time for the task: " + elapsedTime + " ns");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.getTaskReturnValue();
	}

}
