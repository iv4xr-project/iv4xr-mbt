/**
 * 
 */
package eu.fbk.iv4xr.mbt.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.TheReplacementFunction;
import org.evosuite.ShutdownTestWriter;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.FitnessReplacementFunction;
import org.evosuite.ga.SecondaryObjective;
import org.evosuite.ga.metaheuristics.BreederGA;
import org.evosuite.ga.metaheuristics.CellularGA;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
//import org.evosuite.ga.metaheuristics.LIPS;
import org.evosuite.ga.metaheuristics.MIO;
import org.evosuite.ga.metaheuristics.MonotonicGA;
import org.evosuite.ga.metaheuristics.NSGAII;
import eu.fbk.iv4xr.mbt.algorithm.random.RandomSearch;
import org.evosuite.ga.metaheuristics.SPEA2;
import org.evosuite.ga.metaheuristics.StandardChemicalReaction;
import org.evosuite.ga.metaheuristics.StandardGA;
import org.evosuite.ga.metaheuristics.SteadyStateGA;
import org.evosuite.ga.metaheuristics.mulambda.MuLambdaEA;
import org.evosuite.ga.metaheuristics.mulambda.MuPlusLambdaEA;
import org.evosuite.ga.metaheuristics.mulambda.OnePlusLambdaLambdaGA;
import org.evosuite.ga.metaheuristics.mulambda.OnePlusOneEA;
import org.evosuite.ga.operators.crossover.CrossOverFunction;
import org.evosuite.ga.operators.selection.BinaryTournamentSelectionCrowdedComparison;
import org.evosuite.ga.operators.selection.FitnessProportionateSelection;
//import org.evosuite.ga.operators.selection.RandomKSelection;
import org.evosuite.ga.operators.selection.RankSelection;
import org.evosuite.ga.operators.selection.SelectionFunction;
import org.evosuite.ga.operators.selection.TournamentSelection;
//import org.evosuite.ga.operators.selection.TournamentSelectionRankAndCrowdingDistanceComparator;
import org.evosuite.ga.stoppingconditions.GlobalTimeStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxTimeStoppingCondition;
import org.evosuite.ga.stoppingconditions.RMIStoppingCondition;
import org.evosuite.ga.stoppingconditions.SocketStoppingCondition;
import org.evosuite.ga.stoppingconditions.StoppingCondition;
import org.evosuite.ga.stoppingconditions.ZeroFitnessStoppingCondition;
import org.evosuite.statistics.StatisticsListener;
import org.evosuite.strategy.PropertiesSearchAlgorithmFactory;
import org.evosuite.testcase.localsearch.BranchCoverageMap;
import org.evosuite.testsuite.RelativeSuiteLengthBloatControl;
import org.evosuite.testsuite.TestSuiteReplacementFunction;
import org.evosuite.utils.ResourceController;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.MBTProperties.ModelCriterion;
import eu.fbk.iv4xr.mbt.algorithm.ga.mosa.MOSA;
import eu.fbk.iv4xr.mbt.algorithm.operators.crossover.ExtendedSinglePointRelativePathCrossOver;
import eu.fbk.iv4xr.mbt.algorithm.operators.crossover.SinglePointPathCrossOver;
import eu.fbk.iv4xr.mbt.algorithm.operators.crossover.SinglePointRelativePathCrossOver;
import eu.fbk.iv4xr.mbt.coverage.CoverageGoal;
import eu.fbk.iv4xr.mbt.coverage.CoverageGoalFactory;
import eu.fbk.iv4xr.mbt.coverage.KTransitionCoverageGoalFactory;
import eu.fbk.iv4xr.mbt.coverage.PathCoverageGoalFactory;
import eu.fbk.iv4xr.mbt.coverage.StateCoverageGoalFactory;
import eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoalFactory;
import eu.fbk.iv4xr.mbt.efsm.EFSMFactory;
import eu.fbk.iv4xr.mbt.testcase.MBTChromosome;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomParameterLengthTestFactory;
import eu.fbk.iv4xr.mbt.testcase.TestFactory;
import eu.fbk.iv4xr.mbt.testcase.secondaryobjectives.MinimizeExceptionsSO;
import eu.fbk.iv4xr.mbt.testcase.secondaryobjectives.MinimizeLengthSO;
import sun.misc.Signal;

/**
 * @author kifetew
 *
 */
public class AlgorithmFactory<T extends Chromosome> extends PropertiesSearchAlgorithmFactory<T>{

	protected CoverageGoalFactory<?> getFitnessFactory(ModelCriterion criterion){
		switch (criterion){
		case STATE:
			return new StateCoverageGoalFactory();
		case TRANSITION:
			return new TransitionCoverageGoalFactory();
		case KTRANSITION:
			return new KTransitionCoverageGoalFactory();
		case PATH:
			return new PathCoverageGoalFactory();
		default:
			throw new RuntimeException("Unsupported model coverage criterion: " + MBTProperties.MODELCRITERION);	
		}
	}
	
	protected List<CoverageGoal> getCoverageGoals(){
		List<CoverageGoal> goals = new ArrayList<CoverageGoal>();
		for (ModelCriterion criterion : MBTProperties.MODELCRITERION) {
			goals.addAll(getFitnessFactory(criterion).getCoverageGoals());
		}
		return goals;
	}
	
	protected ChromosomeFactory<T> getChromosomeFactory() {
		TestFactory testFactory = getTestFactory ();
		
		switch (MBTProperties.STRATEGY) {
		
		case GA:
		case DYNAMOSA:
			return new RandomLengthTestChromosomeFactory<T>(testFactory);
		case MODEL_CHECKING:
			return new RandomLengthTestChromosomeFactory<T>(testFactory);
		default:
			throw new RuntimeException("Unsupported generation strategy: " + MBTProperties.STRATEGY);
		}
	}
	
	protected TestFactory getTestFactory() {
		switch (MBTProperties.TEST_FACTORY) {
		case RANDOM_LENGTH:
			return new RandomLengthTestFactory(EFSMFactory.getInstance().getEFSM());
		case RANDOM_LENGTH_PARAMETER:
			return new RandomParameterLengthTestFactory(EFSMFactory.getInstance().getEFSM());
		default:
			throw new RuntimeException("Unsupported test factory: " + MBTProperties.TEST_FACTORY);
		}
	}


	protected GeneticAlgorithm<T> getGeneticAlgorithm(ChromosomeFactory<T> factory) {
		switch (MBTProperties.ALGORITHM) {
			case ONE_PLUS_ONE_EA:
				logger.info("Chosen search algorithm: (1+1)EA");
				return new OnePlusOneEA<>(factory);
			case MU_PLUS_LAMBDA_EA:
				logger.info("Chosen search algorithm: (Mu+Lambda)EA");
				return new MuPlusLambdaEA<>(factory, Properties.MU, Properties.LAMBDA);
			case MU_LAMBDA_EA:
				logger.info("Chosen search algorithm: (Mu,Lambda)EA");
				return new MuLambdaEA<>(factory, Properties.MU, Properties.LAMBDA);
			case MONOTONIC_GA: {
				logger.info("Chosen search algorithm: MonotonicGA");
				MonotonicGA<T> ga = new MonotonicGA<T>(factory);
				if (Properties.REPLACEMENT_FUNCTION == TheReplacementFunction.FITNESSREPLACEMENT) {
					// user has explicitly asked for this replacement function
					ga.setReplacementFunction(new FitnessReplacementFunction());
				} else {
					// use default
					ga.setReplacementFunction(new TestSuiteReplacementFunction());
				}
				return ga;
			}
			case CELLULAR_GA: {
				logger.info("Chosen search algorithm: CellularGA");
				CellularGA<T> ga = new CellularGA<T>(Properties.MODEL, factory);
				if (Properties.REPLACEMENT_FUNCTION == TheReplacementFunction.FITNESSREPLACEMENT) {
					// user has explicitly asked for this replacement function
					ga.setReplacementFunction(new FitnessReplacementFunction());
				} else {
					// use default
					ga.setReplacementFunction(new TestSuiteReplacementFunction());
				}
				return ga;
			}
			case STEADY_STATE_GA: {
			logger.info("Chosen search algorithm: Steady-StateGA");
				logger.info("Chosen search algorithm: Steady-StateGA");
				SteadyStateGA<T> ga = new SteadyStateGA<T>(factory);
				if (Properties.REPLACEMENT_FUNCTION == TheReplacementFunction.FITNESSREPLACEMENT) {
					// user has explicitly asked for this replacement function
					ga.setReplacementFunction(new FitnessReplacementFunction());
				} else {
					// use default
					ga.setReplacementFunction(new TestSuiteReplacementFunction());
				}
				return ga;
			}
			case BREEDER_GA:
				logger.info("Chosen search algorithm: BreederGA");
				return new BreederGA<>(factory);
			case RANDOM_SEARCH:
				logger.info("Chosen search algorithm: Random");
				return new RandomSearch<T>(factory);
			case NSGAII:
				logger.info("Chosen search algorithm: NSGAII");
				return new NSGAII<>(factory);
			case SPEA2:
				logger.info("Chosen search algorithm: SPEA2");
				return new SPEA2<>(factory);
			case MOSA:
				logger.info("Chosen search algorithm: MOSA");
				return new MOSA<>(factory);
			// evosuite 1.0.7
			/*
			case DYNAMOSA:
				logger.info("Chosen search algorithm: DynaMOSA");
				return new DynaMOSA<>(factory);
			*/				
			case ONE_PLUS_LAMBDA_LAMBDA_GA:
				logger.info("Chosen search algorithm: 1 + (lambda, lambda)GA");
				return new OnePlusLambdaLambdaGA<>(factory, Properties.LAMBDA);
			case MIO:
				logger.info("Chosen search algorithm: MIO");
				return new MIO<>(factory);
			case STANDARD_CHEMICAL_REACTION:
				logger.info("Chosen search algorithm: Standard Chemical Reaction Optimization");
				return new StandardChemicalReaction<>(factory);
			case MAP_ELITES:
				logger.info("Chosen search algorithm: MAP-Elites");
				throw new RuntimeException("MAPElites only works on TestChromosome, not on TestSuiteChromosome");
			// evosuite 1.0.7
			/*
			case LIPS:
				logger.info("Chosen search algorithm: LIPS");
				return new LIPS<>(factory);
			*/
			default:
				logger.info("Chosen search algorithm: StandardGA");
				return new StandardGA<>(factory);
		}
	}
	
	protected SelectionFunction<T> getSelectionFunction() {
		switch (Properties.SELECTION_FUNCTION) {
		case ROULETTEWHEEL:
			return new FitnessProportionateSelection<>();
		case TOURNAMENT:
			return new TournamentSelection<>();
		case BINARY_TOURNAMENT:
		    return new BinaryTournamentSelectionCrowdedComparison<>();
		// evosuite 1.0.7
		/*
		case RANK_CROWD_DISTANCE_TOURNAMENT:
		    return new TournamentSelectionRankAndCrowdingDistanceComparator<>();
		case BESTK:
			return new BestKSelection<>();
		case RANDOMK:
			return new RandomKSelection<>();
		*/
		default:
			return new RankSelection<>();
		}
	}
	
	protected CrossOverFunction getCrossoverFunction() {
		switch (MBTProperties.CROSSOVER_FUNCTION) {
		case SINGLEPOINTFIXED:
			return new SinglePointPathCrossOver();
		case SINGLEPOINTRELATIVE:
			return new SinglePointRelativePathCrossOver();
		case SINGLEPOINT:
			return new SinglePointPathCrossOver();
		case EXTENDEDSINGLEPOINTRELATIVE:
			return new ExtendedSinglePointRelativePathCrossOver();	
		default:
			throw new RuntimeException("Unknown crossover function: "
			        + Properties.CROSSOVER_FUNCTION);
		}
	}

	@Override
	public GeneticAlgorithm<T> getSearchAlgorithm() {
		ChromosomeFactory<T> factory = getChromosomeFactory();
		
		// FIXXME
		GeneticAlgorithm<T> ga = getGeneticAlgorithm(factory);

		if (Properties.NEW_STATISTICS)
			ga.addListener(new StatisticsListener());

		// How to select candidates for reproduction
		SelectionFunction<T> selectionFunction = getSelectionFunction();
		selectionFunction.setMaximize(false);
		ga.setSelectionFunction(selectionFunction);

		// evosuite 1.0.7
		//RankingFunction<T> ranking_function = getRankingFunction();
		//ga.setRankingFunction(ranking_function);

		// When to stop the search
		StoppingCondition stopping_condition = getStoppingCondition();
		ga.setStoppingCondition(stopping_condition);
		// ga.addListener(stopping_condition);
		if (Properties.STOP_ZERO) {
			ga.addStoppingCondition(new ZeroFitnessStoppingCondition());
		}

		if (!(stopping_condition instanceof MaxTimeStoppingCondition)) {
			ga.addStoppingCondition(new GlobalTimeStoppingCondition());
		}

//		if (ArrayUtil.contains(Properties.CRITERION, Criterion.MUTATION)
//		        || ArrayUtil.contains(Properties.CRITERION, Criterion.STRONGMUTATION)) {
//			if (Properties.STRATEGY == Strategy.ONEBRANCH)
//				ga.addStoppingCondition(new MutationTimeoutStoppingCondition());
//			else
//				ga.addListener(new MutationTestPool());
//			// } else if (Properties.CRITERION == Criterion.DEFUSE) {
//			// if (Properties.STRATEGY == Strategy.EVOSUITE)
//			// ga.addListener(new DefUseTestPool());
//		}
		ga.resetStoppingConditions();
		ga.setPopulationLimit(getPopulationLimit());

		// How to cross over
		CrossOverFunction crossover_function = getCrossoverFunction();
		ga.setCrossOverFunction(crossover_function);

		// What to do about bloat
//		RelativeTestLengthBloatControl bloat_control = new RelativeTestLengthBloatControl();
//		ga.setBloatControl(bloat_control);
//		ga.addListener(bloat_control);

		if (Properties.CHECK_BEST_LENGTH) {
			RelativeSuiteLengthBloatControl bloat_control = new RelativeSuiteLengthBloatControl();
			ga.addBloatControl(bloat_control);
			ga.addListener(bloat_control);
		}
		// ga.addBloatControl(new MaxLengthBloatControl());

		setSecondaryObjectives();

		// Some statistics
		//if (Properties.STRATEGY == Strategy.EVOSUITE)
		//	ga.addListener(SearchStatistics.getInstance());
		// ga.addListener(new MemoryMonitor());
		// ga.addListener(MutationStatistics.getInstance());
		// ga.addListener(BestChromosomeTracker.getInstance());

		if (Properties.DYNAMIC_LIMIT) {
			// max_s = GAProperties.generations * getBranches().size();
			// TODO: might want to make this dependent on the selected coverage
			// criterion
			// TODO also, question: is branchMap.size() really intended here?
			// I think BranchPool.getBranchCount() was intended
			Properties.SEARCH_BUDGET = Properties.SEARCH_BUDGET
			        * (BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getNumBranchlessMethods(Properties.TARGET_CLASS) + BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getBranchCountForClass(Properties.TARGET_CLASS) * 2);
			stopping_condition.setLimit(Properties.SEARCH_BUDGET);
			logger.info("Setting dynamic length limit to " + Properties.SEARCH_BUDGET);
		}

		if (Properties.LOCAL_SEARCH_RESTORE_COVERAGE) {
			org.evosuite.ga.metaheuristics.SearchListener map = BranchCoverageMap.getInstance(); 
			ga.addListener(map);
		}

		if (Properties.SHUTDOWN_HOOK) {
			// ShutdownTestWriter writer = new
			// ShutdownTestWriter(Thread.currentThread());
			ShutdownTestWriter writer = new ShutdownTestWriter();
			ga.addStoppingCondition(writer);
			RMIStoppingCondition rmi = RMIStoppingCondition.getInstance();
			ga.addStoppingCondition(rmi);

			if (Properties.STOPPING_PORT != -1) {
				SocketStoppingCondition ss = new SocketStoppingCondition();
				ss.accept();
				ga.addStoppingCondition(ss);
			}

			// Runtime.getRuntime().addShutdownHook(writer);
			Signal.handle(new Signal("INT"), writer);
		}

		ga.addListener(new ResourceController());
		return ga;
	}

	private static void setSecondaryObjectives() {
		for (MBTProperties.SecondaryObjective secondaryObjective : MBTProperties.SECONDARY_OBJECTIVE) {
		      try {
		        SecondaryObjective<MBTChromosome> secondaryObjectiveInstance = null;
		        switch (secondaryObjective) {
		          case AVG_LENGTH:
		          case MAX_LENGTH:
		          case TOTAL_LENGTH:
		            secondaryObjectiveInstance = new MinimizeLengthSO();
		            break;
		          case EXCEPTIONS:
		            secondaryObjectiveInstance = new MinimizeExceptionsSO();
		            break;
		          default:
		            throw new RuntimeException("ERROR: asked for unknown secondary objective \""
		                + secondaryObjective.name() + "\"");
		        }
		        MBTChromosome.addSecondaryObjective(secondaryObjectiveInstance);
		      } catch (Throwable t) {
		      } // Not all objectives make sense for tests
		    }
		
	}


	public Collection<? extends FitnessFunction> getCoverageGoals(MBTProperties.ModelCriterion criterion) {
		List<CoverageGoal> goals = new ArrayList<CoverageGoal>();
		switch (criterion) {
		case STATE:
			goals.addAll(new StateCoverageGoalFactory().getCoverageGoals());
			break;
		case TRANSITION:
			goals.addAll(new TransitionCoverageGoalFactory().getCoverageGoals());
			break;
		case KTRANSITION:
			goals.addAll(new KTransitionCoverageGoalFactory().getCoverageGoals());
			break;
		case PATH:
			goals.addAll(new PathCoverageGoalFactory().getCoverageGoals());
		default:
			throw new RuntimeException("Unsupported model coverage criterion: " + criterion);
		}
		return goals;
	}

}
