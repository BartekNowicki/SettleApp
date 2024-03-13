## ------------------------------------------------------------------- ##

#### API Documentation with Swagger OpenAPI

The application utilizes Swagger OpenAPI for API documentation. With Swagger OpenAPI, you can explore the
endpoints and request/response schemas.

You can access Swagger UI at the following endpoint:

http://localhost:8080/swagger-ui.html

## ------------------------------------------------------------------- ##

#### Functional Specifications

The IoTconfig Application is a follow-user-stories-MVP implementing the following stories:

Create Event, Cost, User

User Story: As a user, I would like to have the ability to create, edit and delete users, costs, events.
Input:
Output:

## ------------------------------------------------------------------- ##

#### Application Design

1.Initial Data Loading: events, costs, users

The application uses flyway to initialize the database with predefined entities.

3. Adding Data

   When adding a new cost, the application mandates that it be linked to an event and a user already present in the
   database. This requirement ensures that configurations are logically connected to specific
   devices, maintaining data coherence and integrity.

4. FE -> BE Contracts

   When adding a new entity, the backend expects the following payload:

   {

   }

   When modifying an existing configuration, the backend expects the following payload:

   {

   }

## ------------------------------------------------------------------- ##

#### Security considerations

The application implements Spring Security and JWT. The tokens are acuired at login and stored in local storage.

The MySql credentials used are "myuser" for user, "secret" for password and "verysecret" for root password. Thie is a
development only measure and the credentials need to be changed in further development and production.

## ------------------------------------------------------------------- ##

#### Technology stack

Spring Boot: Version 3.2.2
Java: Version 21
Apache Maven 3.9.6

Here's the list of dependencies along with their versions:

## ------------------------------------------------------------------- ##

#### Running the Application with Docker

This guide assumes that Docker is installed and running on your machine. If Docker is not installed, please visit
the [official Docker website](https://www.docker.com/get-started) for installation instructions.

## Steps to Run the Application

1. **Clone the Repository**

   First, clone the repository to your local machine using Git:

   git clone <repository-url>

   Replace `<repository-url>` with the actual URL of the Git repository.

2. Go to the root directory of the project where the `docker-compose.yml` file is located and execute these commands
   to

   -- build the application JAR file
   -- build the Docker image of the application
   -- build the Docker image of the database
   -- run both as Docker containers

   execute the following commands:

   to build the application JAR file execute:
   mvn clean install

   to build the Docker image of the application based on the JAR file, build the Docker image of the database, run both
   as Docker containers, execute:
   docker-compose up --build

You should see logs in the console indicating that the application and the mysql service are up and running. The Docker
image of the application will not build until it gets a health check confirmation from the database image, so there
might be a few seconds delay in the process - read the logs.

If the app fails to start execute:
docker-compose up --build --remove-orphans
(If you have any orphaned containers from previous runs that are still running, they might
interfere with the newly built containers and will get cleaned up.)

## Accessing the Application

Once the application is running, you can access it at `http://localhost:8080`

## Stopping the Application

To stop and remove the containers created by Docker Compose, use the following command:

1. Interrupt the process using ctrl+c, then execute:
   docker-compose down

This command stops all running services and removes the containers to clean up.


