package ie.gmit.ds.health;

import com.codahale.metrics.health.HealthCheck;

/* Cathal Butler | G00346889
 * Class that handles checking the health of the User Account Service Application
 */

public class UserAccountHealthCheck extends HealthCheck {

    /**
     * @return the health of the User Account Service Application
     * @throws Exception
     */
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}//End class
