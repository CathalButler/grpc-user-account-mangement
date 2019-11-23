package ie.gmit.ds.client;

import io.dropwizard.lifecycle.Managed;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedGrpcChannel implements Managed {
    //Member Variables
    private static final Logger logger = LoggerFactory.getLogger(ManagedGrpcChannel.class);
    private final ManagedChannel channel;

    public ManagedGrpcChannel(final ManagedChannel channel) {
        this.channel = channel;
    }

    @Override
    public void start() throws Exception {
        //Not used to start a gRPC channel
    }

    @Override
    public void stop() throws Exception {
        logger.info("Shutting down gRPC client connection to service", channel);
        channel.shutdown();
        logger.info("Shutdown complete");
    }//End override stop method
}//End class
