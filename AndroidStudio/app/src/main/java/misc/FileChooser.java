package misc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import processing.core.PApplet;

public class FileChooser {
    private static final int SELECT_FILE = 1;
    private static final int SELECT_FILES = 2;

    private Activity activity;
    private Context context;

    private Uri uri;
    private ArrayList<Uri> uris;

    public FileChooser(Activity activity) {
        this.activity = activity;
        context = activity.getApplicationContext();
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setUris(ArrayList<Uri> uris) {
        this.uris = uris;
    }

    public void removeUri() {
        uri = null;
        uris = null;
    }

    public boolean hasUri() {
        return uri != null;
    }

    public boolean hasUris() {
        return uris != null;
    }

    public Uri getUri(){
        return uri;
    }

    public ArrayList<Uri> getUris(){
        return uris;
    }

//    public String getPath() {
//        if (uri != null) {
//            String path = getPathFromUri(context, uri);
//            PApplet.print("Got path from URI: " + path);
//            return path;
//        }
//        return "";
//    }

    // open file saver
    public void createSaveFile() {
        uri = null; // remove uri
        uris = null; // remove uris

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        // intent.setType("application/json");
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "level.unplayed");
        activity.startActivityForResult(Intent.createChooser(intent, "Select Level"), SELECT_FILE);
    }

    // open file loader
    public void createLoadFile() {
        uri = null; // remove uri
        uris = null; // remove uris

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // intent.setType("application/json");
        intent.setType("*/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Select Level"), SELECT_FILE);
    }

    // open files loader
    public void createLoadFiles() {
        uri = null; // remove uri
        uris = null; // remove uris

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Level"), SELECT_FILES);
    }

    // ----------convert uri to correct file path-----------

    @SuppressLint("NewApi")
    public static String getPathFromUri(final Context context, final Uri uri) {

//		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // TODO: can probably remove the code for kitkat, old af and our app needs newer phones

        // check if document uri
        if (DocumentsContract.isDocumentUri(context, uri)) {
            PApplet.print("Is Document URI");
            String uriAuthority = uri.getAuthority();
            PApplet.print("Authority: " + uriAuthority);
        }

        // check if it is a file uri
        String uriSchema = uri.getScheme();
        if ("file".equalsIgnoreCase(uriSchema)) {
            PApplet.print("Is File URI");
        }

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) { //isKitKat && ...
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
