package gui;
import static org.junit.Assert.*;
import org.junit.*;
import gui.WestPanels.WestPanel.ConnectButtons;
public class MainTestCase {
    @Before public void setUp() throws Exception {
        //Init.first.restoreSystmeIO();
        Thread.sleep(100);
    }
    @After public void tearDown() throws Exception {}
    @Test public void testOldWestPanelConnectButtom() {
        Mediator.useNewWestPanel=false;
        Main.run("test");
        assertTrue(ConnectButtons.connect.abstractButton.isEnabled());
    }
    @Test public void testOldWestPanel() {
        Mediator.useNewWestPanel=false;
        Main.run("test");
        assertFalse(ConnectButtons.disconnect.abstractButton.isEnabled());
    }
    @Test public void testNewWestPanelConnectButtom() {
        Mediator.useNewWestPanel=true;
        Main main=Main.run("test");
        ButtonsABC button=main.mediator.newWestPanel.buttons;
        assertTrue(button.get(WestPanels.MyEnums.connect).isEnabled());
    }
    @Test public void testNewWestPanel() {
        Mediator.useNewWestPanel=true;
        Main main=Main.run("test");
        ButtonsABC button=main.mediator.newWestPanel.buttons;
        assertFalse(button.get(WestPanels.MyEnums.disconnect).isEnabled());
    }
}
