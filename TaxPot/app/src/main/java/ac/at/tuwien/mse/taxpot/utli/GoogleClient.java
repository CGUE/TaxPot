package ac.at.tuwien.mse.taxpot.utli;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by markj on 5/3/2017.
 */

public class GoogleClient {

    private static GoogleClient INSTANCE;
    private static Context ctx;
    private RequestQueue requestQueue;

    private GoogleClient(Context ctx){
        this.ctx = ctx;
        requestQueue = getRequestQueue();
    }

    public static synchronized GoogleClient getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new GoogleClient(context);
        }
        return INSTANCE;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
