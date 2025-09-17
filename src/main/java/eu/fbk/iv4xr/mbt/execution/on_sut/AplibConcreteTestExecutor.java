package eu.fbk.iv4xr.mbt.execution.on_sut;

public interface AplibConcreteTestExecutor extends ConcreteTestExecutor{
	public void setMaxCyclePerGoal(int max);

	public int getMaxCylcePerGoal();
}
