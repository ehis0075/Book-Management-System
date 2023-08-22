

# Book Management System
This is a Spring Boot application Backend API Service for a Book Management System.

# Requirements
-Java 11 or higher
-Maven build tool

# Getting Started
git clone https://github.com/ehis0075/Book-Management-System

# Navigate to the project directory:
cd bms

# Build the project using Maven
mvn clean package

# Run the Application Locally
java -jar target/book-mgt-service.jar

The API service will be accessible at `http://localhost:8080/api/v1`

# Dockerize the Application
You can build a Docker image for the application using the provided Dockerfile
docker build -t book-mgt-service-docker:latest .

# Running the Application with Docker
Run the Docker container using the following command:

docker run -p 8080:8080 book-mgt-service-docker:latest

The API service will be available at http://localhost:8080/api/v1




                                # AUTHOR Endpoints
## Create Author API

**Endpoint:** `/api/v1/authors/create`
**Method:** `POST`

This API create a new author, it requires providing a `name`, and `email` in the request body.


## Update Author API

**Endpoint:** `/api/v1/authors/update/{authorId}`
**Method:** `POST`

This API update an already exited author, it requires providing a `name`, and `email` in the request body, and the authorId as the path variable


## Delete Author API

**Endpoint:** `/api/v1/authors/delete/{authorId}`
**Method:** `POST`

This API delete author, it requires providing a `authorId` as the path variable


## Get all Author API List

**Endpoint:** `/api/v1/authors`
**Method:** `POST`

This API get all authors from the db, it requires providing a `page` and  `size`.





                                # BOOK Endpoints
## Create Book API

**Endpoint:** `/api/v1/books/create`
**Method:** `POST`

This API create a new author, it requires providing a `title`, `authorId`, and `publicationYear` in the request body.


## Update Book API

**Endpoint:** `/api/v1/books/update/{bookId}`
**Method:** `POST`

This API update an already exited book, it requires providing a `title`, `authorId`, and `publicationYear` in the request body and `bookId` as the path variable.


## Delete Book API

**Endpoint:** `/api/v1/books/delete/{bookId}`
**Method:** `POST`

This API delete book from the database, it requires providing a `bookId` as the path variable


## Get all Book API List

**Endpoint:** `/api/v1/books`
**Method:** `POST`

This API get all books from the db, it requires providing a `page` and  `size`.


## Note 

**For pagination: the first page is page 0 and the size is between 1-100, if you want the 101 item on the list, go to page 1.
