package com.taqvimcod.calendar;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class HolidaysTest {
    @Test
    public void nowruzIsMarkedAsOfficialHoliday() {
        assertTrue(Holidays.isHoliday(1, 1, 1, 1));
        List<Holidays.Ev> events = Holidays.forDay(1, 1, 1, 1);
        assertFalse(events.isEmpty());
        assertEquals("عید نوروز", events.get(0).name(true));
        assertTrue(events.get(0).holiday);
    }

    @Test
    public void ordinaryDayIsNotAutomaticallyHoliday() {
        assertFalse(Holidays.isHoliday(2, 2, 4, 4));
    }

    @Test
    public void lunarHolidayIsRecognized() {
        assertTrue(Holidays.isHoliday(5, 5, 10, 1));
        List<Holidays.Ev> events = Holidays.forDay(5, 5, 10, 1);
        assertFalse(events.isEmpty());
        assertTrue(events.get(0).holiday);
    }
}
