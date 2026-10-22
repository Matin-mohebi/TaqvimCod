package com.taqvimcod.calendar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Offline list of Iranian cities and approximate city-center coordinates.
 * All 31 provincial capitals first (kept in the historical order so existing
 * saved selections keep their index), then the largest cities of every
 * province. Coordinates are WGS84 at 2-decimal precision.
 */
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
        // ---------- 31 provincial capitals (original order, indexes preserved) ----------
        new City("تهران", 35.69, 51.39),
        new City("زنجان", 36.67, 48.48),
        new City("مشهد", 36.30, 59.61),
        new City("اصفهان", 32.65, 51.67),
        new City("شیراز", 29.59, 52.58),
        new City("تبریز", 38.08, 46.29),
        new City("اهواز", 31.32, 48.67),
        new City("قم", 34.64, 50.88),
        new City("کرج", 35.84, 50.94),
        new City("کرمانشاه", 34.33, 47.08),
        new City("رشت", 37.28, 49.58),
        new City("ارومیه", 37.55, 45.08),
        new City("یزد", 31.90, 54.36),
        new City("همدان", 34.80, 48.52),
        new City("قزوین", 36.28, 50.00),
        new City("اردبیل", 38.25, 48.29),
        new City("بندرعباس", 27.18, 56.27),
        new City("ساری", 36.56, 53.06),
        new City("کرمان", 30.28, 57.08),
        new City("زاهدان", 29.50, 60.86),
        new City("ایلام", 33.64, 46.42),
        new City("بوشهر", 28.92, 50.82),
        new City("شهرکرد", 32.33, 50.86),
        new City("بیرجند", 32.87, 59.22),
        new City("بجنورد", 37.48, 57.33),
        new City("یاسوج", 30.67, 51.59),
        new City("گرگان", 36.85, 54.44),
        new City("خرم‌آباد", 33.49, 48.36),
        new City("سنندج", 35.32, 47.00),
        new City("اراک", 34.09, 49.69),
        new City("سمنان", 35.58, 53.40),
        // ---------- استان تهران ----------
        new City("ری", 35.59, 51.44),
        new City("اسلامشهر", 35.55, 51.23),
        new City("شهریار", 35.66, 51.06),
        new City("ورامین", 35.32, 51.65),
        // ---------- استان البرز ----------
        new City("هشتگرد", 35.96, 50.77),
        new City("نظرآباد", 35.95, 50.61),
        // ---------- استان زنجان ----------
        new City("ابهر", 36.15, 47.55),
        new City("قیدار", 36.80, 48.49),
        new City("خرمدره", 36.20, 48.52),
        // ---------- استان قزوین ----------
        new City("تاکستان", 36.07, 49.70),
        new City("بویین‌زهرا", 35.77, 50.05),
        // ---------- استان گیلان ----------
        new City("بندر انزلی", 37.47, 49.46),
        new City("لاهیجان", 37.21, 50.00),
        new City("لنگرود", 37.19, 50.15),
        new City("آستارا", 38.45, 48.87),
        // ---------- استان مازندران ----------
        new City("آمل", 36.47, 52.32),
        new City("بابل", 36.55, 52.68),
        new City("قائم‌شهر", 36.47, 52.86),
        new City("تنکابن", 36.83, 50.87),
        new City("چالوس", 36.65, 51.41),
        // ---------- استان گلستان ----------
        new City("گنبد کاووس", 37.25, 55.17),
        new City("بندر ترکمن", 36.90, 54.06),
        new City("آزادشهر", 36.82, 55.11),
        // ---------- خراسان رضوی ----------
        new City("نیشابور", 36.21, 58.80),
        new City("سبزوار", 36.21, 57.68),
        new City("تربت حیدریه", 35.28, 59.22),
        new City("قوچان", 37.11, 58.51),
        // ---------- خراسان شمالی ----------
        new City("شیروان", 37.39, 57.92),
        new City("اسفراین", 37.07, 57.51),
        new City("جاجرم", 36.94, 56.37),
        // ---------- خراسان جنوبی ----------
        new City("قائن", 33.72, 59.18),
        new City("فردوس", 34.02, 58.47),
        new City("طبس", 33.60, 56.92),
        // ---------- استان اصفهان ----------
        new City("کاشان", 33.99, 51.43),
        new City("خمینی‌شهر", 32.70, 51.52),
        new City("نجف‌آباد", 32.63, 51.37),
        new City("شاهین‌شهر", 32.86, 51.55),
        new City("گلپایگان", 33.45, 50.29),
        // ---------- استان فارس ----------
        new City("مرودشت", 29.87, 52.80),
        new City("کازرون", 29.62, 51.59),
        new City("آباده", 31.16, 52.65),
        new City("جهرم", 28.50, 53.56),
        new City("لار", 27.67, 54.34),
        // ---------- استان کرمان ----------
        new City("رفسنجان", 30.41, 55.99),
        new City("سیرجان", 29.45, 55.68),
        new City("بم", 29.11, 58.36),
        new City("جیرفت", 28.67, 57.74),
        // ---------- استان یزد ----------
        new City("میبد", 32.24, 54.02),
        new City("اردکان", 32.31, 54.02),
        new City("مهریز", 31.58, 54.47),
        // ---------- استان مرکزی ----------
        new City("ساوه", 35.02, 50.36),
        new City("خمین", 33.64, 50.08),
        new City("محلات", 33.92, 50.45),
        // ---------- استان اردبیل ----------
        new City("پارس‌آباد", 39.65, 47.91),
        new City("مشگین‌شهر", 38.40, 47.67),
        // ---------- آذربایجان شرقی ----------
        new City("مراغه", 37.39, 46.24),
        new City("مرند", 38.43, 45.77),
        new City("میانه", 37.42, 47.71),
        new City("اهر", 38.48, 47.07),
        new City("جلفا", 38.94, 45.62),
        // ---------- آذربایجان غربی ----------
        new City("خوی", 38.55, 44.95),
        new City("مهاباد", 36.77, 45.72),
        new City("بوکان", 36.52, 46.21),
        new City("میاندوآب", 36.97, 46.10),
        new City("نقده", 36.96, 45.39),
        // ---------- استان کردستان ----------
        new City("مریوان", 35.53, 46.18),
        new City("سقز", 36.25, 46.27),
        new City("بانه", 35.99, 45.89),
        new City("قروه", 35.17, 47.80),
        new City("بیجار", 35.88, 47.47),
        // ---------- استان همدان ----------
        new City("ملایر", 34.30, 48.82),
        new City("نهاوند", 34.19, 48.38),
        new City("تویسرکان", 34.55, 48.44),
        // ---------- استان کرمانشاه ----------
        new City("اسلام‌آباد غرب", 34.11, 46.52),
        new City("کنگاور", 34.51, 47.65),
        new City("هرسین", 34.27, 47.06),
        // ---------- استان لرستان ----------
        new City("بروجرد", 33.90, 48.75),
        new City("دورود", 33.50, 49.07),
        new City("الیگودرز", 33.40, 49.70),
        new City("کوهدشت", 33.53, 48.62),
        // ---------- استان ایلام ----------
        new City("دهلران", 33.11, 47.28),
        new City("ایوان", 33.82, 46.29),
        // ---------- استان خوزستان ----------
        new City("آبادان", 30.34, 48.30),
        new City("خرمشهر", 30.44, 48.17),
        new City("دزفول", 32.38, 48.41),
        new City("اندیمشک", 32.46, 48.36),
        new City("مسجدسلیمان", 31.93, 49.30),
        new City("شوشتر", 32.05, 48.85),
        new City("بهبهان", 30.60, 50.24),
        // ---------- چهارمحال و بختیاری ----------
        new City("بروجن", 31.97, 51.29),
        new City("فارسان", 32.26, 50.57),
        // ---------- کهگیلویه و بویراحمد ----------
        new City("گچساران", 30.35, 50.79),
        new City("دهدشت", 30.71, 50.91),
        // ---------- استان بوشهر ----------
        new City("برازجان", 29.26, 51.22),
        new City("گناوه", 29.58, 50.52),
        // ---------- استان هرمزگان ----------
        new City("میناب", 27.09, 57.08),
        new City("قشم", 26.96, 56.27),
        new City("کیش", 26.53, 53.98),
        new City("بندر لنگه", 26.56, 54.88),
        // ---------- استان سمنان ----------
        new City("شاهرود", 36.42, 54.96),
        new City("دامغان", 36.17, 54.34),
        new City("گرمسار", 35.22, 52.33),
        // ---------- سیستان و بلوچستان ----------
        new City("زابل", 31.02, 61.41),
        new City("چابهار", 25.29, 60.64),
        new City("ایرانشهر", 27.20, 60.68),
        new City("خاش", 28.22, 61.21),
        new City("سراوان", 27.37, 62.33)
    ));

    public static List<City> getIranianCities() { return CITIES; }
}
