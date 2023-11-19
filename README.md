# Engineering Home Task
In finance, it's common for accounts to have so-called "velocity limits". In this task, you'll create a Java Spring boot application that accepts or declines attempts to load funds into customers' accounts in real-time..

## Table of Contents
- [Introduction](#introduction)
- [How to Run](#how-to-run)
- [How to Test](#how-to-test)

## Introduction
This project is designed to handle fund loading operations, validating funds based on various constraints, and providing responses indicating the acceptance or rejection of fund loading operations. The main components of the project include the FundService, FundController, and the main application class HomeTaskApplication.

**Key Features:**
Validates funds based on various velocity limits.
- A maximum of $5,000 can be loaded per day.
- A maximum of $20,000 can be loaded per week.
- A maximum of 3 loads can be performed per day

## How to Run
There are two ways to run the application:

### 1. Command Line (Provide File Path)
To run the application and process fund loading operations based on an input file, provide the path to the input file as a command-line argument.

- step 1
```maven
./mvnw clean install
```
- step 2
```bash
java -jar target/task-0.0.1-SNAPSHOT.jar /path/to/input/file.txt
```

### 2. REST API Endpoint
If you prefer to interact with the application through REST API, you can use an HTTP client to call the endpoint, however you should not provide any path to the input file when you start the service.

- step 1
```maven
./mvnw clean install
```
- step 2
```bash
java -jar target/task-0.0.1-SNAPSHOT.jar
```
- step 3
```http
curl --location 'localhost:8080/api/funds/load' \
--header 'Content-Type: multipart/form-data' \
--form 'file=@"/path/to/input/file/file.txt"'
```


## How To Test
The `HomeTaskApplicationTests` class includes an integration test named `integrationTest` that validates the fund loading operations based on provided input and expected output files. The test ensures that the application's responses match the expected results.

To run the tests, execute the following command:
```maven
./mvnw -Dtest=HomeTaskApplicationTests#integrationTest test
```