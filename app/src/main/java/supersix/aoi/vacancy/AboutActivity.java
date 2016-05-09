package supersix.aoi.vacancy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //アプリ名取得
        TextView appName = (TextView)findViewById(R.id.app_name);
        int id = getResources().getIdentifier("app_name", "string", getPackageName());
        if (id != 0) {
            appName.setText(getResources().getString(id));
        }
    }
}
