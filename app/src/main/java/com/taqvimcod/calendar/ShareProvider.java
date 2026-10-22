package com.taqvimcod.calendar;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Minimal, dependency-free equivalent of androidx FileProvider: serves PNG
 * files written under {cacheDir}/share/ to the system share sheet.
 *
 * The app has zero external dependencies (no androidx), so instead of pulling
 * in the support library we expose the same contract ourselves:
 *   authority  = <package>.share
 *   uri        = content://<package>.share/<fileName>
 *   file_paths.xml documents the served directory (cache/share/).
 *
 * The provider is not exported; read access is granted per-intent via
 * FLAG_GRANT_READ_URI_PERMISSION + ClipData (set by the sharing code).
 */
public class ShareProvider extends ContentProvider {

    public static final String AUTHORITY_SUFFIX = ".share";
    static final String SERVED_DIR = "share";

    public static Uri uriFor(String packageName, String fileName) {
        return Uri.parse("content://" + packageName + AUTHORITY_SUFFIX + "/" + Uri.encode(fileName));
    }

    private File servedRoot() {
        File root = new File(getContext().getCacheDir(), SERVED_DIR);
        if (!root.exists()) root.mkdirs();
        return root;
    }

    /** resolve a uri to a file inside the served directory (path-traversal safe) */
    private File fileFor(Uri uri) throws FileNotFoundException {
        String name = uri.getLastPathSegment();
        if (name == null || name.contains("/") || name.contains("\\") || name.contains(".."))
            throw new FileNotFoundException("Invalid share path");
        File f = new File(servedRoot(), name);
        String canonicalRoot;
        try { canonicalRoot = servedRoot().getCanonicalPath(); }
        catch (IOException e) { throw new FileNotFoundException("Share root unavailable"); }
        try {
            if (!f.getCanonicalPath().startsWith(canonicalRoot + File.separator))
                throw new FileNotFoundException("Invalid share path");
        } catch (IOException e) {
            throw new FileNotFoundException("Invalid share path");
        }
        if (!f.isFile()) throw new FileNotFoundException("No shared file: " + name);
        return f;
    }

    @Override public boolean onCreate() { return true; }

    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        // sharing is read-only; reject anything else
        if (!"r".equals(mode)) throw new FileNotFoundException("Read-only provider");
        return ParcelFileDescriptor.open(fileFor(uri), ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
        String[] cols = projection == null
                ? new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE}
                : projection;
        MatrixCursor cursor = new MatrixCursor(cols, 1);
        File file;
        try { file = fileFor(uri); }
        catch (FileNotFoundException e) { return cursor; }
        Object[] row = new Object[cols.length];
        for (int i = 0; i < cols.length; i++) {
            if (OpenableColumns.DISPLAY_NAME.equals(cols[i])) row[i] = file.getName();
            else if (OpenableColumns.SIZE.equals(cols[i])) row[i] = file.length();
            else row[i] = null;
        }
        cursor.addRow(row);
        return cursor;
    }

    @Override public String getType(Uri uri) {
        String name = uri.getLastPathSegment();
        if (name != null && name.endsWith(".png")) return "image/png";
        if (name != null && name.endsWith(".jpg")) return "image/jpeg";
        return "application/octet-stream";
    }

    @Override public Uri insert(Uri uri, ContentValues values) { return null; }

    @Override public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }

    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
