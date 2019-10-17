package ie.gmit.ds;

import com.google.protobuf.BoolValue;
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
    private final PasswordServiceGrpc.PasswordServiceStub asyncPasswordSerive;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub syncPasswordService;
    // Member Variables
    private static final Logger logger =
            Logger.getLogger(Client.class.getName());

    // Constructor
    public Client(String host, int port) {
        // Creat a channel:
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        // Create a stub & blocking stub with the channel
        asyncPasswordSerive = PasswordServiceGrpc.newStub(channel);
        syncPasswordService = PasswordServiceGrpc.newBlockingStub(channel);
    }// End Constructor

    // Shutdown Method
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }// End method

    // Hash Request
    public void hashRequest(UserHashRequest newUserRequest) {
        // Logger
        logger.info("Client: Making hashing request with details: " + newUserRequest);

        // Instance of response
        UserHashResponse response;
        try {
            // Create the response to the
            response = syncPasswordService.hash(newUserRequest);
            //Print response
            System.out.println(response);
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }// End try catch
    }// End method


    // Main method
    public static void main(String[] args) throws InterruptedException {
        // Creat an instance of the Client and pass it the host and port:
        Client client = new Client("localhost", 50551);

        // Creating a request to the Server:
        UserHashRequest requests = UserHashRequest.newBuilder()
                .setUserId(1234)
                .setPassword("yurtthecamel")
                .build();
        try {
            //Send request created above:
            client.hashRequest(requests);
        } finally {
            // Don't stop process, keep alive to receive async response
            Thread.currentThread().join();
        }
    }// End main method
}// Class
