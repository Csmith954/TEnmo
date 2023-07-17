# TEnmo

**Project Description:** TEnmo is a command-line program based on the popular payment platform Venmo. It allows users to register, login, search for other users, and send/receive money. Please note that there is no front-end interface for this project, so all functions can be run using Postman. The project was built using Java, PostgreSQL, and Postman.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Endpoints](#endpoints)
- [Technologies Used](#technologies-used)

## Installation

To run TEnmo locally, please follow these steps:

1. Clone the repository to your local machine:
   ```
   git clone https://github.com/your-username/TEnmo.git
   ```

2. Navigate to the project directory:
   ```
   cd TEnmo
   ```

3. Set up the PostgreSQL database:
   - Create a new database in PostgreSQL.
   - Update the `application.properties` file in the project with the appropriate database connection details.

4. Compile the Java source code:
   ```
   mvn clean package
   ```

5. Start the server:
   ```
   java -jar target/tenmo-server.jar
   ```

## Usage

To use TEnmo, follow these steps:

1. Set up the server and ensure it is running.

2. Use Postman to interact with the available endpoints. The following endpoints are available:

## Endpoints

- Register a new user:
  - **Endpoint:** `POST /register`
  - **Description:** Registers a new user.
  - **Body:** 
    ```json
    {
      "username": "example_user",
      "password": "example_password"
    }
    ```

- Login:
  - **Endpoint:** `POST /login`
  - **Description:** Logs in a user and returns a JWT token.
  - **Body:** 
    ```json
    {
      "username": "example_user",
      "password": "example_password"
    }
    ```

- Retrieve a list of all users:
  - **Endpoint:** `GET /users`
  - **Description:** Retrieves a list of all users.

- Retrieve a specific user by ID:
  - **Endpoint:** `GET /users/{id}`
  - **Description:** Retrieves a specific user by ID.

- Retrieve the user's account details:
  - **Endpoint:** `GET /accounts`
  - **Description:** Retrieves the user's account details.

- Retrieve the user's transfers (sent/received):
  - **Endpoint:** `GET /transfers`
  - **Description:** Retrieves the user's transfers.

- Send money to another user:
  - **Endpoint:** `POST /transfers/send`
  - **Description:** Sends money to another user.
  - **Body:** 
    ```json
    {
      "amount": 50.0,
      "recipientId": 2
    }
    ```

- Retrieve a transaction by ID:
  - **Endpoint:** `GET /transactions/{transactionId}`
  - **Description:** Retrieves a transaction by ID.

- Retrieve a list of transactions by user:
  - **Endpoint:** `GET /transactions/list`
  - **Description:** Retrieves a list of transactions by user.

- Send money:
  - **Endpoint:** `POST /transactions`
  - **Description:** Sends money from one user to another.
  - **Body:** 
    ```json
    {
      "user1Id": 1,
      "user2Id": 2,
      "transferAmount": 50.0
    }
    ```

- Request money:
  - **Endpoint:** `POST /transactions/request`
  - **Description:** Requests money from another user.
  - **Body:** 
    ```json
    {
      "user1Id": 1,
      "user2Id": 2,
      "transferAmount": 50.0
    }
    ```

- Respond to a request:
  - **Endpoint:** `POST /transactions/respond`
  - **Description:** Responds to a money request.
  - **Query Parameters:**
    - `acceptOrReject` (boolean): Specifies whether to accept or reject the request.
    - `transactionId` (int): ID of the transaction.
  
- Add an account to a user:
  - **Endpoint:** `POST /account/addaccount`
  - **Description:** Adds an account to a user.

- Get account details by user:
  - **Endpoint:** `GET /account`
  - **Description:** Retrieves the account details of a user.

- Get a list of all accounts by user:
  - **Endpoint:** `GET /account/list`
  - **Description:** Retrieves a list of all accounts by user.

Use Postman to make requests to the desired endpoints by providing the necessary parameters.

## Technologies Used

- Java: Programming language used to build the TEnmo program.
- PostgreSQL: Relational database management system used to store user and transaction data.
- Postman: API development and testing tool used to interact with the TEnmo endpoints.


## Disclaimer

Please note that TEnmo is a command-line program and does not have a graphical user interface. All interactions are performed using Postman. This project is not affiliated with Venmo and is intended for educational purposes only.
