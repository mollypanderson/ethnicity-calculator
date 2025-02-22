# Ethnicity Calculator

This is a simple tool that takes in a GEDCOM file (the standard file format for exchanging genealogical data) and traverses each line of your family tree until it finds an ancestor who was born somewhere other than the US/Canada. It compiles that data from each line and prints out a summary with each country found + percent of your ancestry associated with that country. Useful for people like me that have family trees so large that it would be extremely cumbersome to do this math by hand. 

I've tested it with a downloaded GEDCOM file from both MyHeritage and Ancestry. Those sites both export GEDCOM 5.5.5 files. It should work with the newer GEDCOMX format as well. 

<br />

<img width="1414" alt="Screenshot 2025-02-20 at 1 46 46 PM" src="https://github.com/user-attachments/assets/357da5e0-0f14-4523-83d9-455ae7e62856" />
<br />

### Getting Started
```
git clone git@github.com:mollypanderson/ethnicity-calculator.git
./gradlew build
./gradlew run path/to/your/gedcom/file
```

To export GEDCOM:

Ancestry
1. Open your tree
2. In the left-hand toolbar, click the 3 dots "More" -> Tree Settings
3. Right-hand side -> Manage Your Tree -> Export Tree
4. When it's done, click Download

To run locally:
1. Open project in IntelliJ
2. `./gradlew clean appRun`
3. Navigate to `http://localhost:8080/ethnicity-calculator/` in browser

------

# Vaadin Gradle Skeleton Starter Spring Boot

This project demos the possibility of having Vaadin project in npm+webpack mode using Gradle.
Please see the [Starting a Vaadin project using Gradle](https://vaadin.com/docs/latest/guide/start/gradle) for the documentation.


Prerequisites:
* Java 17 or higher
* Git
* (Optionally): Intellij Community
* (Optionally): Node.js and npm, if you have JavaScript/TypeScript customisations in your project.
  * You can either let the Vaadin Gradle plugin to install `Node.js` and `npm/pnpm` for you automatically, or you can install it to your OS:
  * Windows: [node.js Download site](https://nodejs.org/en/download/) - use the .msi 64-bit installer
  * Linux: `sudo apt install npm`

## Vaadin Versions

* The [v24](https://github.com/vaadin/base-starter-spring-gradle) branch (the default one) contains the example app for Vaadin latest version
* See other branches for other Vaadin versions.

## Running With Spring Boot via Gradle In Development Mode

Run the following command in this repo:

```bash
./gradlew clean bootRun
```

Now you can open the [http://localhost:8080](http://localhost:8080) with your browser.

## Running With Spring Boot from your IDE In Development Mode

Run the following command in this repo, to create necessary Vaadin config files:

```bash
./gradlew clean vaadinPrepareFrontend
```

The `build/vaadin-generated/` folder will now contain proper configuration files.

Open the `DemoApplication` class, and Run/Debug its main method from your IDE.

Now you can open the [http://localhost:8080](http://localhost:8080) with your browser.

## Building In Production Mode

Run the following command in this repo:

```bash
./gradlew clean build -Pvaadin.productionMode
```

That will build this app in production mode as a runnable jar archive; please find the jar file in `build/libs/base-starter-spring-gradle*.jar`.
You can run the JAR file with:

```bash
cd build/libs/
java -jar base-starter-spring-gradle*.jar
```

Now you can open the [http://localhost:8080](http://localhost:8080) with your browser.

### Building In Production On CI

Usually the CI images will not have node.js+npm available. Vaadin uses pre-compiled bundle when possible, i.e. Node.js is not always needed.
Or Vaadin Gradle Plugin will download Node.js for you automatically if it finds any front-end customisations, there is no need for you to do anything.
To build your app for production in CI, just run:

```bash
./gradlew clean build -Pvaadin.productionMode
```
