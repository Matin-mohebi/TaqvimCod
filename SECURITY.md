# Security Policy — تقویم / سیاست امنیتی

## Supported versions / نسخه‌های پشتیبانی‌شده

| Version | Supported |
|---------|-----------|
| 1.1.x   | ✅ |
| 1.0.x   | ⚠️ security fixes only / فقط اصلاحات امنیتی |

## Security features / امکانات امنیتی

- **No INTERNET permission** — the app itself cannot make direct network requests. Calendar-provider sync (for example, Google account sync) may still upload events saved to a cloud-backed calendar according to the user’s account/device settings.
- Settings encrypted with **AES-256-GCM**, key stored with **Android Keystore** when available; a software fallback has a lower protection level and is used only if Keystore initialization fails
- `allowBackup=false`, cloud backup & device-transfer excluded via `dataExtractionRules`
- `network_security_config`: cleartext traffic denied
- Optional **FLAG_SECURE** (screenshot/recents protection) in Settings
- Release builds: **R8** shrink + obfuscation, `debuggable=false`; APK signing is enabled only when a private keystore and credentials are configured outside source control
- Minimum attack surface: only 3 permissions (READ_CALENDAR, WRITE_CALENDAR, RECEIVE_BOOT_COMPLETED)
- Widget receiver is non-exported; every provider call is permission-checked; text inputs are length-clamped
- On iOS: all data stays between the device and the user's own calendar via EventKit; no network calls by the app itself

## Reporting a vulnerability / گزارش آسیب‌پذیری

<div dir="rtl">

اگر آسیب‌پذیری امنیتی پیدا کردی، لطفاً **از طریق Issues عمومی گزارش نده**. به یکی از راه‌های زیر اطلاع بده:

- ایمیل/تماس: 09195394891 (تلفن یا واتساپ)
- اینستاگرام: [@matin_web_site](https://instagram.com/matin_web_site)
- سایت: https://matinzeus.ir

تا ۷۲ ساعت پاسخ می‌دم و بعد از رفع، اعتبار گزارش به نامت ثبت می‌شه. ممنون از همکاری‌ت 🙏

</div>

Please **do not report security vulnerabilities through public GitHub issues**.
Use the private contact channels above (phone/WhatsApp 09195394891, Instagram, or matinzeus.ir).
You will receive a response within 72 hours.
