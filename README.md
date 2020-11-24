# Traffirator - PCRF Traffic Generator

[![Build Status](https://github.com/Neloop/traffirator/workflows/CI/badge.svg)](https://github.com/Neloop/traffirator/actions)
[![License](http://img.shields.io/:license-mit-blue.svg)](https://github.com/Neloop/traffirator/blob/master/LICENSE)

**Traffirator** is a traffic generator for the **PCRF** server placed within the LTE network. Traffirator generates traffic for the **Gx** and **Rx** interfaces of the PCRF server. It simulates user equipments connected to the LTE network, which might perform calls and usual maintenance of the user sessions.

The generator is built on a concept of test profiles and scenarios. For each execution, there has to be defined a test profile. Test profiles are sequences of points in time, for each time, the count of active scenarios, which should be kept up, has to be specified. The scenario represents a single user device and it is, in fact, an automaton. The nodes of the automaton can define a sequence of steps which will be done when the node is processed. The steps are only of two types - receive a message and send a message.

Unlike test profiles, scenarios cannot be defined dynamically and are hard-coded in traffirator. In the definition of the test profile, we can only specify the type of the scenario, which will be used. For the time being there are only 5 types of scenarios - _Call Center Employee_, _Call Performance Scenario_, _Classic User_, _Malfunctioning Cell-phone_, _Travelling Manager_.

The project is released under the **MIT** license and uses the **jDiameter** library for the communication over the Gx and Rx interfaces.

The **traffirator** generator is part of the master thesis written and defended on **[MFF](https://www.mff.cuni.cz/)** (Faculty of Mathematics and Physics, Charles University), the thesis with attachments is publicly accessible and can be found at [https://is.cuni.cz/webapps/zzp/detail/192875](https://is.cuni.cz/webapps/zzp/detail/192875).

## Installation and Compilation

For the time being, traffirator is distributed only in the form of the GitHub repository. If we want to use traffirator we have to clone the repository. By the following command, we clone the repository to the current directory.

```
> git clone https://github.com/Neloop/traffirator.git
```

After downloading the sources of traffirator we can build the whole project with **maven**. Maven has to be executed in the root directory of the project. During the first execution, maven will download all dependencies of the project, mainly including jDiameter and its dependencies.

```
> mvn clean package
```

The previous command will compile Java sources into class files and package the whole solution into jar file placed in `target` directory. The resulting jar file does not contain the dependencies of the project, these are placed in the maven repository folder. To get the dependencies closer to the project, we can run following command, which will copy all the project dependencies in the `target/dependency` directory.

```
> mvn dependency:copy-dependencies
```

Copying the dependencies is optional and should be used only if the project jar file will not be executed using maven.

## Configuration

The traffirator project contains three configuration methods. There is a **YAML** configuration of traffirator itself, a configuration of the logging framework and a configuration of the jDiameter stacks. All three has to be provided upon execution.

### Traffirator Configuration

The configuration of the generator is defined in the YAML configuration format. The YAML file has to be provided upon execution as a command line argument. The configuration configures some general attributes of the execution and also the test profile, which will be used. The items of the configuration are as follows:

* **description** - Textual description of this configuration and the test profile defined in this file.
* **threadCount** - Number of threads, which will be created in the common executor service.
* **summary** - Contains path, where the summary log file should be placed after execution.
* **statistics** - Defines the options for statistics outputs from the traffirator.
    * **logFile** - The path, where the statistics file will be placed.
    * **samplingPeriod** - Sampling period in milliseconds, which will be used as period between the collections of information from the generator.
* **profile** - Definition of the profile, which will be executed.
    * **burstLimit** - Burst limit corresponds to the maximum number of new scenarios that can be created in one second.
    * **end** - Defines the point in time, when the execution should end, it is defined in seconds.
    * **flow** - Contains the list of the profile entries.
        * **start** - The time, when this profile item begins and should be applied, it is defined in seconds.
        * **scenarios** - Contains the list of scenarios used in this profile item.
            * **type** - The type of scenarios to which the amount in the item `count` is related.
            * **count** - The number of scenarios with particular type, which will be spawned in the time defined in `start` item. The count is defined in absolute numbers and not incremental numbers.

The example configuration, which can be taken as a reference, is placed in the `examples/example-config.yml` file.

For the definition of the scenarios, following types can be used:

* **CallCenterEmployee** - Represents a user device, which is used by the call center employee. The device is making a lot of calls with a very small delays between the calls.
* **CallPerformance** - Scenario used for intense testing of the PCRF server, all the delays are small and it is focused on making a single call and then end.
* **ClassicUser** - Used for real-life testing, it represents classical user of the telecommunication network, who is making some normal amount of calls and have a high delays between operations.
* **MalfunctioningCellPhone** - The device, which is behaving in an unexpected way, it is frequently disconnecting and connecting and also losing a signal. The probability of making a call is very low.
* **TravellingManager** - The travelling manager scenario is a lot like call center employee, manager makes a lot of calls, but with a difference of often changes of the location. This is done by updating the session information, which are highly probable in this scenario.

### Log4j Configuration

The configuration of the log4j logging framework is defined in the resources directory of the project (`src/main/resources/log4j.properties`) and can be modified before the execution, to meet the current needs. The description of the configuration items can be found in the appropriate project documentation of log4j.

### jDiameter Configuration

Traffirator contains two Diameter stacks - Gx and Rx. Both of them are used from the jDiameter library, which has its own configuration of these stacks. The configuration for a stack is in **XML**, in which the peer to which the stack will connect is defined. In addition, there are also some attributes of the connection, such as the Diameter application used during the capabilities exchange. There are also defined some internals, such as timeouts for default Diameter messages or configuration of threading.

Both of the configuration files must be placed in the resources directory. The Gx stack should be configured in the `src/main/resources/gx-client-config.xml` and the Rx stack in the `src/main/resources/rx-client-config.xml` file. Both of the files should be revised before the execution to apply correct domain names and IP addresses of the PCRF server.

The definition of the format can be found in the documentation of the jDiameter. It is defined as AsciiDoc in the directory [https://github.com/RestComm/jdiameter/tree/master/core/docs/sources-asciidoc/src/main/asciidoc](https://github.com/RestComm/jdiameter/tree/master/core/docs/sources-asciidoc/src/main/asciidoc). The whole documentation is also accessible as a web-page on the address [https://www.restcomm.com/docs/core/diameter/Diameter_User_Guide.html#_jdiameter_configuration](https://www.restcomm.com/docs/core/diameter/Diameter_User_Guide.html#_jdiameter_configuration).

## Execution

Running traffirator is rather easy, we just have to know the path to the configuration file, which we want to use. Maven project in traffirator is setup with the support of the maven exec plugin, so the execution can be started by executing the following command.

```
> mvn exec:java -q -Dexec.args="--config=./examples/example-config.yml"
```

The argument `-Dexec.args` is used to specify the command line arguments for the execution of the project. There are only two arguments, which are defined in the traffirator application: `help` and `config`. If the `help` option is given, traffirator will display its help message with usage and description of all options. The `config` option is used to hand over the path for the YAML configuration to the application. The given configuration file is loaded and defined test profile executed.

## Outputs

For the time being, traffirator supports only one output file, if we omit the classical log. The output is statistics log, which contains some interesting or useful statistics from the whole execution. The statistics will be stored in the path which was given in the configuration. The log contains named columns with the corresponding values in the rows, columns are separated by the tabulator character.

The statistics log has following columns:

* **Time** - This column contains the current timestamp when the row was sampled, in milliseconds.
* **ScenariosCount** - The scenarios count column contains the number of currently active scenarios.
* **TimeoutsCount** - The column containing the number of timeouts since the last sampled point in time.
* **SentCount** - Contains the number of sent messages, which was sent since the previous sampling.
* **ReceivedCount** - Relative number of received message since the last sampling.
* **FailuresCount** - The column contains the number of failures, which was observed since the last sampling time.
* **ProcessLoad** - The process load column contains the current load of the traffirator process.