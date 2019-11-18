package ie.gmit.ds;

import ie.gmit.ds.health.UserAccountHealthCheck;
import ie.gmit.ds.resources.UserAccountResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Cathal Butler | G00346889
 *
 */

public class App extends Application<Configuration> {
    //Member Variables
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    final UserAccountHealthCheck healthCheck = new UserAccountHealthCheck();

    /**
     * @param configuration
     * @param environment
     * @throws Exception
     */
    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        LOGGER.info("Registering REST resources");
        environment.jersey().register(new UserAccountResource(environment.getValidator()));
        //Register health check class
        environment.healthChecks().register("UserAccount Health Check", healthCheck);
    }// end run method

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }// end main method
}// End class
