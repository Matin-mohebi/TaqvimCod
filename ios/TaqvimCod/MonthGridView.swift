import SwiftUI

/// Jalali month grid: Saturday-first, RTL, soft circular badges,
/// gradient "today" medallion, holiday tint, event dots.
struct MonthGridView: View {
    let jy: Int
    let jm: Int
    let selectedJd: Int
    let today: JDate
    let eventDays: Set<Int>
    let scheme: ColorScheme
    let onTapDay: (Int) -> Void
    let onSwipe: (Bool) -> Void

    private var c: (bg: Color, surface: Color, surfaceAlt: Color, text: Color, textSub: Color,
                    accent: Color, accentSoft: Color, gradStart: Color, gradEnd: Color,
                    red: Color, redSoft: Color, amber: Color, green: Color) { Pal.colors(scheme) }

    private var offset: Int {
        DateEngine.weekday(fromJdn: DateEngine.j2d(jy: jy, jm: jm, jd: 1))
    }
    private var monthLen: Int { DateEngine.jalaaliMonthLength(jy, jm) }
    private var rows: [[Int?]] {
        var grid: [[Int?]] = []
        var row: [Int?] = Array(repeating: nil, count: offset)
        for d in 1...monthLen {
            row.append(d)
            if row.count == 7 { grid.append(row); row = [] }
        }
        if !row.isEmpty { grid.append(row + Array(repeating: nil, count: 7 - row.count)) }
        return grid
    }

    private let cols = Array(repeating: GridItem(.flexible(), spacing: 2), count: 7)

    var body: some View {
        VStack(spacing: 0) {
            // weekday header
            HStack(spacing: 2) {
                ForEach(0..<7, id: \.self) { i in
                    Text(L.wds[i])
                        .font(.custom(FontNames.medium, size: 12.5))
                        .foregroundColor(i == 6 ? c.red : c.textSub)
                        .frame(maxWidth: .infinity)
                }
            }
            .padding(.bottom, 6)
            Rectangle().fill(c.surfaceAlt).frame(height: 1)

            ForEach(Array(rows.enumerated()), id: \.offset) { _, row in
                HStack(spacing: 2) {
                    ForEach(Array(row.enumerated()), id: \.offset) { _, cell in
                        ZStack {
                            if let jd = cell {
                                DayBadge(jd: jd,
                                         isToday: jy == today.y && jm == today.m && jd == today.d,
                                         isSelected: jd == selectedJd,
                                         isHoliday: Holidays.isHoliday(jm: jm, jd: jd,
                                                                       hm: hijri(jd).m, hd: hijri(jd).d),
                                         hasEvent: eventDays.contains(jd),
                                         isFriday: colIndex(jd) == 6,
                                         scheme: scheme)
                            }
                        }
                        .frame(maxWidth: .infinity, minHeight: 46)
                        .contentShape(Rectangle())
                        .onTapGesture { if let jd = cell { onTapDay(jd) } }
                    }
                }
            }
        }
        .gesture(
            DragGesture(minimumDistance: 30).onEnded { g in
                if abs(g.translation.width) > abs(g.translation.height) {
                    onSwipe(g.translation.width < 0) // swipe left = next month
                }
            }
        )
    }

    private func colIndex(_ jd: Int) -> Int { (offset + jd - 1) % 7 }
    private func hijri(_ jd: Int) -> HDate {
        DateEngine.hijriFromJdn(DateEngine.j2d(jy: jy, jm: jm, jd: jd),
                                adjust: UserDefaults.standard.integer(forKey: "hijri_adj") )
    }
}

private struct DayBadge: View {
    let jd: Int
    let isToday: Bool
    let isSelected: Bool
    let isHoliday: Bool
    let hasEvent: Bool
    let isFriday: Bool
    let scheme: ColorScheme

    var body: some View {
        let c = Pal.colors(scheme)
        VStack(spacing: 3) {
            ZStack {
                if isToday {
                    LinearGradient(colors: [c.gradStart, c.gradEnd],
                                   startPoint: .topLeading, endPoint: .bottomTrailing)
                        .clipShape(Circle())
                } else if isSelected {
                    Circle().fill(c.accentSoft).overlay(Circle().stroke(c.accent, lineWidth: 1.6))
                } else if isHoliday {
                    Circle().fill(c.redSoft)
                }
                Text(L.d(jd))
                    .font(.custom(FontNames.semibold, size: 17))
                    .foregroundColor(isToday ? .white
                        : (isHoliday || isFriday) ? c.red
                        : isSelected ? c.accent : c.text)
            }
            .frame(width: 34, height: 34)
            HStack(spacing: 3) {
                if hasEvent { Circle().fill(c.accent).frame(width: 4.5, height: 4.5) }
                if isHoliday { Circle().fill(c.red).frame(width: 4.5, height: 4.5) }
            }
            .frame(height: 5)
        }
    }
}

/// bundled font names (registered via Info.plist UIAppFonts)
enum FontNames {
    static let regular = "Vazirmatn-Regular"
    static let medium = "Vazirmatn-Medium"
    static let semibold = "Vazirmatn-SemiBold"
    static let bold = "Vazirmatn-Bold"
}
