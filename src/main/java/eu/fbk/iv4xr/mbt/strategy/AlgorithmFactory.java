/**
 * 
 */
package eu.fbk.iv4xr.mbt.strategy;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.Properties.Strategy;
import org.evosuite.Properties.TheReplacementFunction;
import org.evosuite.ShutdownTestWriter;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.coverage.mutation.MutationTestPool;
import org.evosuite.coverage.mutation.MutationTimeoutStoppingCondition;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessReplacementFunction;
import org.evosuite.ga.metaheuristics.BreederGA;
import org.evosuite.ga.metaheuristics.CellularGA;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.LIPS;
import org.evosuite.ga.metaheuristics.MIO;
import org.evosuite.ga.metaheuristics.MonotonicGA;
import org.evosuite.ga.metaheuristics.NSGAII;
import org.evosuite.ga.metaheuristics.RandomSearch;
import org.evosuite.ga.metaheuristics.SPEA2;
import org.evosuite.ga.metaheuristics.StandardChemicalReaction;
import org.evosuite.ga.metaheuristics.StandardGA;
import org.evosuite.ga.metaheuristics.SteadyStateGA;
import org.evosuite.ga.metaheuristics.mosa.DynaMOSA;
import org.evosuite.ga.metaheuristics.mosa.MOSA;
import org.evosuite.ga.metaheuristics.mulambda.MuLambdaEA;
import org.evosuite.ga.metaheuristics.mulambda.MuPlusLambdaEA;
import org.evosuite.ga.metaheuristics.mulambda.OnePlusLambdaLambdaGA;
import org.evosuite.ga.metaheuristics.mulambda.OnePlusOneEA;
import org.evosuite.ga.operators.crossover.CrossOverFunction;
import org.evosuite.ga.operators.crossover.SinglePointCrossOver;
import org.evosuite.ga.operators.crossover.SinglePointFixedCrossOver;
import org.evosuite.ga.operators.crossover.SinglePointRelativeCrossOver;
import org.evosuite.ga.operators.crossover.UniformCrossOver;
import org.evosuite.ga.operators.ranking.FastNonDominatedSorting;
import org.evosuite.ga.operators.ranking.RankBasedPreferenceSorting;
import org.evosuite.ga.operators.ranking.RankingFunction;
import org.evosuite.ga.operators.selection.BestKSelection;
import org.evosuite.ga.operators.selection.BinaryTournamentSelectionCrowdedComparison;
import org.evosuite.ga.operators.selection.FitnessProportionateSelection;
import org.evosuite.ga.operators.selection.RandomKSelection;
import org.evosuite.ga.operators.selection.RankSelection;
import org.evosuite.ga.operators.selection.SelectionFunction;
import org.evosuite.ga.operators.selection.TournamentSelection;
import org.evosuite.ga.operators.selection.TournamentSelectionRankAndCrowdingDistanceComparator;
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
import org.evosuite.testsuite.secondaryobjectives.TestSuiteSecondaryObjective;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.ResourceController;

import de.upb.testify.efsm.EFSM;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.algorithm.operators.crossover.SinglePointPathCrossOver;
import eu.fbk.iv4xr.mbt.algorithm.operators.crossover.SinglePointRelativePathCrossOver;
import eu.fbk.iv4xr.mbt.coverage.CoverageGoalFactory;
import eu.fbk.iv4xr.mbt.coverage.KTransitionCoverageGoalFactory;
import eu.fbk.iv4xr.mbt.coverage.PathCoverageGoalFactory;
import eu.fbk.iv4xr.mbt.coverage.StateCoverageGoalFactory;
import eu.fbk.iv4xr.mbt.coverage.TransitionCoverageGoalFactory;
import eu.fbk.iv4xr.mbt.model.LabRecruitsEFSMFactory;
import eu.fbk.iv4xr.mbt.testcase.RandomLengthTestChromosomeFactory;
import sun.misc.Signal;

/**
 * @author kifetew
 *
 */
public class AlgorithmFactory<T extends Chromosome> extends PropertiesSearchAlgorithmFactory<T>{

	
	public static EFSM getModel () {
		LabRecruitsEFSMFactory efsmFactory = LabRecruitsEFSMFactory.getInstance();
		EFSM efsm = efsmFactory.getEFSM();
		return efsm;
	}
	
	
	protected CoverageGoalFactory<?> getFitnessFactory(){
		switch (MBTProperties.MODELCRITERION){
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
	
	protected ChromosomeFactory<T> getChromosomeFactory() {
		switch (MBTProperties.STRATEGY) {
		
		case GA:
		case DYNAMOSA:
			return new RandomLengthTestChromosomeFactory<T>(getModel());
		case MODEL_CHECKING:
			return new RandomLengthTestChromosomeFactory<T>(getModel());
		default:
			throw new RuntimeException("Unsupported test factory: "
					+ Properties.TEST_FACTORY);
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
				return new RandomSearch<>(factory);
			case NSGAII:
				logger.info("Chosen search algorithm: NSGAII");
				return new NSGAII<>(factory);
			case SPEA2:
				logger.info("Chosen search algorithm: SPEA2");
				return new SPEA2<>(factory);
			case MOSA:
				logger.info("Chosen search algorithm: MOSA");
				return new MOSA<>(factory);
			case DYNAMOSA:
				logger.info("Chosen search algorithm: DynaMOSA");
				return new DynaMOSA<>(factory);
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
			case LIPS:
				logger.info("Chosen search algorithm: LIPS");
				return new LIPS<>(factory);
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
		case RANK_CROWD_DISTANCE_TOURNAMENT:
		    return new TournamentSelectionRankAndCrowdingDistanceComparator<>();
		case BESTK:
			return new BestKSelection<>();
		case RANDOMK:
			return new RandomKSelection<>();
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
		default:
			throw new RuntimeException("Unknown crossover function: "
			        + Properties.CROSSOVER_FUNCTION);
		}
	}

	private RankingFunction<T> getRankingFunction() {
	  switch (Properties.RANKING_TYPE) {
	    case FAST_NON_DOMINATED_SORTING:
	      return new FastNonDominatedSorting<>();
	    case PREFERENCE_SORTING:
	    default:
	      return new RankBasedPreferenceSorting<>();
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

		RankingFunction<T> ranking_function = getRankingFunction();
		ga.setRankingFunction(ranking_function);

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

		if (ArrayUtil.contains(Properties.CRITERION, Criterion.MUTATION)
		        || ArrayUtil.contains(Properties.CRITERION, Criterion.STRONGMUTATION)) {
			if (Properties.STRATEGY == Strategy.ONEBRANCH)
				ga.addStoppingCondition(new MutationTimeoutStoppingCondition());
			else
				ga.addListener(new MutationTestPool());
			// } else if (Properties.CRITERION == Criterion.DEFUSE) {
			// if (Properties.STRATEGY == Strategy.EVOSUITE)
			// ga.addListener(new DefUseTestPool());
		}
		ga.resetStoppingConditions();
		ga.setPopulationLimit(getPopulationLimit());

		// How to cross over
		CrossOverFunction crossover_function = getCrossoverFunction();
		ga.setCrossOverFunction(crossover_function);

		// What to do about bloat
		// MaxLengthBloatControl bloat_control = new MaxLengthBloatControl();
		// ga.setBloatControl(bloat_control);

		if (Properties.CHECK_BEST_LENGTH) {
			RelativeSuiteLengthBloatControl bloat_control = new org.evosuite.testsuite.RelativeSuiteLengthBloatControl();
			ga.addBloatControl(bloat_control);
			ga.addListener(bloat_control);
		}
		// ga.addBloatControl(new MaxLengthBloatControl());

		TestSuiteSecondaryObjective.setSecondaryObjectives();

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

}
