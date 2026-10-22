package com.taqvimcod.calendar;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CityDatabaseTest {

    private static List<CityDatabase.City> cities() {
        return CityDatabase.getIranianCities();
    }

    @Test public void databaseHasAtLeast120Cities() {
        assertTrue("city count was " + cities().size(), cities().size() >= 120);
    }

    @Test public void allCoordinatesInsideIranBoundingBox() {
        for (CityDatabase.City c : cities()) {
            assertTrue(c.name + " lat " + c.latitude, c.latitude >= 25.0 && c.latitude <= 40.0);
            assertTrue(c.name + " lon " + c.longitude, c.longitude >= 44.0 && c.longitude <= 64.0);
        }
    }

    @Test public void cityNamesAreUnique() {
        Set<String> seen = new HashSet<>();
        for (CityDatabase.City c : cities()) {
            assertNotNull(c.name);
            assertTrue("duplicate city: " + c.name, seen.add(c.name));
        }
    }

    @Test public void all31ProvincialCapitalsPresent() {
        String[] capitals = {"تهران","زنجان","مشهد","اصفهان","شیراز","تبریز","اهواز","قم","کرج",
                "کرمانشاه","رشت","ارومیه","یزد","همدان","قزوین","اردبیل","بندرعباس","ساری","کرمان",
                "زاهدان","ایلام","بوشهر","شهرکرد","بیرجند","بجنورد","یاسوج","گرگان","خرم‌آباد",
                "سنندج","اراک","سمنان"};
        Set<String> names = new HashSet<>();
        for (CityDatabase.City c : cities()) names.add(c.name);
        for (String cap : capitals) assertTrue("missing capital " + cap, names.contains(cap));
    }

    /** Spot checks against well-known city-center coordinates (tolerance 0.2° ≈ 22 km). */
    @Test public void spotCheckKnownCoordinates() {
        assertCity("تهران", 35.69, 51.39);
        assertCity("مشهد", 36.30, 59.61);
        assertCity("اصفهان", 32.65, 51.67);
        assertCity("بندرعباس", 27.18, 56.27);
        assertCity("آبادان", 30.34, 48.30);
        assertCity("کاشان", 33.99, 51.43);
    }

    private static void assertCity(String name, double lat, double lon) {
        CityDatabase.City found = null;
        for (CityDatabase.City c : cities()) if (c.name.equals(name)) found = c;
        assertNotNull(name + " not in database", found);
        assertTrue(name + " lat " + found.latitude, Math.abs(found.latitude - lat) <= 0.2);
        assertTrue(name + " lon " + found.longitude, Math.abs(found.longitude - lon) <= 0.2);
    }
}
