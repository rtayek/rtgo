package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import io.IOs.End.Holder;
import utilities.MyTestWatcher;

public abstract class ControllerHolderTestSupport {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    protected Holder holder;

    protected abstract Holder createHolder() throws Exception;

    @Before public void setUpHolder() throws Exception {
        holder = createHolder();
        if (holder == null) throw new IllegalStateException("holder must not be null");
        onHolderCreated(holder);
    }

    @After public void tearDownHolder() throws Exception {
        holder = null;
    }

    protected void onHolderCreated(Holder holder) {}
}
