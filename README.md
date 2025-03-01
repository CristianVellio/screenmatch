# 📽️ Screenmatch

Screenmatch is a Spring Boot-based Java application designed as an introductory project for working with the framework. It leverages several key technologies, including PostgreSQL for database management and OpenAI integration for additional functionalities.

## Features
- RESTful API using Spring Boot
- PostgreSQL database integration with Spring Data JPA
- Environment variable management using `java-dotenv`
- JSON processing with Jackson
- Development tools enabled via `spring-boot-devtools`

## Requirements
- Java 21
- Maven
- PostgreSQL database

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/your-username/screenmatch.git
   cd screenmatch
   ```
2. Configure environment variables:
   - Create a `.env` file in the project's root and specify required environment variables.

3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## Dependencies
The project uses the following dependencies:
- `spring-boot-starter` - Core Spring Boot features
- `spring-boot-starter-web` - Enables REST API functionalities
- `spring-boot-devtools` - Hot reload for development
- `java-dotenv` - Environment variable management
- `jackson-databind` - JSON serialization and deserialization
- `spring-boot-starter-data-jpa` - Database access using JPA
- `postgresql` - PostgreSQL database driver
- `openai-gpt3-java` - OpenAI API integration

## Running Tests
To run tests, execute:
```sh
mvn test
```

## API Endpoints
(Provide specific API routes and their functionalities here)

## License
This project is licensed under the MIT License.

## Author
[Cristian Vellio](https://github.com/your-username)

