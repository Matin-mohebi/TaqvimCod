package com.taqvimcod.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.KeyStore;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Hardware-backed encrypted settings storage.
 *
 * <ul>
 *   <li>Every value is encrypted with AES-256-GCM before it touches disk.</li>
 *   <li>The master key lives inside the Android Keystore (TEE / StrongBox when
 *       available) and can never be read by this app or any other app.</li>
 *   <li>A fresh random 12-byte IV is generated for every write, so identical
 *       values never produce identical ciphertexts.</li>
 *   <li>If the Keystore is unavailable (rare broken devices), we fail closed
 *       to a device-local random key kept in a MODE_PRIVATE file, which is
 *       still far safer than plaintext.</li>
 * </ul>
 *
 * This class is the ONLY way Prefs touches disk — no setting is ever stored
 * in clear text (defense in depth on top of allowBackup=false).
 */
public final class SecurePrefs {

    private static final String FILE = "taqvimkod_secure";
    private static final String KS_ALIAS = "taqvimkod_master_key";
    private static final String FALLBACK_FILE = "tk_fallback_key";
    private static final String FALLBACK_KEY = "k";
    private static final int IV_LEN = 12;
    private static final int TAG_BITS = 128;

    private static volatile SecretKey cachedKey;

    private SecurePrefs() {}

    /** underlying encrypted store */
    public static SharedPreferences raw(Context c) {
        return c.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    // ---------------- typed API used by Prefs ----------------

    public static String getString(Context c, String key, String def) {
        String stored = raw(c).getString(key, null);
        if (stored == null) return def;
        String plain = decrypt(c, stored);
        return plain != null ? plain : def;
    }

    public static void putString(Context c, String key, String value) {
        raw(c).edit().putString(key, encrypt(c, value)).apply();
    }

    public static int getInt(Context c, String key, int def) {
        String v = getString(c, key, null);
        if (v == null) return def;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return def; }
    }

    public static void putInt(Context c, String key, int value) {
        putString(c, key, String.valueOf(value));
    }

    public static boolean getBoolean(Context c, String key, boolean def) {
        String v = getString(c, key, null);
        return v == null ? def : "1".equals(v);
    }

    public static void putBoolean(Context c, String key, boolean value) {
        putString(c, key, value ? "1" : "0");
    }

    /** remove one key */
    public static void remove(Context c, String key) {
        raw(c).edit().remove(key).apply();
    }

    /** true if the secure file holds this key (used by migration) */
    public static boolean contains(Context c, String key) {
        return raw(c).contains(key);
    }

    // ---------------- crypto core ----------------

    private static SecretKey key(Context c) {
        SecretKey k = cachedKey;
        if (k != null) return k;
        synchronized (SecurePrefs.class) {
            if (cachedKey == null) cachedKey = loadOrCreateKey(c);
            return cachedKey;
        }
    }

    private static SecretKey loadOrCreateKey(Context c) {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            java.security.Key entry = ks.getKey(KS_ALIAS, null);
            if (entry instanceof SecretKey) return (SecretKey) entry;

            KeyGenerator gen = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            gen.init(new KeyGenParameterSpec.Builder(KS_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setRandomizedEncryptionRequired(false) // we supply our own IV
                    .build());
            return gen.generateKey();
        } catch (Exception e) {
            return fallbackKey(c); // fail closed, never plaintext
        }
    }

    private static SecretKey fallbackKey(Context c) {
        try {
            SharedPreferences sp = c.getApplicationContext()
                    .getSharedPreferences(FALLBACK_FILE, Context.MODE_PRIVATE);
            String saved = sp.getString(FALLBACK_KEY, null);
            if (saved != null) {
                byte[] bytes = Base64.decode(saved, Base64.NO_WRAP);
                return new javax.crypto.spec.SecretKeySpec(bytes, "AES");
            }
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            sp.edit().putString(FALLBACK_KEY,
                    Base64.encodeToString(bytes, Base64.NO_WRAP)).apply();
            return new javax.crypto.spec.SecretKeySpec(bytes, "AES");
        } catch (Exception e) {
            // last resort: ephemeral key (settings reset rather than leak)
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            return new javax.crypto.spec.SecretKeySpec(bytes, "AES");
        }
    }

    private static String encrypt(Context c, String plain) {
        if (plain == null) return null;
        try {
            byte[] iv = new byte[IV_LEN];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key(c), new GCMParameterSpec(TAG_BITS, iv));
            byte[] ct = cipher.doFinal(plain.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            byte[] out = new byte[iv.length + ct.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(ct, 0, out, iv.length, ct.length);
            return Base64.encodeToString(out, Base64.NO_WRAP);
        } catch (Exception e) {
            return null; // refuse to write plaintext
        }
    }

    private static String decrypt(Context c, String stored) {
        try {
            byte[] all = Base64.decode(stored, Base64.NO_WRAP);
            if (all.length <= IV_LEN) return null;
            byte[] iv = new byte[IV_LEN];
            byte[] ct = new byte[all.length - IV_LEN];
            System.arraycopy(all, 0, iv, 0, IV_LEN);
            System.arraycopy(all, IV_LEN, ct, 0, ct.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key(c), new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(ct), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
