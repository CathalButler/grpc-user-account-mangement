package ie.gmit.ds.resources;

import ie.gmit.ds.api.UserAccount;
import ie.gmit.ds.db.UserAccountDB;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;


@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class UserAccountResource {
    //Member Variable
    private final Validator validator;

    //Constructor
    public UserAccountResource(Validator validator) {
        this.validator = validator;
    }// End Constructor

    /**
     * Method to get all user accounts in the database
     *
     * @return response status to client
     */
    @GET
    public Response getUserAccounts() {
        return Response.ok(UserAccountDB.getUserAccounts()).build();
    }

    /**
     * Method to get a user account by id
     *
     * @param id
     * @return response status to client
     */
    @GET
    @Path("/{userId}")
    public Response getUserAccountById(@PathParam("userId") Integer id) {
        UserAccount userAccount = UserAccountDB.getUserAccountById(id);
        if (userAccount != null)
            return Response.ok(userAccount).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }//End get user by id method

    /**
     * @param userAccount
     * @return response status to client
     * @throws URISyntaxException
     */
    @POST
    @Path("/add")
    public Response createUserAccount(UserAccount userAccount) throws URISyntaxException {
        // Validation
        Set<ConstraintViolation<UserAccount>> violations = validator.validate(userAccount);
        UserAccount ua = UserAccountDB.getUserAccountById(userAccount.getUserId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<UserAccount> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }//End for loop
            //Return status response to client
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }// end if
        if (ua == null) {
            UserAccountDB.addUserAccount(userAccount.getUserId(), userAccount);
            //Return ok response to client
            return Response.created(new URI("/login/" + userAccount.getUserId()))
                    .build();
        } else
            //Return status problem to user
            return Response.status(Response.Status.NOT_FOUND).build();
    }// End createUserAccount method

    /**
     * @param id
     * @param userAccount
     * @return response status to client
     */
    @PUT
    @Path("/{userId}")
    public Response updateUserAccountById(@PathParam("userId") Integer id, UserAccount userAccount) {
        // Validation
        Set<ConstraintViolation<UserAccount>> violations = validator.validate(userAccount);
        UserAccount ua = UserAccountDB.getUserAccountById(userAccount.getUserId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<UserAccount> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }//End for loop
            //Return status response to client
            return Response.status(Response.Status.BAD_REQUEST).entity(validationMessages).build();
        }// end if
        // If user account is not = to null do update
        if (ua != null) {
            UserAccountDB.updateUserAccount(userAccount.getUserId(), userAccount);
            //Return ok response to client
            return Response.ok(userAccount).build();
        } else
            //Return status problem to user
            return Response.status(Response.Status.NOT_FOUND).build();
    }// End updateUserAccountById method
}// End class
