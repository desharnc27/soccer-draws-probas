package tools;

/**
 *
 * @author desharnc27
 */
public class RootException extends Exception {

    public static RootException make(String path, int idx0, int idx1) {
        String message = "Root path could not be guessed with confidence, at least two possibilites:" + "\n";
        message += path.substring(0, idx0) + "and \n";
        message += path.substring(0, idx1) + "and \n";
        return new RootException(message);
    }

    public RootException(String s) {

        super(s);
    }
}
