package com.taqvimcod.calendar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Offline starter list of Iranian cities and approximate city-center coordinates. */
public final class CityDatabase {
    private CityDatabase() {}

    public static final class City {
        public final String name;
        public final double latitude;
        public final double longitude;

        public City(String name, double latitude, double longitude) {
            if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("City name is required");
            if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
                throw new IllegalArgumentException("Coordinates out of range");
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
        @Override public String toString() { return name; }
    }

    private static final List<City> CITIES = Collections.unmodifiableList(Arrays.asList(
        new City("تهران", 35.6892, 51.3890),
        new City("زنجان", 36.6736, 48.4787),
        new City("مشهد", 36.2972, 59.6067),
        new City("اصفهان", 32.6546, 51.6680),
        new City("شیراز", 29.5918, 52.5837),
        new City("تبریز", 38.0800, 46.2919),
        new City("اهواز", 31.3183, 48.6706),
        new City("قم", 34.6401, 50.8764),
        new City("کرج", 35.8400, 50.9391),
        new City("کرمانشاه", 34.3277, 47.0778),
        new City("رشت", 37.2808, 49.5832),
        new City("ارومیه", 37.5527, 45.0761),
        new City("یزد", 31.8974, 54.3569),
        new City("همدان", 34.7989, 48.5150),
        new City("قزوین", 36.2797, 50.0049),
        new City("اردبیل", 38.2498, 48.2933),
        new City("بندرعباس", 27.1832, 56.2666),
        new City("ساری", 36.5633, 53.0601),
        new City("کرمان", 30.2839, 57.0834),
        new City("زاهدان", 29.4963, 60.8629),
        new City("ایلام", 33.6374, 46.4227),
        new City("بوشهر", 28.9234, 50.8203),
        new City("شهرکرد", 32.3256, 50.8644),
        new City("بیرجند", 32.8663, 59.2211),
        new City("بجنورد", 37.4750, 57.3290),
        new City("یاسوج", 30.6682, 51.5879),
        new City("گرگان", 36.8456, 54.4393),
        new City("خرم‌آباد", 33.4878, 48.3558),
        new City("سنندج", 35.3149, 46.9988),
        new City("اراک", 34.0917, 49.6892),
        new City("سمنان", 35.5769, 53.3970)
    ));

    public static List<City> getIranianCities() { return CITIES; }
}
