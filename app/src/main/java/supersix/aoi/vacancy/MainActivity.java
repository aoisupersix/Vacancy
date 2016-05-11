package supersix.aoi.vacancy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity{

    private GoogleApiClient client;
    //照会時刻
    private String Year;
    private String Month;
    private String Day;
    private String Hour;
    private String Minute;
    //列車タイプ
    final CharSequence[] Char_traintype = {"のぞみ,さくら,みずほ,さくら,つばめ", "こだま", "はやぶさ,はやて,やまびこ,なすの,つばさ,こまち", "とき,たにがわ,かがやき,はくたか,あさま,つるぎ", "在来線列車"};
    private int traintype = 1;
    //駅名
    private String dep_stn;
    private String arr_stn;
    private String dep_push = "";
    private String arr_push = "";
    //新幹線の駅名
    private ArrayList<String> tokai_stalist = new ArrayList<String>();
    private ArrayList<String> group2_stalist = new ArrayList<String>();
    private ArrayList<String> east_stalist = new ArrayList<String>();
    private ArrayList<String> nagano_stalist = new ArrayList<String>();

    //プッシュコードMap
    private Map<String, String> pushcode = new HashMap<>();
    //HTML
    private String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //日時、時刻を取得
        Calendar _calendar = Calendar.getInstance();
        this.Year = String.valueOf(_calendar.get(Calendar.YEAR));         //年を取得
        this.Month = String.valueOf(_calendar.get(Calendar.MONTH) + 1);       //月を取得
        this.Day = String.valueOf(_calendar.get(Calendar.DATE));         //日を取得
        this.Hour = String.valueOf(_calendar.get(Calendar.HOUR_OF_DAY));         //時を取得
        this.Minute = String.valueOf(_calendar.get(Calendar.MINUTE));    //分を取得

        //デバッグ用設定
//        Month = "5";
//        Day = "10";
//        Hour = "7";
//        Minute = "0";
//        traintype = 5;
//        setDepSta("新宿", "4115");
//        setArrSta("松本", "5400");

        //自動設定
        setDate();
        setDepTime();
        setTrainType();

        //駅リスト読み込み
        tokai_stalist = setSuperexpSta("TokaiStaList.txt");
        group2_stalist = setSuperexpSta("Group2StaList.txt");
        east_stalist = setSuperexpSta("EastStaList.txt");
        nagano_stalist = setSuperexpSta("NaganoStaList.txt");

        try {
            readPushcode();
        }catch(IOException e){
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                       // .setAction("Action", null).show();
                changeDate(view);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.profile) {
            //プロファイルダイアログを開く
            LayoutInflater inflater = this.getLayoutInflater();
            View profileDialogView = inflater.inflate(R.layout.profile_dialog, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(profileDialogView);

            final AlertDialog stadialog = builder.show();

            return true;
        }else if(id == R.id.about_app){
            Intent intent = new Intent();
            intent.setClassName("supersix.aoi.vacancy", "supersix.aoi.vacancy.AboutActivity");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeDate(View v) {
        Calendar _calendar = Calendar.getInstance();
        DatePickerDialog _datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month,
                                          int day) {

                        //設定した日時が1ヶ月以内ならOK!
                        Calendar current = Calendar.getInstance();
                        Log.d("day", String.valueOf(current.get(Calendar.MONTH)));

                        //12月
                        if(current.get(Calendar.MONTH) == 11){
                            if(year == (current.get(Calendar.YEAR) + 1) && (month + 1) == 1 && day <= current.get(Calendar.DAY_OF_MONTH)){
                                Year = String.valueOf(year);
                                Month = String.valueOf(month + 1);
                                Day = String.valueOf(day);
                                setDate();
                            }else{
                                //TODO エラーメッセージ
                            }
                        }else{
                            //12月以外
                            if(year == current.get(Calendar.YEAR) && month < (current.get(Calendar.MONTH) + 2) && month >= current.get(Calendar.MONTH)){
                                if(month == current.get(Calendar.MONTH)){
                                    Year = String.valueOf(year);
                                    Month = String.valueOf(month + 1);
                                    Day = String.valueOf(day);
                                    setDate();
                                }else if(day <= current.get(Calendar.DAY_OF_MONTH)){
                                    if(day == current.get(Calendar.DAY_OF_MONTH)){
                                        if(current.get(Calendar.HOUR_OF_DAY) >= 10){
                                            Year = String.valueOf(year);
                                            Month = String.valueOf(month + 1);
                                            Day = String.valueOf(day);
                                            setDate();
                                        }else{
                                            //TODO エラーメッセージ
                                        }
                                    }else{
                                        Year = String.valueOf(year);
                                        Month = String.valueOf(month + 1);
                                        Day = String.valueOf(day);
                                        setDate();
                                    }
                                }else{
                                    //TODO エラーメッセージ
                                }
                            }else{
                                //TODO エラーメッセージ
                            }
                        }
                    }
                },
                _calendar.get(Calendar.YEAR),
                _calendar.get(Calendar.MONTH),
                _calendar.get(Calendar.DAY_OF_MONTH)
        );
        _datePickerDialog.show();
    }
    public void changeDepTime(View v){
        Calendar _calendar = Calendar.getInstance();
        int hour = _calendar.get(Calendar.HOUR_OF_DAY);
        int minute = _calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute){
                        Hour = String.valueOf(hour);
                        Minute = String.valueOf(minute);
                        setDepTime();
                    }
                },
                hour,minute,true);
        dialog.show();

    }
    public void changeTrainType(View v){

        new AlertDialog.Builder(this)
                .setTitle("列車の種類を選択してください。")
                .setSingleChoiceItems(Char_traintype, traintype - 1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        traintype = item + 1;
                        setTrainType();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    public void showStaDialog(View v){
        final int type = v.getId();

        if(traintype == 5) {
            //在来線の場合、駅名を入力させる
            LayoutInflater inflater = this.getLayoutInflater();
            View staDialogView = inflater.inflate(R.layout.sta_dialog, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(staDialogView)
                    .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            final AlertDialog stadialog = builder.show();

            //ダイアログ内の駅名入力
            final EditText sta_name = (EditText) staDialogView.findViewById(R.id.InputStaname);
            final ListView sta_list = (ListView) staDialogView.findViewById(R.id.StaListView);
            //駅名とプッシュコードのリスト
            final List<String> sta = new ArrayList<String>();
            final List<String> push = new ArrayList<String>();
            sta_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    //駅名を探す
                    String text = sta_name.getText().toString();

                    //リスト削除
                    Removelist(sta_list);
                    sta.clear();
                    push.clear();

                    for (String key : pushcode.keySet()) {
                        if (key.equals(text)) {
                            //完全一致
                            //TODO　今は同じ処理
                            sta.add(key);
                            push.add(pushcode.get(key));
                        } else if (key.startsWith(text)) {
                            //前方一致
                            sta.add(key);
                            push.add(pushcode.get(key));
                        }
                    }
                    //リスト追加
                    Addlist(sta_list, sta);
                }
            });
            sta_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    if (type == R.id.DepStaChange) {
                        //出発駅の場合
                        setDepSta(sta.get(pos), push.get(pos));
                    } else {
                        //到着駅の場合
                        setArrSta(sta.get(pos), push.get(pos));
                    }
                    stadialog.dismiss();
                }
            });
        }else{
            //新幹線の場合、駅名は選択する。

            final String[] items;

            switch(traintype){
                case 1:
                    items = enterStalist(tokai_stalist);
                    break;
                case 2:
                    items = enterStalist(group2_stalist);
                    break;
                case 3:
                    items = enterStalist(east_stalist);
                    break;
                case 4:
                    items = enterStalist(nagano_stalist);
                    break;
                default:
                    //必要ないけどエラーが出るのでとりあえず代入しておく
                    items = enterStalist(tokai_stalist);
            }
            AlertDialog.Builder listDlg = new AlertDialog.Builder(this);
            listDlg.setTitle("駅名を選択してください");
            listDlg.setItems(
                    items,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (type == R.id.DepStaChange) {
                                //出発駅の場合
                                setDepSta(items[which], pushcode.get(items[which]));
                            } else {
                                //到着駅の場合
                                setArrSta(items[which], pushcode.get(items[which]));
                            }
                        }
                    });

            // 表示
            listDlg.create().show();
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://supersix.aoi.vacancy/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://supersix.aoi.vacancy/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    private void readPushcode() throws IOException{

        //Pushcode.csvを読み込み
        AssetManager as = getResources().getAssets();
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = as.open("pushcode.csv");
            br = new BufferedReader(new InputStreamReader(is));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //PushcodeMapに代入
        int n;
        String sta_name, p_code;
        for(String line = ""; (line = br.readLine()) != null; ){
            //駅名切り取り
            n = line.indexOf(",");
            sta_name = line.substring(0, n);
            //プッシュコード切り取り
            p_code = line.substring(n + 1, line.length());
            //pushcodeMapに入れる
            this.pushcode.put(sta_name, p_code);
        }
        br.close();
    }

    private void setDate(){
        TextView DateView = (TextView) findViewById(R.id.DateView);
        if(Integer.parseInt(Month) < 10){
            Month = "0" + Month;
        }
        if(Integer.parseInt(Day) < 10){
            Day = "0" + Day;
        }
        DateView.setText(this.Year + "/" + this.Month + "/" + this.Day);
    }
    private void setDepTime(){
        if(Integer.parseInt(Hour) < 10){
            Hour = "0" + Hour;
        }
        if(Integer.parseInt(Minute) < 10){
            Minute = "0" + Minute;
        }
        TextView DepTimeView = (TextView)findViewById(R.id.DepTimeView);
        DepTimeView.setText(Hour + ":" + Minute);
    }
    private void setTrainType(){
        //新幹線の場合、駅名は選ぶ
        if(traintype == 5){
            BootstrapButton DepStaChange = (BootstrapButton) findViewById(R.id.DepStaChange);
            DepStaChange.setText("入力");
            BootstrapButton ArrStaChange = (BootstrapButton) findViewById(R.id.ArrStaChange);
            ArrStaChange.setText("入力");
        }else{
            BootstrapButton DepStaChange = (BootstrapButton) findViewById(R.id.DepStaChange);
            DepStaChange.setText("選択");
            BootstrapButton ArrStaChange = (BootstrapButton) findViewById(R.id.ArrStaChange);
            ArrStaChange.setText("選択");
        }
        TextView TrainTypeView = (TextView)findViewById(R.id.TrainTypeView);
        TrainTypeView.setText(Char_traintype[traintype - 1]);
    }
    public void setDepSta(String name, String code){
        this.dep_stn = name;
        this.dep_push = code;
        TextView DepStnView = (TextView)findViewById(R.id.DepStnView);
        DepStnView.setText(name);
    }
    public void setArrSta(String name, String code){
        this.arr_stn = name;
        this.arr_push = code;
        TextView ArrStnView = (TextView)findViewById(R.id.ArrStnView);
        ArrStnView.setText(name);
    }
    private void Addlist(ListView list, List<String> str){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for(int i = 0; i < str.size(); i++){
            adapter.add(str.get(i));
        }

        list.setAdapter(adapter);

    }
    private void Removelist(ListView list){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        list.setAdapter(adapter);
    }
    public void setResultHTML(String result){
        this.result = result;
        Log.d("aho", "result=" + result);
        //結果画面へ
        Intent intent = new Intent();
        String access[] = {Year,Month,Day,Hour,Minute,dep_stn,arr_stn,dep_push,arr_push,String.valueOf(traintype)};
        Log.d("aho", "data=" + result);
        intent.putExtra("result", result);
        intent.putExtra("data", access);
        intent.setClassName("supersix.aoi.vacancy", "supersix.aoi.vacancy.ResultActivity");
        startActivity(intent);
    }
    public void Post(View v) {
        if(Year != null && Month != null && Day != null && Hour != null && Day != null && dep_stn != null && arr_stn != null) {
            String REQUEST_URL = "http://www1.jr.cyberstation.ne.jp/csws/Vacancy.do?script=0&month=" + Month + "&day=" + Day + "&hour=" + Hour + "&minute=" + Minute + "&train=" + traintype + "&dep_stn=" + dep_stn + "&arr_stn=" + arr_stn + "&dep_stnpb=" + dep_push + "&arr_stnpb=" + arr_push;
            Log.d("aho", REQUEST_URL);
            try {
                new GetVacancy(this).execute(new URL(REQUEST_URL));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    //ファイルを読み込み、listに代入する
    private ArrayList<String> setSuperexpSta(String file_name){
        ArrayList<String> list = new ArrayList<String>();
        InputStream is = null;
        BufferedReader br = null;

        try {
            try {

                is = this.getAssets().open(file_name);
                br = new BufferedReader(new InputStreamReader(is));

                // １行ずつ読み込む
                String str;
                while ((str = br.readLine()) != null) {
                    list.add(str);
                    Log.d("superexp_train", str);
                }
            } finally {
                if (is != null) is.close();
                if (br != null) br.close();
            }
        } catch (Exception e) {
            // エラー発生時の処理
            Log.d("aho", "LoadTrainName FILED!");
        }
        return list;
    }
    //新幹線の駅名リストを配列に代入
    private String[] enterStalist(ArrayList<String> list){
        String[] result = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            result[i] = list.get(i);
        }

        return result;
    }
}

