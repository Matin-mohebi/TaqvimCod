# Google Play Protect and release signing / امنیت انتشار

## What this repository does
- The Android manifest intentionally does not declare `INTERNET`; the app's own code does not call remote services.
- Calendar read/write access is used for the user-requested device-calendar integration. Keep the permission explanation visible and request access only when the user invokes that feature.
- The repository does not contain a release keystore. Release signing is configured only when the developer supplies a private keystore and credentials through GitHub Actions secrets or local Gradle properties.

## If Play Protect warns about an APK
A warning can be based on several signals and cannot be attributed to a single permission or signing setting without the exact Play Protect message and APK hash. **No source-code change or appeal can guarantee a permanent removal of a warning.**

1. Verify the APK came from the expected GitHub Actions run and record its SHA-256 hash.
2. Check the manifest of the built APK and review the permissions actually requested.
3. Do not publish a keystore, private key, keystore password, or key password in the repository. If any real signing key was ever committed publicly, treat it as compromised and rotate it where possible; changing the key may prevent updating existing installs under the same package.
4. Submit the exact warning and APK through Google's official Play Protect appeal/reporting flow. Describe the app accurately and include its source repository. Do not claim that the app has no calendar permissions if the manifest declares them.
5. Test a newly built APK on a clean device. An appeal response time or successful removal cannot be guaranteed.

## Signing notes
- Android signing scheme compatibility depends on supported Android versions and the Android Gradle Plugin. Do not disable v1 signing solely on the assumption that it causes a Play Protect warning.
- Keep release signing secrets in GitHub Actions secrets. Debug builds are for testing and should not be presented as official stable releases.
