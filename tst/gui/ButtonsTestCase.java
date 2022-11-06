package gui;
import static org.junit.Assert.*;
import org.junit.Test;
import gui.ButtonsABC.ButtonWithEnum;
enum AColor { r, g, b }
enum Letter { x, y, z }
class Buttons1 extends ButtonsABC {
    {
        for(AColor c:AColor.values()) { new ButtonWithEnum<AColor>(c); }
    }
}
class Buttons2 extends ButtonsABC {
    {
        for(Letter l:Letter.values()) { new ButtonWithEnum<Letter>(l); }
    }
}
class Buttons3 extends ButtonsABC {
    {
        for(Letter l:Letter.values()) { new ButtonWithEnum<Letter>(l); }
    }
}
class Buttons4 extends ButtonsABC {
    {
        for(AColor c:AColor.values()) { new ButtonWithEnum<AColor>(c); }
        for(Letter l:Letter.values()) { new ButtonWithEnum<Letter>(l); }
    }
}
class Buttons5 extends ButtonsABC {}
public class ButtonsTestCase {
    @Test public void testButtons1() {
        Buttons1 buttons1=new Buttons1();
        for(ButtonWithEnum<AColor> expected:buttons1.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonWithEnum<AColor> actual=buttons1.get(expected.t);
            assertEquals(expected,actual);
        }
    }
    @Test public void testButtons2() {
        Buttons2 buttons2=new Buttons2();
        for(ButtonWithEnum<Letter> expected:buttons2.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonWithEnum<Letter> actual=buttons2.get(expected.t);
            assertEquals(expected,actual);
        }
    }
    @Test public void testButtons3() {
        Buttons3 buttons3=new Buttons3();
        for(ButtonWithEnum<Letter> expected:buttons3.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonWithEnum<Letter> actual=buttons3.get(expected.t);
            assertEquals(expected,actual);
        }
    }
    @Test public void testButtons4() {
        Buttons4 buttons4=new Buttons4();
        for(Object anEmum:buttons4.enums()) {
            @SuppressWarnings("rawtypes") ButtonWithEnum button=buttons4.get((Enum)anEmum);
            assertTrue(button.isEnabled());
            Object actual=button.t;
            assertEquals(anEmum,actual);
        }
    }
    @Test public void testIndependent() {
        Buttons2 buttons2=new Buttons2();
        Buttons3 buttons3=new Buttons3();
        @SuppressWarnings("unchecked") ButtonWithEnum<Letter> button2=buttons2.get(Letter.x);
        @SuppressWarnings("unchecked") ButtonWithEnum<Letter> button3=buttons3.get(Letter.x);
        button2.enableButton(true);
        assertTrue(button2.isEnabled());
        button3.enableButton(false);
        assertTrue(button2.isEnabled());
    }
    @Test public void testIndependent2() {
        Buttons2 buttons2=new Buttons2();
        Buttons2 buttons2Again=new Buttons2();
        @SuppressWarnings("unchecked") ButtonWithEnum<Letter> button2=buttons2.get(Letter.x);
        @SuppressWarnings("unchecked") ButtonWithEnum<Letter> button3=buttons2Again.get(Letter.x);
        button2.enableButton(true);
        assertTrue(button2.isEnabled());
        button3.enableButton(false);
        assertTrue(button2.isEnabled());
    }
    @Test public void testAddEnum() {
        Buttons5 buttons5=new Buttons5();
        buttons5.add(AColor.class);
        for(ButtonWithEnum<AColor> expected:buttons5.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonWithEnum<AColor> actual=buttons5.get(expected.t);
            assertEquals(expected,actual);
        }
    }
    @Test public void testAddTwoEnums() {
        Buttons5 buttons5=new Buttons5();
        buttons5.add(AColor.class);
        buttons5.add(Letter.class);
        for(ButtonWithEnum expected:buttons5.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonWithEnum actual=buttons5.get(expected.t);
            assertEquals(expected,actual);
        }
    }
}
