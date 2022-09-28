# __EvoMBT__

__EvoMBT__ combines model-based testing (MBT) with search algorithms for the generation of test cases for systems with complex and fine grained interactions represented as an Extended Finite State Machine (EFSM). __EvoMBT__ 
natively supports full MBT cycle by using the [Lab Recruits](https://github.com/iv4xr-project/labrecruits) 3D game as a use case [[1]](#1).

The tool __EvoMBT__ requires at least Java 11. To see the online help.

> java -jar EvoMBT.jar


#### Building

MBT is a Java library that can be built and installed with the Maven tool. 

> mvn clean package

This will also run tests that would require few minutes. To skip tests run 

> mvn clean package -DskipTests

#### EvoSuite dependency

EvoMBT uses  the algorithms implemented in EvoSuite and depends on version  1.0.6. 
Evosuite 1.0.6 dependency might be a problem, if the latter fails to build. In that case, a known workaround is to use Evosuite jar-file directly. This can be found in Evosuite Github site. Use the right version as specified in MBT's Maven dependency, and also comment-out that dependency.

#### First run

To have a flavor of EvoMBT run the following command

> java -jar EvoMBT.jar -sbt


#### Manual

Detailed list of EvoMBT command line parameters; EvoMBT as a Java library; definition of custom EFSM: see the [wiki](https://github.com/iv4xr-project/iv4xr-mbt/wiki).


#### References 
<a id="1">[1]</a> 
R. Ferdous, F. M. Kifetew, D. Prandi, I. S. W. B. Prasetya, S. Shirzadehhajimahmood, A. Susi.
*Search-based automated play testing of computer games: A model-based approach.*
13th International Symposium, SSBSE 2021. 
[doi:10.1007/978-3-030-88106-1_5](https://link.springer.com/chapter/10.1007/978-3-030-88106-1_5). 

<a id="2">[2]</a> 
R. Ferdous, C. Hung, F. M. Kifetew, D. Prandi, A. Susi.
*EvoMBT*.
15th IEEE/ACM International Workshop on Search-Based Software Testing (tool competition), SBST@ICSE 2022.
[doi:10.1145/3526072.3527534](https://ieeexplore.ieee.org/document/9810734).


