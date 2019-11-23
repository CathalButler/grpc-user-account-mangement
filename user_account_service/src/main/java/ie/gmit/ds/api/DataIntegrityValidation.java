package ie.gmit.ds.api;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/* Cathal Butler | G00346889 - Distributed Systems Project
 * DataIntegrityValidation class. This class is used to handle validation errors that may occur from constraint
 * violations that are set on fields in the User & User login POJO classes.
 * https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/en/part1/chapter7/complex_responses.html
 */
@XmlRootElement(name = "dataIntegrityValidation")
public class DataIntegrityValidation {
    //Member Variables
    private ArrayList<String> validationMessages = new ArrayList<String>();

    public void add(String message) {
        validationMessages.add(message);
    }

    public ArrayList<String> getErrorResponse() {
        return validationMessages;
    }

    public void setErrorResponse(ArrayList<String> validationMessages) {
        this.validationMessages = validationMessages;
    }
}// End class
