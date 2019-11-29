package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import ie.gmit.ds.client.GrpcChannelFactory;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/* Cathal Butler | G00346889
 * Class that handles getting configuration options set in the UserAccountConfig.yaml file.
 * 'externalService' is the hostname & port for the gRPC Password service.
 * This ties in with teh GrpcChannelFactory class
 */

public class UserAccountConfig extends Configuration {

    @Valid
    @NotNull
    private GrpcChannelFactory externalPasswordService = new GrpcChannelFactory();

    @JsonProperty("externalService")
    public GrpcChannelFactory getExternalGrpcChannelFactory() {
        return externalPasswordService;
    }

    @JsonProperty("externalService")
    public void setExternalGrpcChannelFactory(final GrpcChannelFactory externalService) {
        this.externalPasswordService = externalService;
    }
}//End class
