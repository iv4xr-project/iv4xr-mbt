package eu.fbk.iv4xr.mbt.framework;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
 * A TestFactory is an interface for any program that can produces 'tests'. Use
 * this interface to wrap around a testing tool or algorithm that produces a
 * test case or a test suite. This interface defines how such an algorithm can
 * be interfaced with {@link eu.iv4xr.framework.mainConcepts.TestAgent}.
 * 
 * <p>
 * To interface a test algorithm to test-agents we see the algorithm as a 'test
 * factory'. As such, the algorithm produces either test-cases (if it aims to
 * produce a whole test suite) or just test-steps (if it aims to just produce a
 * test-case). To be understood by the test agents, these test-cases or
 * test-steps have to be formulated as goal-structures (instances of
 * {@link nl.uu.cs.aplib.mainConcepts.GoalStructure}), which can then be given
 * to a test-agent to be executed.
 * 
 * <p>
 * The algorithm can be thought to produce a sequence of goal-structures; the
 * method {@link #nextGoal()} can be invoked to obtain the next goal-structure,
 * and then given to the method {@link #execute(GoalStructure)} to be executed.
 * The latter gives the structure to the designated agent, and the agent will
 * execute the tactics embedded in the goal-structure to accomplish the goals in
 * the goal-structure.
 * 
 * <p>
 * We should note that while goals formulate states we desire to achieve,
 * something should be invoked to actually 'solve' them. In iv4xr solvers are
 * expressed in terms of so-called tactics
 * {@link nl.uu.cs.aplib.mainConcepts.Tactic}. It is the responsibility of the
 * test algorithm to provide suitable tactics for the goals it sends to the
 * test-agent. The test-agent itself mainly provides a runtime system for
 * managing goals and executing the tactics (this involves determining which
 * primitive-tactics can execute and choosing which one to execute, and for
 * solving which goal).
 * 
 * @author Wish
 *
 */

public interface ITestFactory {
	
	/**
	 * Attach a test-agent to this factoru. This will be the designated agent for
	 * executing the goal-structures produced by this factory. The agent is assumed
	 * to already have an Environment
	 * ({@link nl.uu.cs.aplib.mainConcepts.Environment}) attached to it. The
	 * latter forms the actual interface to the System under Test (SUT).
	 */
	public void attachAgent(TestAgent agent) ;
	
	/**
	 * Return the test-agent that is attached to this factory.
	 */
	public TestAgent getAgent() ;
	
	/**
	 * Reset the test-agent back to its initial state, and also reset
	 * the Environment ({@link nl.uu.cs.aplib.mainConcepts.Environment}) that
	 * is attached to the agent. The latter forms the actual interface to the
	 * System under Test (SUT). Reseting the Environment might entail resetting
	 * the SUT as well.
	 */
	public void reset() ;
	
	/**
	 * Return the next goal-structure to execute. A goal-structure might represent
	 * a whole test-case, or it might represent a single test-step that constitutes
	 * a test-case. The method returns null if there is no next goal-structure
	 * to return.
	 */
	public GoalStructure nextGoal() ;
	
	/**
	 * Gives the goal/test-case G for the agent attached to this executor, to be
	 * executed. After the execution, the method returns the goal. It can be
	 * inspected for its status (e.g. if it was achieved/success or failed).
	 * 
	 * <p>When the 'reset' parameter is true, it indicates that the test agent should
	 * be reset first (using {@link #reset()} before executing the goal.
	 */
	public GoalStructure execute(GoalStructure G, boolean reset) ;
	
	/**
	 * Return test results collected by the test-agent during its runs. The agent
	 * stores this data in a 'data collector'
	 * ({@link eu.iv4xr.framework.mainConcepts.TestDataCollector}) attached to it.
	 * The default implementation of this method simply returns this data collector.
	 */
	default public TestDataCollector getTestResults() {
		return  getAgent().getTestDataCollector() ;
	}
	

}
