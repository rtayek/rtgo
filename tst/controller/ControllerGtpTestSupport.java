package controller;

import org.junit.Rule;
import utilities.MyTestWatcher;
import java.util.Arrays;
import java.util.List;
import model.Model;
import com.tayek.util.core.Texts;

public abstract class ControllerGtpTestSupport {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    protected Model model;
    protected boolean useHexAscii = true;

    protected Model model() {
        if (model == null) model = createModel();
        return model;
    }

    protected Model createModel() {
        return new Model("model");
    }

    protected Response runGtpCommand(String command) {
        return runGtpCommand(model(), command);
    }

    protected Response runGtpCommand(Model model, String command) {
        return Response.response(runGtpCommandString(model, command));
    }

    protected Response runGtpCommand(String command, boolean justRun) {
        return runGtpCommand(model(), command, justRun);
    }

    protected Response runGtpCommand(Model model, String command, boolean justRun) {
        return Response.response(runGtpCommandString(model, command, justRun));
    }

    protected String runGtpCommandString(Model model, String command) {
        return runGtpCommandString(model, command, true);
    }

    protected String runGtpCommandString(Model model, String command, boolean justRun) {
        GTPBackEnd backEnd = new GTPBackEnd(command, model);
        backEnd.useHexAscii = useHexAscii;
        return backEnd.runCommands(justRun);
    }

    protected Response[] runGtpCommands(String... commands) {
        return runGtpCommands(model(), Arrays.asList(commands));
    }

    protected Response[] runGtpCommands(List<String> commands) {
        return runGtpCommands(model(), commands);
    }

    protected Response[] runGtpCommands(Model model, List<String> commands) {
        return runGtpCommands(model, commands, true);
    }

    protected Response[] runGtpCommands(List<String> commands, boolean justRun) {
        return runGtpCommands(model(), commands, justRun);
    }

    protected Response[] runGtpCommands(Model model, List<String> commands, boolean justRun) {
        GTPBackEnd backEnd = new GTPBackEnd(Texts.cat(commands), model);
        backEnd.useHexAscii = useHexAscii;
        return Response.responses(backEnd.runCommands(justRun));
    }
}

