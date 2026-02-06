package controller;

import org.junit.After;
import org.junit.Before;
import com.tayek.util.io.End.Holder;
import utilities.TestSupport;

public abstract class ControllerHolderTestSupport extends TestSupport {
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
