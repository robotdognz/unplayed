package misc;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class FileChooser {
	private static final int SELECT_FILE = 1;
	private static final int SELECT_FOLDER = 2;

	private Activity activity;
	private Context context;

	private Uri uri;

	public FileChooser(Activity activity) {
		this.activity = activity;
		context = activity.getApplicationContext();
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public void removeUri() {
		uri = null;
	}

	public boolean hasUri() {
		if (uri != null) {
			return true;
		} else {
			return false;
		}
	}

	public String getPath() {
		if (uri != null) {
			String path = getPathFromUri(context, uri);
			return path;
		}
		return "";
	}

	// open file saver
	public void createSaveFile() {
		uri = null; // remove uri

		Intent intent = new Intent();
		// intent.setType("application/json");
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_CREATE_DOCUMENT);
		activity.startActivityForResult(Intent.createChooser(intent, "Select Level"), SELECT_FILE);
	}

	// open file loader
	public void createLoadFile() {
		uri = null; // remove uri

		Intent intent = new Intent();
		// intent.setType("application/json");
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(Intent.createChooser(intent, "Select Level"), SELECT_FILE);
	}

	// open folder loader
	public void createLoadFolder() {
		uri = null; // remove uri

		Intent intent = new Intent();
		// intent.setType("application/json");
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
		activity.startActivityForResult(Intent.createChooser(intent, "Select Folder"), SELECT_FOLDER);
	}

	// ----------convert uri to correct file path-----------

	@SuppressWarnings("deprecation")
	public static String getPathFromUri(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// TODO: can probably remove the code for kitkat, old af and our app needs newer
		// phones

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
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
				final String[] selectionArgs = new String[] { split[1] };

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
		final String[] projection = { column };

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
