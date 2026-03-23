# Payment Gateway Challenege

This is my submission for the Checkout.com Payment Gateway Challenge.

## Requirements

This project requires the following dependencies:
- JDK 17
- Docker

## Getting Started

In order to run the application, please follow these steps:

1. Clone the repository to your local machine.
2. Navigate over to `src/main/java/com.checkout.payment.gateway/PaymentGatewayApplication` and run `PaymentGatewayApplication.java`.
3. Alternatively run `./gradlew build` and `./gradlew bootRun` to start the application.
4. In order to get responses from the bank simulator, the container containing the bank simulator must be running. Navigate to the root project directory and run docker-compose up.
5. Once the container is up and running, API calls can be made to `http://localhost:8090`, or the port number specified in application.properties. Postman is recommended to test the API calls to the application.
6. Tests can be run using the following command `./gradlew test`
7. A test payment can be made using the following curl command (ensure the simulator is running first) :
```curl -X POST http://localhost:8090/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "card_number": "12345678909876",
    "expiry_month": 12,
    "expiry_year": 2027,
    "currency": "GBP",
    "amount": 100,
    "cvv": "123"
  }'
```
Note: After running the application, API documentation is available at: `http://localhost:8090/swagger-ui/index.html` 



## Project Architecture

This is a simple payment gateway solution that I have built using Java Spring Boot. The objective was to meet all funtional requirements and to keep the modules open for extension for any future modification should that be required. Before explaining the architecture, the assumptions made for this project are noted below.

**Assumptions Made**

1. I assumed that a data structure to store payments is sufficient for this project. This means that payments are stored to the repository whilst the application is running and once the application is stopped, all payments stored are then lost.
2. Authentication and Authorization were not key requirements for this project, hence checks are not implemented for this and this would be a potential area for improvement.
3. If a payment fails validation, a response is returned with status "Rejected". I assumed that these attempted payments do not have to be stored.
4. Whilst initial tesing of the Bank Simulator, I noticed it can throw up a few errors. The challenge doesn't specify what to do with these so I've tried to surface these bank simulator errors back to the merchant to let me them know there is an issue with the bank.
5. A big assumption I made was around the error the merchant receives when a validation for a field occures, the decision was to either return one error message(either first or last) or a list of all error messages that occured during validation. I've opted for listing all error messages.
6. If there is an issue with the bank simulator, then the payment gateway returns a 503 error to the merchant.

**Areas of Improvement**

1. Given more time, I would have liked to implement storing payments in the database, which we could then query.
2. There wasn't enough time and also there wasn't mention of authorization and authentication in the challenge, but it would have been nice to be able to implement this even in a simple way.
3. Idempotentcy - one of the biggest challenges of payment and ow latency systems is idempotency, although it is a big factor in payment systems, due to lack of time I was not able to implement this and I consider this a big area of improvement in the future.
4. Given time constraints, I have kept testing to what I felt was required mainly tests that should trigger failure from the system (sad path), with more time I would have liked to add more comprehensive tests to cover more scenarios.
5. I've tried to implement logging and all the calls log to the console to some level, but it would be an improvement if these logs could be persisted for future analysis.
6. For tests, whilst unit and integration testing has been covered, a payment system would need more robust testing such as load testing to see how the system would behave.

**Project Design**

I have designed the solution in a way so that it follows SOLID principles and it is easy for extension in the future. For example, classes follow the Single Responsiblity pricinple, like separating the validation into a separate class rather than keep in the same class.

The design follows a typical Spring Boot Application where there are layers for each component of the project. The layers are as follows:

**Controller** - Entry point of the API call and deals with HTTP requests and respones.

**Service** - Business logic is handled here such as processing the request and forwarding the request to the Bank Simulator and returning a response back to the Controller.

**Repository** - Responsible for storing the payments that are coming into the system.

**Validation** - I would say this layer was optional, as we could have done the validation within the model class or within service but using this layer means that the responsibility of validation sits with a single class and doesn't give additional responsiblity to the model or service classes.

A High Level FLow of the Architecture looks like this:

```
Merchant
|
| POST /api/v1/payments
| GET /api/v1/payments/{id}
v
PaymentGatewayController
|
| --- PaymentRequestValidator - validate the fields of the incoming payment request
v
PaymentGatewayService
|
| --- callBank() - calls the bank to process the payment and return whether its authorised or not.
v
PaymentsRepository
|
| --- store the payments
v
Response returned to the merchant
```

**Some Key Design Decisions.**

1. As mentioned above, the use of Validation layer to be able to separate this from the other classes and to make validation easier to unit testable. Each field can be unit tested as opposed to having a single method that validates all fields. Having a single method would have meant that unit testing would be difficult and further extensions could break things.

2. Versioning has been implemented by specifying the API version in the URL, this ensures that backwards compatibility is maintained and any breaking changes can be made to a newer version.

3. Error Handling has been extended from what was provided in the skeleton. The main decision here was to add a class for Bank Simulator Exceptions, this would allow us to return to the merchant whether the error lied with the bank simulator or within the payment gateway.

4. The correct HTTP status codes are returned to the merchant.

5. A big decision point was whether to return a single error message or a list of all encountered error messages. I have opted to return a list because it informs the merchant of all issues. If a single error message was returned, then say cardNumber is fixed, then there could be an issue with cvv as well, this would not be surfaced to the merchant until cardNumber is fixed meaning more calls will have to be made just to identify all issues with the request.

6. In terms of testing, I have tried to test each layer separately, ie. there are unit tests for the fields and validator and each validation is tested independently. There are also integrated tests for the controller and service. The idea of these tests is so that they test what the layer is responsible for, for eg, the controller test tests that HTTP concerns and the service tests whether the payments are stored or not etc.

7. I have also placed classes in a clear package structure that is easy to navigate and also placed based on the responsibility of the class.

8. Logging has been implemented to keep track of API calls received from merchants and also calls made to the simulator. Logging has records responses received and any key information when a method is called for ease of analyzing and debugging when required. Also, sensitive data is not logged anywhere.

