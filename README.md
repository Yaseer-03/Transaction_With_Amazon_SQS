# Transaction_With_Amazon_SQS


# Transaction Management System

## Overview
This project is a **Transaction Management System** developed using **Java**, **SQL**, **Hibernate**, and **Amazon SQS** as a cloud service. The system is designed to handle transactions efficiently by utilizing cloud-based queuing for seamless communication and transaction processing.

## Features
- **Amazon SQS Integration**: Transactions are sent to an SQS queue, ensuring reliable message queuing and processing.
- **Transaction Listener**: Listens for incoming transactions from the SQS queue and processes them in real-time.
- **Validation**: Each transaction is validated against specific business rules before being processed.
- **Database Management**: Used **Hibernate** ORM for efficient interaction with the **MySQL** database.
- **Merchant Processing**: Validated requests are forwarded to the merchant to make the transaction successful.

## Technologies Used
- **Programming Language**: Java
- **Database**: MySQL
- **ORM**: Hibernate
- **Cloud Service**: Amazon SQS
- **Frameworks**: Spring Boot
- **Tools**: IntelliJ IDEA
- **Version Control**: Git, GitHub

## How It Works
1. **Queue a Transaction**: A transaction request is sent to the **Amazon SQS** queue.
2. **Listening to Queue**: The system continuously listens for new transaction requests from the SQS queue.
3. **Validation**: Once a transaction is received, it is validated against a set of predefined rules to ensure its correctness.
4. **Processing**: After validation, the transaction is processed with the merchant.
5. **Database Management**: **Hibernate** is used for performing CRUD operations on the **MySQL** database.
