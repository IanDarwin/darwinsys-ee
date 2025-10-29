package com.darwinsys.servlet;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

public class HTMLDateUtilsTest {

	@Test
	public void testPrintMonthCalendar() throws Exception {
		System.out.println("XXX this is not a real test! Rewrite with StringOutputStream");
		PrintWriter out = new PrintWriter(System.out);
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Canada/Eastern"));
		c.set(Calendar.YEAR, 2007);
		c.set(Calendar.MONTH, Calendar.MARCH);
		c.set(Calendar.DAY_OF_MONTH, 15);
		HTMLDateUtils.printMonthCalendar(out, c);
		out.flush();
	}
}

