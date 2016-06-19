# Dose Amigos Server Side Code

## Build instructions
Run `./gradlew clean build` to build and run tests.
`clean` will delete the build folder and `build` will create a build folder.
In `build/distributions`, a file called `server.zip` will be created that contains all dependency jars as well as compiled classes.
This is what is added to the lambdas

## Code Conventions
All classes will be contained in a package associated with the feature it belongs to.
For example `Med.java` would belong in the `info.doseamigos.med` package.

The code will be written in Java 8, so that it's compatible with Amazon AWS Lambdas.

We'll handle dependencies between classes using Google Guice: https://github.com/google/guice
* Each feature package will contain a module
* Each request handler will create an injector to use in its constructor.
  * Note that the injector should include everything required for that handler class.

The general structure should be as follows:
* All Echo specific Code belongs in a Speechlet class.
  * For example, `MedSpeechlet.java` would be a Speechlet for med adding/removing
  * Follow https://developer.amazon.com/appsandservices/solutions/alexa/alexa-skills-kit/getting-started-guide for conventions.
* All Rest endpoints should belong in a WebService class.
  * Can have multiple Handler methods for each http method
  * Follow http://docs.aws.amazon.com/lambda/latest/dg/java-programming-model-handler-types.html for conventions.
* All logic should be in a Service class.
  * For example, `MedService.java` is a service that holds methods that handle business and validation logic dealing with meds.
  * Services can depend on other services, but should only have 1 DAO dependency.
  * Note that Services should be interfaces with an implementation.
  * Services can also communicate with External APIS, such as rxnav for drug interaction information.
* All DB related code should be in a DAO class.
  * For example, `MedDao.java` is a dao class that holds methods that talks with the DB.
  * It's only dependency should be a Connection.
  * We're using JDBC prepared statements for ease of use.
  * Like with Services, this should have an interface with an implementation build for MySQL.
  
Another note, please write unit tests using TestNG: http://testng.org/doc/documentation-main.html
* All services should at least have tests for each method that meets 100% test coverage.
