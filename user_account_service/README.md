# Distributed System Project
### Final year Software Development | Cathal Butler

Dropwizard RESTful User Account Service that runs on your `localhost` and port `9000`. The program is a REST API that 
allows users make GET, POST, PUT & DELETE requests to it to be able to CREATE, READ, UPDATE & DELETE User accounts.

This is part 2 of a final year project where part one in this repo was developed first and ties in with this part to allow
hashing requests of passwords be done when a user either creates or updates an account in the database. An endpoint /login
allows users who`s account details are in the database validate there user ID and password. When a request is made 
the application will check to see if the user ID exists in the database, if so it will then check with the password service
to see if the password matches the salt and hash stored with the users details. 

### [API Design](https://app.swaggerhub.com/apis/butlawr/UserAccountAPI/1.0)

### [Releases here](https://github.com/butlawr/grpc-user-account-mangement/releases)

#### Running the application:
To run the jar requires the server command requires a configuration file that sets the hostname and port for both the 
User Service and connection to the Password Service. Please download [applicationConfig](https://github.com/butlawr/grpc-user-account-mangement/blob/master/user_account_service/userAccountConfig.yaml)

Once the jar has been downloaded run `java -jar user-account-service.jar server userAccountConfig.yaml`

The program will log requests made from the clients and any server information.