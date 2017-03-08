package bluepanther.envirinsta.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import bluepanther.envirinsta.Signing.Sign_In;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shyam on 10-Feb-17.
 */

public class Preferences {
    public static void getPrefs(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        if(pref.getBoolean("islog",false))
        {
            new CurrentUser(pref.getString("user",null),pref.getString("pass",null),pref.getString("sclass",null),pref.getString("ssec",null),pref.getString("idate",null),pref.getString("adate",null),pref.getString("vdate",null),pref.getString("fdate",null),pref.getString("tdate",null));

        }else
        {
            Intent intent = new Intent(context, Sign_In.class);
            context.startActivity(intent);

        }
    }
}
