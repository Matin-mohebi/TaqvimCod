import Foundation

// MARK: - Date holders

struct JDate: Equatable { var y: Int, m: Int, d: Int }
struct GDate: Equatable { var y: Int, m: Int, d: Int }
struct HDate: Equatable { var y: Int, m: Int, d: Int }

/// Accurate conversions: Gregorian ⇄ Jalali (Borkowski astronomical algorithm,
/// port of "jalaali-js") ⇄ Hijri (tabular Kuwaiti algorithm with correction).
/// Direct port of the Android engine — identical results.
enum DateEngine {

    // Swift Int division truncates toward zero, same as the Java/JS original.
    private static func div(_ a: Int, _ b: Int) -> Int { a / b }
    private static func mod(_ a: Int, _ b: Int) -> Int { a % b }
    private static func floorDiv(_ a: Int, _ b: Int) -> Int {
        var q = a / b
        if (a % b != 0) && ((a < 0) != (b < 0)) { q -= 1 }
        return q
    }

    static let breaks = [-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
                         1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178]

    /// returns (leap, gregorianYear, marchDay)
    static func jalCal(_ jy: Int) -> (Int, Int, Int) {
        let bl = breaks.count
        let gy = jy + 621
        var leapJ = -14
        var jp = breaks[0]
        var jump = 0, n = 0
        for i in 1..<bl {
            let jm = breaks[i]
            jump = jm - jp
            if jy < jm { break }
            leapJ += div(jump, 33) * 8 + div(mod(jump, 33), 4)
            jp = jm
        }
        n = jy - jp
        leapJ += div(n, 33) * 8 + div(mod(n, 33) + 3, 4)
        if mod(jump, 33) == 4 && jump - n == 4 { leapJ += 1 }
        let leapG = div(gy, 4) - div((div(gy, 100) + 1) * 3, 4) - 150
        let march = 20 + leapJ - leapG
        if jump - n < 6 { n = n - jump + div(jump + 4, 33) * 33 }
        var leap = mod(mod(n + 1, 33) - 1, 4)
        if leap == -1 { leap = 4 }
        return (leap, gy, march)
    }

    // MARK: Gregorian ⇄ JDN

    static func g2d(_ gy: Int, _ gm: Int, _ gd: Int) -> Int {
        var d = div((gy + div(gm - 8, 6) + 100100) * 1461, 4)
            + div(153 * mod(gm + 9, 12) + 2, 5)
            + gd - 34840408
        d = d - div(div(gy + 100100 + div(gm - 8, 6), 100) * 3, 4) + 752
        return d
    }

    static func d2g(_ jdn: Int) -> GDate {
        var j = 4 * jdn + 139361631
        j += div(div(4 * jdn + 183187720, 146097) * 3, 4) * 4 - 3908
        let i = div(mod(j, 1461), 4) * 5 + 308
        let gd = div(mod(i, 153), 5) + 1
        let gm = mod(div(i, 153), 12) + 1
        let gy = div(j, 1461) - 100100 + div(8 - gm, 6)
        return GDate(y: gy, m: gm, d: gd)
    }

    // MARK: Jalali ⇄ JDN

    static func j2d(jy: Int, jm: Int, jd: Int) -> Int {
        let r = jalCal(jy)
        return g2d(r.1, 3, r.2) + (jm - 1) * 31 - div(jm, 7) * (jm - 7) + jd - 1
    }

    static func d2j(_ jdn: Int) -> JDate {
        let gy = d2g(jdn).y
        var jy = gy - 621
        let r = jalCal(jy)
        let jdn1f = g2d(gy, 3, r.2)
        var k = jdn - jdn1f
        var jm: Int, jd: Int
        if k >= 0 {
            if k <= 185 {
                jm = 1 + div(k, 31)
                jd = mod(k, 31) + 1
                return JDate(y: jy, m: jm, d: jd)
            }
            k -= 186
        } else {
            jy -= 1
            k += 179
            if r.0 == 1 { k += 1 }
        }
        jm = 7 + div(k, 30)
        jd = mod(k, 30) + 1
        return JDate(y: jy, m: jm, d: jd)
    }

    static func isLeapJalaali(_ jy: Int) -> Bool { jalCal(jy).0 == 0 }

    static func jalaaliMonthLength(_ jy: Int, _ jm: Int) -> Int {
        if jm <= 6 { return 31 }
        if jm <= 11 { return 30 }
        return isLeapJalaali(jy) ? 30 : 29
    }

    static func toJalaali(_ g: GDate) -> JDate { d2j(g2d(g.y, g.m, g.d)) }
    static func toGregorian(_ j: JDate) -> GDate { d2g(j2d(jy: j.y, jm: j.m, jd: j.d)) }

    // MARK: Hijri (tabular, adjustable)

    static func hijriFromJdn(_ jdn: Int, adjust: Int) -> HDate {
        var l = jdn - adjust - 1948440 + 10632
        let n = floorDiv(l - 1, 10631)
        l = l - 10631 * n + 354
        let j = floorDiv(10985 - l, 5316) * floorDiv(50 * l, 17719)
            + floorDiv(l, 5670) * floorDiv(43 * l, 15238)
        l = l - (floorDiv(30 - j, 15) * floorDiv(17719 * j, 50)
                 - floorDiv(j, 16) * floorDiv(15238 * j, 43)) + 29
        let m = floorDiv(24 * l, 709)
        let d = l - floorDiv(709 * m, 24)
        let y = 30 * n + j - 30
        return HDate(y: y, m: m, d: d)
    }

    static func hijriMonthLength(_ hm: Int) -> Int { hm % 2 == 1 ? 30 : 29 }

    // MARK: weekday (0 = Saturday … 6 = Friday)

    static func weekday(fromJdn jdn: Int) -> Int {
        let v = (jdn + 2) % 7
        return v < 0 ? v + 7 : v
    }

    // MARK: device day helpers

    static var todayG: GDate {
        let c = Calendar(identifier: .gregorian)
        let comp = c.dateComponents([.year, .month, .day], from: Date())
        return GDate(y: comp.year ?? 2025, m: comp.month ?? 1, d: comp.day ?? 1)
    }

    static var todayJ: JDate { toJalaali(todayG) }

    /// local midnight of the given JDN
    static func startOfDay(jdn: Int) -> Date {
        let g = d2g(jdn)
        var comps = DateComponents()
        comps.year = g.y; comps.month = g.m; comps.day = g.d
        comps.hour = 0; comps.minute = 0; comps.second = 0
        var cal = Calendar(identifier: .gregorian)
        cal.timeZone = TimeZone.current
        return cal.date(from: comps) ?? Date()
    }

    /// JDN of the local day containing `date`
    static func jdn(of date: Date) -> GDate {
        var cal = Calendar(identifier: .gregorian)
        cal.timeZone = TimeZone.current
        let comp = cal.dateComponents([.year, .month, .day], from: date)
        return GDate(y: comp.year ?? 2025, m: comp.month ?? 1, d: comp.day ?? 1)
    }

    static func jdn(ofDate date: Date) -> Int {
        let g = jdn(of: date)
        return g2d(g.y, g.m, g.d)
    }

    static func hourMinute(of date: Date) -> (Int, Int) {
        let comp = Calendar.current.dateComponents([.hour, .minute], from: date)
        return (comp.hour ?? 0, comp.minute ?? 0)
    }
}
