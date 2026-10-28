package es.gsmsmart.reparaciones;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(GSMNativePlugin.class);
        super.onCreate(savedInstanceState);
    }
}
