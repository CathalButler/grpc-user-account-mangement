package ie.gmit.ds.client;

import com.google.protobuf.ByteString;
import ie.gmit.ds.*;
import ie.gmit.ds.server.HashedData;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Cathal Butler | G00346889 - Distributed Systems Project
 * Client Class
 */

public class Client {
    // Member Variables
    private final ManagedChannel channel;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub syncPasswordService;
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    // Member variables for storing hash & salt
    private ByteString hashedPassword;
    private ByteString salt;

    // Constructor
    public Client(String host, int port) {
        // Creat a channel:
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        // Create a stub & blocking stub with the channel
        syncPasswordService = PasswordServiceGrpc.newBlockingStub(channel);
    }// End Constructor

    // Shutdown Method
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }// End method

    // Hash Request

    /**
     * Method that makes a hash request to the server with the params below
     * @param userId
     * @param password
     */
    public void hashRequest(int userId, String password) {
        // Logger
        logger.info("\n\nClient: Making hashing request with details: User ID: " + userId + " password: " + password);
        //Variables
        HashedData hd = new HashedData();
        // Creating a request to the Server:
        UserHashRequest request = UserHashRequest.newBuilder().setUserId(userId).setPassword(password).build();
        // Instance of User Hash Response
        UserHashResponse response;
        try {
            // Make a request to the server hash method
            response = syncPasswordService.hash(request);
            //Setting hashed password var and salt
            this.setHashedPassword(response.getHashedPassword());
            this.setSalt(response.getSalt());
        } catch (StatusRuntimeException ex) {
            // Log exception if any
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }// End try catch

        //Logger for response
        logger.info("\n\nServer Response:\nUser ID: " + response.getUserId() + "\nHashed Password: "
                + response.getHashedPassword() + "\nSalt: " + response.getSalt());
    }// End method

    /**
     * Mehtod that makes a validate request to the server with the parameters below
     * @param password
     * @param hashedPassword
     * @param salt
     */
    public void validateRequest(String password, ByteString hashedPassword, ByteString salt) {
        // Logger
        logger.info("\n\nClient: Making Validation request with details Password: " + password + "\nHashed Password: "
                + hashedPassword + "\nSalt " + salt);
        //Creating a request to the server
        ValidateRequest request = ValidateRequest.newBuilder().setPassword(password).setHashedPassword(hashedPassword)
                .setSalt(salt)
                .build();
        // Instance of Validate response
        ValidateResponse response;
        try {
            // Create the response to the
            response = syncPasswordService.validate(request);
        } catch (StatusRuntimeException ex) {
            // Log exception if any
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }// End try catch
        //Logger for response
        logger.info("\n\nServer Response: Valid Password (Returns true if the given password and salt match the hashed " +
                "value, false otherwise)\n:" + response.getValidity());
    }// End validate request method

    public ByteString getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(ByteString hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public ByteString getSalt() {
        return salt;
    }

    public void setSalt(ByteString salt) {
        this.salt = salt;
    }

    // Main method
    public static void main(String[] args) throws InterruptedException {
        // Creat an instance of the Client and pass it the host and port:
        Client client = new Client("localhost", 50551);

        try {
            //Send request created above:
            // Hardcoded test data
            client.hashRequest(1234, "yurt the world");
            //Run data through validate method
            client.validateRequest("yurt the world", client.getHashedPassword(), client.getSalt());
        } finally {
            // Don't stop process, keep alive to receive async response
            Thread.currentThread().join();
        }//End try catch
    }// End main method
}// Class
