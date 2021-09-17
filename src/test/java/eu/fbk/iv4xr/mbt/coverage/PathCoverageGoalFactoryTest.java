/**
 * 
 */
package eu.fbk.iv4xr.mbt.coverage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author kifetew
 *
 */
class PathCoverageGoalFactoryTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.PathCoverageGoalFactory#PathCoverageGoalFactory()}.
	 */
	@Test
	void testPathCoverageGoalFactory() {
		PathCoverageGoalFactory pathCoverageGoalFactory = new PathCoverageGoalFactory();
		assertNotNull(pathCoverageGoalFactory);
	}

	/**
	 * Test method for {@link eu.fbk.iv4xr.mbt.coverage.PathCoverageGoalFactory#getCoverageGoals()}.
	 */
	@Test
	void testGetCoverageGoals() {
		PathCoverageGoalFactory pathCoverageGoalFactory = new PathCoverageGoalFactory();
		assertNotNull(pathCoverageGoalFactory);
//		List<PathCoverageGoal> coverageGoals = pathCoverageGoalFactory.getCoverageGoals();
//		for (PathCoverageGoal goal : coverageGoals) {
//			System.out.println(goal.toString());
//		}
	}

}
