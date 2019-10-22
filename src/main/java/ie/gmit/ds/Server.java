package ie.gmit.ds;

import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

/* Cathal Butler | G00346889 - Distributed Systems Project
 * Server Class
 */

public class Server {
    //Member Variables
    private io.grpc.Server grpcServer;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int PORT = 50551;

    // Server Startup and shutdown methods
    private void start() throws IOException {
        grpcServer = ServerBuilder.forPort(PORT)
                .addService(new PasswordService())
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);
    }// End start method

    // Shutdown Method
    private void stop() {
        if (grpcServer != null) {
            grpcServer.shutdown();
        }
    }// End shutdown method

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();
        }
    }

    // MAIN SERVER METHOD
    public static void main(String[] args) throws IOException, InterruptedException {
        final Server server = new Server();
        server.start();
        server.blockUntilShutdown();
    }// End main method
}// End class
