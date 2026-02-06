package io;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static com.tayek.util.range.Range.*;

public class UseJarTestCase {
	@Before public void setUp() throws Exception {}
	@After public void tearDown() throws Exception {}
	@Test public void test() {
		assertTrue(range(1,2).contains(2));
	}
}
