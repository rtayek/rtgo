package utilities;
import io.Logging;
import static io.Init.first;
import org.junit.runner.*;
import controller.AbstractGameFixtureTestCase.SocketTestCase;
public class RunOneTestMethod {
	public static void runMethods() {
		Class<SocketTestCase> clazz=SocketTestCase.class;
		Logging.mainLogger.info(String.valueOf(clazz));
		String method="testA1";
		Request request=Request.method(clazz,method);
		TestRunReporter.logRequest(request);
		JUnitCore junit=new JUnitCore();
		Result result=junit.run(request);
		TestRunReporter.reportResult(result);
	}
	public static void main(String[] args) {
		first.twice(); // do this first in all main programs!
		runMethods();
	}
}
