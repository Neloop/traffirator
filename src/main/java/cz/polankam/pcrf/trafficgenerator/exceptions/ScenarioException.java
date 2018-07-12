package cz.polankam.pcrf.trafficgenerator.exceptions;

/**
 * Used if there is any problem with scenarios.
 */
public class ScenarioException extends Exception {

    /**
     * Constructor.
     * @param message error message
     */
    public ScenarioException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message error message
     * @param cause previous exception
     */
    public ScenarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
