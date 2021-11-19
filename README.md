# iv4xr-mbt

The tool mbt requires at least Java 11. To see the online help.

> java -jar mbt-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Tool mbt has three running modes: test generation, execute on sut, and mutation analysis.

# Building

MBT is a Java library that can be built and installed with the Maven tool, in the usual maven-way. The dependency on Evosuite 1.0.6 might be a problem, if the latter fails to build. In that case, a known workaround is to use Evosuite jar-file directly. This can be found in Evosuite Github site. Use the right version as specified in MBT's Maven dependency, and also comment-out that dependency.

# Test generation

Test generation performs the following two steps:
- creates a random EFSM that can be translated into a csv for Lab Recruits  
- performs search based test suite generation for the random EFSM just created
Test generation is triggered with the following command:

> java -jar mbt-0.0.1-SNAPSHOT-jar-with-dependencies.jar -sbt -Dalgorithm=$algorithm -Dsearch_budget=$budget -Dsut_efsm=$sut -Drandom_seed=$seed -Dpopulation=$population -Dmodelcriterion=$criterion -Dshow_progress=$progress

where:
- $algorithm specifies the search algorithm. Valid values are mosa, monotonic_ga,
  steady_state_ga, spea2, and nsgaii, default=mosa;
- $budget is the budget in seconds the algorithm has to generate a test suite, default=60;
- $sut represents some predefined EFSM configuration. Valid values are
  labrecruits.random_simple, labrecruits.random_medium, labrecruits.random_large,
  and labrecruits.buttons_doors_1, default=labrecruits.random_simple. We detail below the configurations;
- $seed is the random seed used by the search algorithm, default=current_time_milliseconds;
- $population is the initial population of the search algorithm, default=50;
- $criterion is the coverage criterion. Valid values are state and transition, default=transition;
- $progress switches on or off the progress bar during the search phase, default=true

# Sut configuration
The random EFSM generator has five parameters that can be changed to define the structure of the model:
- LR_mean_buttons: mean number of buttons in a room
- LR_n_buttons: total number of buttons in the level
- LR_n_doors: number of doors in the level
- LR_seed: random seed for level generation
- LR_n_goalFlags: number of goal flags in the level

Predefined $sut corresponds to the following parameters:
- labrecruits.random_simple: LR_mean_buttons = 0.5, LR_n_buttons = 5,
  LR_n_doors = 4, LR_seed = 325439;
- labrecruits.random_medium: LR_mean_buttons = 0.5, LR_n_buttons = 10,
  LR_n_doors = 8, LR_seed = 325439;
- labrecruits.random_large: LR_mean_buttons = 0.5, LR_n_buttons = 20,
  LR_n_doors = 15, LR_seed = 325439;

Value labrecruits.buttons_doors_1 for $sut corresponds to Lab Recruits predefined
 level buttonDoors1.csv.

To generate a random EFSM with different parameters it suffices to use
labrecruits.random_default as $sut and to pass the parameters to the program.
For instance, a level with 2 buttons per room, 6 buttons, 3 doors, 1 goal flag and seed 555 is
generate with (then runs the test generation on it using default values, see above)

> java -jar mbt-0.0.1-SNAPSHOT-jar-with-dependencies.jar -sbt -Dsut_efsm=labrecruits.random_default -DLR_mean_buttons=2 -DLR_n_buttons=6 -DLR_n_doors=3 -DLR_n_goalFlags=1 -DLR_seed=555

### Test generation output
The output of the tool is saved in folder mbt-files. Subfolder 'statistics' reports
statistics about the test generation. Subfolder 'tests' contains the EFSM and the
tests generated. In particular, the tool creates folder $sut/$algorithm accordingly
with the parameters passed to the tool. A subfolder with a random name is
generated  and contains the tests and the model.


# Demo on the Lab Recruits

To run the tools it is needed to download Lab Recruits application (it is a game) from
https://github.com/iv4xr-project/labrecruits


### Run on sut
To run generated test suites on the Lab Recruits application, the game should be
in a reachable folder, say LR_FOLDER.
The mbt mode exec_on_sut allows running the test suites generated on an EFMS model
on the Lab Recruits game. To run the execution mode type

> java -jar mbt-0.0.1-SNAPSHOT-jar-with-dependencies.jar -exec_on_sut -sut_exec_dir=$SUT_EXEC_DIR -sut_executable=$SUT_EXECUTABLE -tests_dir=$test -agent_name=$agent -max_cycles=$max_cycle

where:
- $SUT_EXEC_DIR is the folder that stores Lab Recruits executable. The tool searches
  for a folder 'gym'. Within 'gym' there should be  a folder named with the OS (Linux,
  Mac, or Windows). Finally, the tool expects the executable in a folder named 'bin'.
  For instance, on an OSX machine, given $SUT_EXEC_DIR=FOLDER, the tool searches in
  FOLDER/gym/Mac/bin/ for Lab Recruits executable;
- $SUT_EXECUTABLE is the path to the csv file representing the generated Lab
  Recruit level. The extension .csv must be omitted
- $test the folder that stores the tests given as output in the generation phase
- $agent: the name for the agent (Agent1 for random generated and agent1 for
  buttonDoors1 level)
- $max_cycle: the maximum number of cycles the agent could take to perform and
  action

### Output of run on sut
In folder mbt-files/statistics/ the tool saves
- execution_statistics.csv: reports the number of tests performed, the number of tests
  failed and the time required
- execution_debug.csv: debug information about the evaluation of each transition
  of each test case

### Run mutation analysis
Mutation analysis takes the csv of a level and the tests generated in the test
generation phase, mutates it and runs exec_on_sut on each mutated level. The
parameters are the same of run on sut mode:

> java -jar mbt-0.0.1-SNAPSHOT-jar-with-dependencies.jar -mutation_analysis -sut_exec_dir=$SUT_EXEC_DIR -sut_executable=$SUT_EXECUTABLE -tests_dir=$test -agent_name=$agent -max_cycles=$max_cycle -Dmax_mutations=MAX_MUTATION

with -mutation_analysis in place of -exec_on_sut and MAX_MUTATION being the maximum
number of mutated levels to run with Lab Recruits.

### Output of run un mutation analysis
File mbt-file/statistics/mutation_statistics.csv reports the number of killed
mutations and the number of tests performed on each mutation.
Folder mbt-files/mutations/ contains a folder for each row of mutation_statistics.csv.
Field run_id corresponds to the name of the folder. For each run, the tool saves the
mutated levels, the execution data of each run. Subfolder 'tests' contains the
executed tests.
