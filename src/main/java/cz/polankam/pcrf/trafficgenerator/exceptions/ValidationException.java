package cz.polankam.pcrf.trafficgenerator.exceptions;

/**
 * Used when the validation of the YAML configuration fails.
 */
public class ValidationException extends Exception {

    /**
     * Constructor.
     * @param message error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message error message
     * @param cause previous exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
