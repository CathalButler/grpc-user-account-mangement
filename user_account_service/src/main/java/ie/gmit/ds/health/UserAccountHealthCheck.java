package ie.gmit.ds.health;

import com.codahale.metrics.health.HealthCheck;

public class UserAccountHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}//End class
