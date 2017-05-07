package in.silive.emergency.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.silive.emergency.service.StartAppService;


public class VolumeReceiver extends BroadcastReceiver {
    Context mcontext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mcontext=context;

            Intent in = new Intent(mcontext, StartAppService.class);
            mcontext.startService(in);

    }

}
