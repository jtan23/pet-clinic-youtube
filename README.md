# Pet Clinic YouTube

A sample Spring Boot project inspired by the classic Pet Clinic application. This repository demonstrates building a simple pet clinic management system, as seen in YouTube tutorials, using Java, Spring Boot, and related technologies.

## Features

- Manage owners, pets, and visits
- Register new pet owners and their pets
- Schedule and track veterinary visits
- RESTful endpoints for core entities
- Simple web-based frontend

## Tech Stack

- **Backend:** Java, Spring Boot, Spring Data JPA, Spring MVC
- **Database:** H2 (in-memory for development), can be switched to MySQL/PostgreSQL
- **Frontend:** Thymeleaf
- **Build:** Maven/Gradle

## Getting Started

### Prerequisites

- Java 17 or newer
- Maven (or Gradle)

### Learning path

Start from Intro, PetClinic, Install JDK, then work your way through all the steps.

### Running the Application

1. **Clone the repo:**
   ```sh
   git clone https://github.com/jtan23/pet-clinic-youtube.git
   cd pet-clinic-youtube
   ```

2. **Build and run:**
   ```sh
   ./mvnw spring-boot:run
   ```
   Or, if using Gradle:
   ```sh
   ./gradlew bootRun
   ```

3. **Visit in your browser:**
   ```
   http://localhost:8080/
   ```

### Database

By default, the app uses an H2 in-memory database. You can access the H2 console at [http://localhost:8080/h2-console](http://localhost:8080/h2-console).

To use another database, update the `application.properties` file.

## Project Structure

- `src/main/java/...` - Java source code (controllers, services, models, repositories)
- `src/main/resources/templates/` - Thymeleaf HTML templates
- `src/main/resources/application.properties` - Application configuration

## Example Endpoints

- `/owners` - List all owners
- `/pets` - List all pets
- `/visits` - List all visits

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License.

## Acknowledgements

- Based on the original Spring Pet Clinic sample
- Inspired by YouTube tutorials on Spring Boot

```
```
