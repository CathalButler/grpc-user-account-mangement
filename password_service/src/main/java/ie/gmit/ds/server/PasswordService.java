package ie.gmit.ds.server;

import com.google.protobuf.ByteString;
import ie.gmit.ds.*;
import ie.gmit.ds.client.Client;
import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;

/* Cathal Butler | G00346889 - Distributed Systems Project
 * Password Service Class to handle password.proto Services
 */

public class PasswordService extends PasswordServiceGrpc.PasswordServiceImplBase {
    // Variables
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    // Constructor
    public PasswordService() {
    }// End Constructor

    /**
     * Override method from PasswordServiceGrpc.PasswordServiceImplBase that handles a hash request from a client.
     * Method makes a call to the Passwords class method hash to hash a password with a salt generated from the Passwords
     * class and a password provided from the client.
     *
     * @param request          from client
     * @param responseObserver response to client
     */
    @Override
    public void hash(UserHashRequest request, StreamObserver<UserHashResponse> responseObserver) {
        //Logger
        logger.info("Client Hash Request:\nUser ID: " + request.getUserId() + "\nPassword: "
                + request.getPassword() + "\n\n");
        //Variables
        int userId = request.getUserId();
        // Generate salt
        byte[] salt = Passwords.getNextSalt();
        // Run Hash method is Passwords class passing it the password in a char array and the salt byte array
        byte[] hashedPassword = Passwords.hash(request.getPassword().toCharArray(), salt);
        // Response
        UserHashResponse response = UserHashResponse.newBuilder().setUserId(userId)
                .setHashedPassword(ByteString.copyFrom(hashedPassword))
                .setSalt(ByteString.copyFrom(salt)).build();
        //Logger for response
        logger.info("Server Response:\n\nUser ID: " + response.getUserId() + "\nHashed Password: "
                + response.getHashedPassword() + "\nSalt: " + response.getSalt());
        // Send response
        responseObserver.onNext(response);
        // Complete response
        responseObserver.onCompleted();
    }// End method

    /**
     * Override method from PasswordServiceGrpc.PasswordServiceImplBase that handles a validate request from a client.
     * Method makes a call to the Passwords class method isExpectedPassword.
     * Returns true if the given password and salt match the hashed value, false otherwise.
     *
     * @param request          from client
     * @param responseObserver response to client
     */
    @Override
    public void validate(ValidateRequest request, StreamObserver<ValidateResponse> responseObserver) {
        //Logger
        logger.info("Client Validate Request:\nPassword: " + request.getPassword() + "\nSalt: "
                + request.getSalt() + "\nExpected Hash: " + request.getHashedPassword() + "\n\n");
        // Variables
        char[] password = request.getPassword().toCharArray();
        byte[] salt = request.getSalt().toByteArray();
        byte[] expectedHash = request.getHashedPassword().toByteArray();
        // Run checker method in Passwords Class
        boolean validity = Passwords.isExpectedPassword(password, salt, expectedHash);
        //Response
        ValidateResponse response = ValidateResponse.newBuilder().setValidity(validity).build();
        //Logger for response
        logger.info("Server Response: Valid Password (Returns true if the given password and salt match the hashed " +
                "value, false otherwise)\nResponse: " + response.getValidity() + "\n\n");
        // Send response
        responseObserver.onNext(response);
        // Complete response
        responseObserver.onCompleted();
    }// End method
}// End class
