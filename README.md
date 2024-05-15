# Phishingalert.cz - new iteration

## Project structure
- The app consists of two independent parts: `core` and `scraper`.
They both run separately and communicate with each other
through RabbitMQ protocol.
- The both parts utilise some common code which is stored in the `common` project.

## Requirements
- Java 17 SDK or newer (tested with `openjdk version "19" 2022-09-20`)
- Gradle
- PostgreSQL and RabbitMQ broker (can be installed with attached 
`docker compose` file)

## How to start the app?
- Start PostgreSQL database and RabbitMQ broker
- Start the Core and the Scraper (in any order)

### Run locally for demo/testing (Linux)
- [In the root directory] Start database and broker in Docker by running the command `docker compose up`
- Set `JAVA_PATH` variable to your Java SDK install location (for example `JAVA_PATH=/home/jirik/.jdks/openjdk-19`)
- [In the `core` directory] Start the Core with 
`./gradlew -Dorg.gradle.java.home=JAVA_PATH bootRun --args='--spring.config.additional-location=file:./core-config.yml'`
- [In the `scraper` directory] Start the Scraper with
`./gradlew -Dorg.gradle.java.home=JAVA_PATH bootRun --args='--spring.config.additional-location=file:./scraper-config.yml'`
- _FYI: You can run the both parts easily on any OS by opening them in IntelliJ IDEA and
starting them from the IDE which will take care of the setup_
- Open `localhost:8080` to access web form or `localhost:8080/admin` to access
    administration page in your favourite browser

### Credits
- All files in `./core/src/main/resources/static` directory were taken from the original [phishingalert.cz project](https://github.com/sec4good/phishingalert_cz) which was made by Marek Sušický (2023). Look at the `./core/src/main/resources/static/LICENSE` file for more information about the licensing.
- The `./core/controllers/admin/reporting/WebRiskController.kt` file contains some code from Google LLC (2020). See its file header for more information about the licensing.