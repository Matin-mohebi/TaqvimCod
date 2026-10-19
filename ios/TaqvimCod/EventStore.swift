import Foundation
import EventKit

/// EventKit bridge — the iPhone calendar is synced with the user's Google
/// account by iOS itself, so reading/writing here IS Google Calendar sync.
final class EventStore: ObservableObject {

    static let shared = EventStore()
    let store = EKEventStore()

    @Published var authorized = false
    @Published var calendars: [EKCalendar] = []

    /// synchronous convenience for SwiftUI callbacks (onAppear / Button action)
    func requestAccessAsync() {
        Task { await requestAccess() }
    }

    /// iOS 17 changed the API; support both.
    func requestAccess() async {
        let granted: Bool
        if #available(iOS 17.0, *) {
            granted = (try? await store.requestFullAccessToEvents()) ?? false
        } else {
            granted = await withCheckedContinuation { cont in
                store.requestAccess(to: .event) { ok, _ in cont.resume(returning: ok) }
            }
        }
        await MainActor.run {
            authorized = granted
            refreshCalendars()
        }
    }

    func refreshCalendars() {
        calendars = store.calendars(for: .event)
            .filter { $0.allowsContentModifications }
    }

    /// expanded instances overlapping [from, to)
    func events(from: Date, to: Date) -> [EKEvent] {
        guard authorized else { return [] }
        let pred = store.predicateForEvents(withStart: from, end: to, calendars: nil)
        let evs = store.events(matching: pred)
        return evs.sorted {
            if $0.isAllDay != $1.isAllDay { return $0.isAllDay }
            return $0.startDate < $1.startDate
        }
    }

    func writableCalendars() -> [EKCalendar] {
        store.calendars(for: .event).filter { $0.allowsContentModifications }
    }

    @discardableResult
    func save(title: String, note: String?, start: Date, end: Date,
              allDay: Bool, calendar: EKCalendar?) -> Bool {
        let ev = EKEvent(eventStore: store)
        ev.title = title
        ev.notes = note
        ev.startDate = start
        ev.endDate = end
        ev.isAllDay = allDay
        ev.calendar = calendar ?? writableCalendars().first ?? store.defaultCalendarForNewEvents
        do {
            try store.save(ev, span: .thisEvent)
            return true
        } catch {
            return false
        }
    }

    @discardableResult
    func update(_ event: EKEvent, title: String, note: String?, start: Date,
                end: Date, allDay: Bool) -> Bool {
        event.title = title
        event.notes = note
        event.startDate = start
        event.endDate = end
        event.isAllDay = allDay
        do {
            try store.save(event, span: .thisEvent)
            return true
        } catch {
            return false
        }
    }

    @discardableResult
    func delete(_ event: EKEvent) -> Bool {
        do {
            try store.remove(event, span: .thisEvent)
            return true
        } catch {
            return false
        }
    }
}
