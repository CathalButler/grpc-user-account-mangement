package ie.gmit.ds.health;

import com.codahale.metrics.health.HealthCheck;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.grpc.ConnectivityState.TRANSIENT_FAILURE;

/* Cathal Butler | G00346889
 * Class that handles checking the health of the connection to the gRPC Password Service.
 */

public class GrpcPasswordServiceHealthCheck extends HealthCheck {
    //Member Variables
    private final ManagedChannel channel;
    private static final Logger logger = LoggerFactory.getLogger(GrpcPasswordServiceHealthCheck.class);

    public GrpcPasswordServiceHealthCheck(ManagedChannel channel) {
        this.channel = channel;
    }

    /**
     * @return the health of the connection to the gRPC Password Service
     * @throws Exception
     */
    @Override
    protected Result check() throws Exception {
        if (channel.getState(true) == TRANSIENT_FAILURE) {
            logger.info("Cannot connect to gRPC Password Service");
            return Result.unhealthy("Cannot connect to gRPC Password Service");
        } else {
            logger.info("gRPC Password Service Connected");
            return Result.healthy("gRPC Password Service Connected");
        }// End if else
    }//End check method
}//End class
