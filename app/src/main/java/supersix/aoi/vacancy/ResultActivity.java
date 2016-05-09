package supersix.aoi.vacancy;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private String result; //HTML
    private String[] data = new String[10];
    //結果
    private List<String> train_name = new ArrayList<String>();
    private List<String> train_deptime = new ArrayList<String>();
    private List<String> train_arrtime = new ArrayList<String>();
    private List<Integer> train_reserved_ns = new ArrayList<Integer>();
    private List<Integer> train_reserved_s = new ArrayList<Integer>();
    private List<Integer> train_green_ns = new ArrayList<Integer>();
    private List<Integer> train_green_s = new ArrayList<Integer>();
    private List<Integer> train_gran = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //データ引き継ぎ
        Intent intent = getIntent();
        result = intent.getStringExtra("result");
        data = intent.getStringArrayExtra("data");

        //入力された情報を表示
        TextView result = (TextView)findViewById(R.id.ResultDataView);
        result.setText(data[0] + "年" + data[1] + "月" + data[2] + "日  " + data[3] + ":" + data[4] + "発  " + data[5] + "(" + data[7] + ") → " + data[6] + "(" + data[8] + ")");

        //HTMLチェック
        short html_type = checkHTML();
        Log.d("aho", "html_type =" + html_type);

        //結果に応じて表示
        TextView DocumentTypeView = (TextView) findViewById(R.id.DocumentTypeView);
        switch(html_type){
            case -1:
                //受付時間外
                DocumentTypeView.setVisibility(View.VISIBLE);
                DocumentTypeView.setText("ただいま、受け付け時間外のため、ご希望の情報の照会はできません。");
                break;
            case 0:
                //照会結果表示
                DocumentTypeView.setVisibility(View.GONE);
                //HTML解析
                searchHTML();

                //リストビュー表示
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
                for(int i = 0; i < train_name.size(); i++){
                    adapter.add(train_name.get(i) + "(" + train_deptime.get(i) + "-" + train_arrtime.get(i) + ")");
                }
                ListView list = (ListView)findViewById(R.id.TrainList);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id){
                        showTrainDialog(pos);
                    }
                });
                break;
            case 1:
                //該当なし
                DocumentTypeView.setVisibility(View.VISIBLE);
                DocumentTypeView.setText("該当区間を運転している空席照会可能な列車はありません。");
                break;
        }

    }
    private void showTrainDialog(int pos){
        //リストビュークリック
        LayoutInflater inflater = this.getLayoutInflater();
        View TrainDialogView = inflater.inflate(R.layout.train_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(TrainDialogView)
                .setNegativeButton("もどる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){

                    }
                });

        final AlertDialog Traindialog = builder.show();
        //結果表示
        TextView train_title = (TextView)TrainDialogView.findViewById(R.id.TrainTitleView);
        ImageView Res_ns = (ImageView)TrainDialogView.findViewById(R.id.res_ns);
        ImageView Res_s = (ImageView)TrainDialogView.findViewById(R.id.res_s);
        ImageView Green_ns = (ImageView)TrainDialogView.findViewById(R.id.gre_ns);
        ImageView Green_s = (ImageView)TrainDialogView.findViewById(R.id.gre_s);
        ImageView Gran = (ImageView)TrainDialogView.findViewById(R.id.gran);

        train_title.setText(train_name.get(pos) + " " + data[5] + "(" + train_deptime.get(pos) + ") → " + data[6] + "(" + train_arrtime.get(pos) + ")");
        setImage(Res_ns,train_reserved_ns.get(pos));
        setImage(Res_s,train_reserved_s.get(pos));
        setImage(Green_ns,train_green_ns.get(pos));
        setImage(Green_s,train_green_s.get(pos));
        setImage(Gran,train_gran.get(pos));

    }
    private void setImage(ImageView v, int type){
        switch(type){
            case -1:
                v.setImageResource(R.mipmap.b_seat_no_l);
                break;
            case 0:
                v.setImageResource(R.mipmap.b_seat_no_l);
                break;
            case 1:
                v.setImageResource(R.mipmap.b_seat_fin_l);
                break;
            case 2:
                v.setImageResource(R.mipmap.b_seat_sankaku_l);
                break;
            case 3:
                v.setImageResource(R.mipmap.b_seat_maru_l);
                break;
        }
    }
    private short checkHTML(){
        /*
        DocumentType:
        -1:受付時間外&混雑中
        0:照会結果あり
        1:照会結果なし
         */
        short DocumentType = 0;
        if(result.indexOf("ただいま、受け付け時間外のため、ご希望の情報の照会はできません。", 0) != -1){
            //受付時間外
            DocumentType = -1;
        }else if(result.indexOf("該当区間を運転している空席照会可能な列車はありません。", 0) != -1){
            //列車なし
            DocumentType = 1;
        }
        return DocumentType;
    }
    private void searchHTML(){
        int position = 0;
        int train_end_position = 0;
        String name_word = "<td align=\"left\">";
        String train_word = "<td align=\"center\">";
        int t_before_length = name_word.length();
        int before_length = train_word.length();

        while((position = result.indexOf(name_word, position) + t_before_length) != -1){
            if(result.charAt(position) == 'グ'){
                //最後
                break;
            }
                /*在来線列車*/
                //列車名
                train_end_position = result.indexOf("<", position);
                train_name.add(result.substring(position, train_end_position));
                Log.d("aho", "trainname=" + train_name.get(train_name.size() - 1));
                //発車時刻
                position = result.indexOf(train_word, train_end_position) + before_length;
                train_deptime.add(result.substring(position, position + 5));
                //到着時刻
                position = result.indexOf(train_word, position) + before_length;
                train_arrtime.add(result.substring(position, position + 5));
                //指定席(禁煙)
                position = result.indexOf(train_word, position) + before_length;
                train_reserved_ns.add(getState(result.substring(position, position + 1)));
                Log.d("aho", result.substring(position, position + 1));
                //指定席(喫煙)
                position = result.indexOf(train_word, position) + before_length;
                train_reserved_s.add(getState(result.substring(position, position + 1)));
                Log.d("aho", result.substring(position, position + 1));
                //グリーン車(禁煙)
                position = result.indexOf(train_word, position) + before_length;
                train_green_ns.add(getState(result.substring(position, position + 1)));
                //グリーン車(喫煙)
                position = result.indexOf(train_word, position) + before_length;
                train_green_s.add(getState(result.substring(position, position + 1)));
                //グランクラス(未設定)
                train_gran.add(-1);
        }
    }

    private int getState(String str){
        int state;
        switch(str){
            case "○":
                state = 3;
                break;
            case "△":
                state = 2;
                break;
            case "×":
                state = 1;
                break;
            case "＊":
                state = 0;
                break;
            default:
                state = -1;
                break;
        }
        return state;
    }

}
