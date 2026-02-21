package gui;
import static org.junit.Assert.*;
import org.junit.*;
public class MainTestCase {
    @Before public void setUp() throws Exception {
        //Init.first.restoreSystmeIO();
        Thread.sleep(100);
    }
    @After public void tearDown() throws Exception {}
    @Test public void testNewWestPanelConnectButtom() {
        Main main=Main.run("test");
        ButtonsABC<WestPanels.MyEnums> button=main.mediator.newWestPanel.buttons;
        assertTrue(button.get(WestPanels.MyEnums.connect).isEnabled());
    }
    @Test public void testNewWestPanel() {
        Main main=Main.run("test");
        ButtonsABC<WestPanels.MyEnums> button=main.mediator.newWestPanel.buttons;
        assertFalse(button.get(WestPanels.MyEnums.disconnect).isEnabled());
    }
}
