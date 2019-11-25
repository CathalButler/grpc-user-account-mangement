package ie.gmit.ds.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class GrpcChannelFactory {

    //Member variables
    @NotEmpty
    private String hostname;

    @Min(1)
    @Max(65535)
    private int port;

    /**
     * @return ManagedChannelBuilder with hostname and port set from config file
     */
    public ManagedChannelBuilder builder() {
        return ManagedChannelBuilder.forAddress(getHostname(), getPort()).usePlaintext();
    }//End ManagedChannelBuilder

    /**
     * @param environment
     * @return ManagedChannel with hostname and port set from config file
     */
    public ManagedChannel build(final Environment environment) {
        final ManagedChannel managedChannel;
        managedChannel = builder().build();
        environment.lifecycle().manage(new ManagedGrpcChannel(managedChannel));
        return managedChannel;
    }// ENd managedChannel

    @JsonProperty
    public String getHostname() {
        return hostname;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }
}// End class
