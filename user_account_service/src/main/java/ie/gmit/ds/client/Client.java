package ie.gmit.ds.client;

import com.google.protobuf.ByteString;
import ie.gmit.ds.*;
import ie.gmit.ds.api.User;
import ie.gmit.ds.db.UserAccountDB;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Cathal Butler | G00346889 - Distributed Systems Project
 * Client Class. This class will make request to the server which the server will respond to the request made.
 */

public class Client {
    // Member Variables
    private final ManagedChannel channel;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub blockingStub;
    private final PasswordServiceGrpc.PasswordServiceStub asyncStub;
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    // Constructor
    public Client(String host, int port) {
        // Creat a channel:
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }// End Constructor

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public Client(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = PasswordServiceGrpc.newBlockingStub(channel);
        asyncStub = PasswordServiceGrpc.newStub(channel);
    }

    // Shutdown Method
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }// End method

    // Hash Request

    /**
     * Method that makes a hash request to the server with the params below
     *
     * @param newUser
     */
    public void hashRequest(User newUser) {
        // Creating a request to the Server:
        UserHashRequest request = UserHashRequest.newBuilder().setUserId(newUser.getUserId()).setPassword(newUser.getPassword()).build();
        // Make a request to the server hash method
        //Request is passed with SteamObserver listens for a response
        asyncStub.hash(request, new StreamObserver<UserHashResponse>() {
            @Override
            public void onNext(UserHashResponse userHashResponse) {
                //Log incoming request
                logger.info("Received request items: " + userHashResponse);


                //Creating a new User object with the now hashed password and salt
                User user = new User(newUser.getUserId(), newUser.getUserName(), newUser.getEmail(),
                        userHashResponse.getSalt(), userHashResponse.getHashedPassword());

                logger.info("\n==================================\n" + user.toString() + "\n====================================\nUser after created with toStringUtf8:");
                //Added the new user account to the database
                UserAccountDB.addUserAccount(user.getUserId(), user);
            }

            @Override
            public void onError(Throwable throwable) {
                //Log errors if any:
                Status status = Status.fromThrowable(throwable);
                logger.log(Level.WARNING, "RPC Error: {0}", status);
            }//End onError

            @Override
            public void onCompleted() {
                logger.info("Finished Request");
                channel.shutdownNow();
            }//End onCompleted
        });
    }// End method

    /**
     * Mehtod that makes a validate request to the server with the parameters below
     *
     * @param password
     * @param hashedPassword
     * @param salt
     */
    public boolean validateRequest(String password, String hashedPassword, String salt) {
        //Creating a request to the server
        ValidateRequest request = ValidateRequest.newBuilder()
                .setPassword(password)
                .setHashedPassword(ByteString.copyFrom(Base64.getDecoder().decode(hashedPassword)))
                .setSalt(ByteString.copyFrom(Base64.getDecoder().decode(salt)))
                .build();
        // Instance of Validate response
        ValidateResponse response;
        try {

            // Create the response to the
            response = blockingStub.validate(request);
            System.out.println(response.getValidity());
            return response.getValidity();

        } catch (StatusRuntimeException ex) {
            // Log exception if any
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return false;
        }// End try catch
    }// End validate request method
}// Class
