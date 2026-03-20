# Payment Gateway Challenege

This is my submission for the Checkout.com Payment Gateway Challenge.

## Requirements

This project requires the following dependencies:
- JDK 17
- Docker

## Getting Started

In order to run the application, please follow these steps:

1. Clone the repository to your local machine.
2. Navigate over to src/main and run PaymentGatewayApplication.java.
3. In order to get responses from the bank simulator, the container containing the bank simulator must be running. Navigate to the root project directory and run docker-compose up.
4. Once the container is up and running, API calls can be made to http://localhost:8090, or the port number specified in application.properties. Postman is recommended to test the API calls to the application.


## Project Architecture

This is a simple payment gateway solution that I have built using Java Spring Boot. The objective was to meet all funtional requirements and to keep the modules open for extension for any future modification should that be required. Before explaining the architecture, the assumptions made for this project are noted below.

Assumptions Made

1. I assumed that a data structure to store payments is sufficient for this project. This means that payments are stored to the repository whilst the application is running and once the application is stopped, all payments stored are then lost.
2. Authentication and Authorization were not key requirements for this project, hence checks are not implemented for this and this would be a potential area for improvement.
3. If a payment fails validation, a response is returned with status "Rejected". I assumed that these attempted payments do not have to be stored.
4. Whilst initial tesing of the Bank Simulator, I noticed it can throw up a few errors. The challenge doesn't specify what to do with these so I've tried to surface these bank simulator errors back to the merchant to let me them know there is an issue with the bank.
5. A big assumption I made was around the error the merchant receives when a validation for a field occures, the decision was to either return one error message(either first or last) or a list of all error messages that occured during validation. I've opted for listing all error messages.

Areas of Improvement

1. Given more time, I would have liked to implement storing payments in the database, which we could then query.
2. There wasn't enough time and also there wasn't mention of authorization and authentication in the challenge, but it would have been nice to be able to implement this even in a simple way.
3. Idempotentcy - one of the biggest challenges of payment and ow latency systems is idempotency, although it is a big factor in payment systems, due to lack of time I was not able to implement this and I consider this a big area of improvement in the future.
4. Given time constraints, I have kept testing to what I felt was required mainly tests that should trigger failure from the system (sad path), with more time I would have liked to add more comprehensive tests to cover more scenarios.
5. I've tried to implement logging and all the calls log to the console to some level, but it would be an improvement if these logs could be persisted for future analysis.
6. For tests, whilst unit and integration testing has been covered, a payment system would need more robust testing such as load testing to see how the system would behave.

Project Design





## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**