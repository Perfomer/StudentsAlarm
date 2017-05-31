package activities;

import android.app.Application;
import android.widget.Toast;

import com.drivemode.android.typeface.TypefaceHelper;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicationWrapper extends Application {

    public static final String
            FONT_MAIN_REGULAR = "fonts/bender_regular.otf",
            FONT_MAIN_LIGHT = "fonts/bender_light.otf";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        TypefaceHelper.initialize(this);
    }

}
