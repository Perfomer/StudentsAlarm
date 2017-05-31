package managers.firebase;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;

public abstract class FirebaseManager {

    public final static int ERROR_NOT_INITIALIZED_YET = -1;

    protected Context fbContext;
    protected FirebaseAuth fbAuth;

    protected FirebaseManager(Context context) {
        fbContext = context;
    }

    protected abstract boolean auth();
}
