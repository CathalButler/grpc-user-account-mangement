package ie.gmit.ds;

import ie.gmit.ds.health.GrpcPasswordServiceHealthCheck;
import ie.gmit.ds.health.UserAccountHealthCheck;
import ie.gmit.ds.resources.UserAccountResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Cathal Butler | G00346889
 * Class that handles registering services into the application
 */

public class App extends Application<UserAccountConfig> {
    //Member Variables
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    final UserAccountHealthCheck healthCheck = new UserAccountHealthCheck();

    /**
     * @param configuration
     * @param environment
     */
    @Override
    public void run(final UserAccountConfig configuration, final Environment environment) {
        LOGGER.info("Registering REST resources");
        //Register health check class
        environment.healthChecks().register("UserAccount Health Check", healthCheck);
        //Register gRPC external service
        final ManagedChannel externalServiceChannel;
        externalServiceChannel = configuration.getExternalGrpcChannelFactory().build(environment);
        //Registering two services to class UserAccountResource. Validation and gRPC External Password Service
        environment.jersey().register(new UserAccountResource(environment.getValidator(), externalServiceChannel));
        //Registering gRPC Password Service Health Check
        environment.healthChecks().register("gRPC Password Service", new GrpcPasswordServiceHealthCheck(externalServiceChannel));
    }//End override run method

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }// end main method
}// End class
