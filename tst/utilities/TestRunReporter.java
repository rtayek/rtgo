package utilities;
import io.Logging;
import java.util.List;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
final class TestRunReporter {
    private TestRunReporter() {}
    static void logRequest(Request request) {
        Logging.mainLogger.info("get request");
        Logging.mainLogger.info(String.valueOf(request));
    }
    static void reportResult(Result result) {
        Logging.mainLogger.info("after run");
        printFailures(result);
        Logging.mainLogger.info("Test successful? "+result.wasSuccessful());
    }
    private static void printFailures(Result result) {
        List<Failure> failures=result.getFailures();
        if(failures.size()>0) {
            Logging.mainLogger.info(failures.size()+" failures: "+failures);
            for(Failure failure:result.getFailures()) {
                Logging.mainLogger.info("message: "+failure.getMessage());
                if(failure.getException()!=null) {
                    Logging.mainLogger.info(String.valueOf(failure.getException()));
                    //failure.getException().printStackTrace();
                }
            }
        } else Logging.mainLogger.info("no failures or errors.");
    }
}
