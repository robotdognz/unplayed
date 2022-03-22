package misc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FileChooser {
    private static final int SELECT_FILE = 1;
    private static final int SELECT_FILES = 2;

    private final Activity activity;
//    private final Context context;

    private Uri uri;
    private ArrayList<Uri> uris;

    public FileChooser(Activity activity) {
        this.activity = activity;
//        context = activity.getApplicationContext();
    }

    public void setUri(Uri uri) {
        this.uri = uri;
        this.uris = null;
    }

    public void setUris(ArrayList<Uri> uris) {
        this.uris = uris;
        this.uri = null;
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

    public Uri getUri() {
        return uri;
    }

    public ArrayList<Uri> getUris() {
        return uris;
    }

    // open file saver
    public void createSaveFile() {
        uri = null; // remove uri
        uris = null; // remove uris

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("*/*");
        // make default file name with date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        intent.putExtra(Intent.EXTRA_TITLE, currentDate + " .unplayed");

        activity.startActivityForResult(Intent.createChooser(intent, "Select Level"), SELECT_FILE);
    }

    // open file loader
    public void createLoadFile() {
        uri = null; // remove uri
        uris = null; // remove uris

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
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
}
