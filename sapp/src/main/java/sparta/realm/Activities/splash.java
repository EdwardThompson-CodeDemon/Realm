package sparta.realm.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import sparta.realm.MainActivity;
import sparta.realm.R;
import sparta.realm.spartaservices.sdbw;


public class splash extends SpartaAppCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


new Thread(new Runnable() {
    @Override
    public void run() {
     //   ((ImageView)findViewById(R.id.c_logo)).setImageDrawable(getDrawable(R.drawable.cs));
        /*try {
           // Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        sd=new sdbw(act);
        sd.load_employee("0");
        finish();
        startActivity(new Intent(act, MainActivity.class));
    }
}).start();

    }

}
