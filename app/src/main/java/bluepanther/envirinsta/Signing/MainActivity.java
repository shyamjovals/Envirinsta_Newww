package bluepanther.envirinsta.Signing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;

import bluepanther.envirinsta.Services.MyService;
import bluepanther.envirinsta.Timeline.Timeline;


public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);

        startService(new Intent(this,MyService.class));
    Intent i=new Intent(this,Timeline.class);
        i.putExtra("noti","null");
        startActivity(i);

finish();
    }


}
