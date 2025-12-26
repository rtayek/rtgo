package gui;
import static org.junit.Assert.*;
import org.junit.Test;
enum AColor { r, g, b }
enum Letter { x, y, z }
class Buttons1 extends ButtonsABC<AColor> {
    {
        for(AColor c:AColor.values()) { new ButtonWithEnum(c); }
    }
}
class Buttons2 extends ButtonsABC<Letter> {
    {
        for(Letter l:Letter.values()) { new ButtonWithEnum(l); }
    }
}
class Buttons3 extends ButtonsABC<Letter> {
    {
        for(Letter l:Letter.values()) { new ButtonWithEnum(l); }
    }
}
class Buttons4 extends ButtonsABC<Enum<?>> {
    {
        for(AColor c:AColor.values()) { new ButtonWithEnum(c); }
        for(Letter l:Letter.values()) { new ButtonWithEnum(l); }
    }
}
class Buttons5 extends ButtonsABC<Enum<?>> {}
public class ButtonsTestCase {
    @Test public void testButtons1() {
        Buttons1 buttons1=new Buttons1();
        for(ButtonsABC<AColor>.ButtonWithEnum expected:buttons1.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonsABC<AColor>.ButtonWithEnum actual=buttons1.get(expected.t);
            assertEquals(expected,actual);
        }
    }
    @Test public void testButtons2() {
        Buttons2 buttons2=new Buttons2();
        for(ButtonsABC<Letter>.ButtonWithEnum expected:buttons2.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonsABC<Letter>.ButtonWithEnum actual=buttons2.get(expected.t);
            assertEquals(expected,actual);
        }
    }
    @Test public void testButtons3() {
        Buttons3 buttons3=new Buttons3();
        for(ButtonsABC<Letter>.ButtonWithEnum expected:buttons3.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonsABC<Letter>.ButtonWithEnum actual=buttons3.get(expected.t);
            assertEquals(expected,actual);
        }
    }
    @Test public void testButtons4() {
        Buttons4 buttons4=new Buttons4();
        for(Object anEmum:buttons4.enums()) {
            @SuppressWarnings("rawtypes") ButtonsABC.ButtonWithEnum button=buttons4.get((Enum<?>)anEmum);
            assertTrue(button.isEnabled());
            Object actual=button.t;
            assertEquals(anEmum,actual);
        }
    }
    @Test public void testIndependent() {
        Buttons2 buttons2=new Buttons2();
        Buttons3 buttons3=new Buttons3();
        ButtonsABC<Letter>.ButtonWithEnum button2=buttons2.get(Letter.x);
        ButtonsABC<Letter>.ButtonWithEnum button3=buttons3.get(Letter.x);
        button2.enableButton(true);
        assertTrue(button2.isEnabled());
        button3.enableButton(false);
        assertTrue(button2.isEnabled());
    }
    @Test public void testIndependent2() {
        Buttons2 buttons2=new Buttons2();
        Buttons2 buttons2Again=new Buttons2();
        ButtonsABC<Letter>.ButtonWithEnum button2=buttons2.get(Letter.x);
        ButtonsABC<Letter>.ButtonWithEnum button3=buttons2Again.get(Letter.x);
        button2.enableButton(true);
        assertTrue(button2.isEnabled());
        button3.enableButton(false);
        assertTrue(button2.isEnabled());
    }
    @Test public void testAddEnum() {
        Buttons5 buttons5=new Buttons5();
        buttons5.add(AColor.class);
        for(ButtonsABC<Enum<?>>.ButtonWithEnum expected:buttons5.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonsABC<Enum<?>>.ButtonWithEnum actual=buttons5.get(expected.t);
            assertEquals(expected,actual);
        }
    }
    @Test public void testAddTwoEnums() {
        Buttons5 buttons5=new Buttons5();
        buttons5.add(AColor.class);
        buttons5.add(Letter.class);
        for(ButtonsABC<Enum<?>>.ButtonWithEnum expected:buttons5.buttons()) {
            assertTrue(expected.isEnabled());
            ButtonsABC<Enum<?>>.ButtonWithEnum actual=buttons5.get(expected.t);
            assertEquals(expected,actual);
        }
    }
}
