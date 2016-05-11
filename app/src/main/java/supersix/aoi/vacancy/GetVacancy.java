package supersix.aoi.vacancy;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetVacancy extends AsyncTask<URL, Void, String> {

    public static String ReturnData = null;
    public MainActivity _mainAct;
    public ProgressDialog m_ProgressDialog;

    public GetVacancy(MainActivity mainAct){
        _mainAct = mainAct;
    }

    @Override
    protected void onPreExecute() {

        // プログレスダイアログの生成
        this.m_ProgressDialog = new ProgressDialog(_mainAct);

        // プログレスダイアログの設定
        this.m_ProgressDialog.setMessage("照会中...");  // メッセージをセット

        // プログレスダイアログの表示
        this.m_ProgressDialog.show();

        return;
    }

    @Override
    protected String doInBackground(URL...urls) {
        // 取得したテキストを格納する変数
        final StringBuilder result = new StringBuilder();
        // アクセス先URL
        final URL url = urls[0];

        HttpURLConnection con = null;
        try {
            // ローカル処理
            // コネクション取得
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Referer", "http://www1.jr.cyberstation.ne.jp/csws/Vacancy.do");
            con.connect();

            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            Log.d("aho", String.valueOf(status));
            if (status == HttpURLConnection.HTTP_OK) {
                Log.d("connect", "通信成功");
                // 通信に成功した
                // テキストを取得する
                final InputStream in;
                if(status / 100 == 4 || status / 100 == 5){
                    in = con.getErrorStream();
                }else{
                    in = con.getInputStream();
                }
                //final String encoding = con.getContentEncoding();
                Log.d("connect", String.valueOf(status));
                final InputStreamReader inReader = new InputStreamReader(in, "SJIS");
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = null;
                // 1行ずつテキストを読み込む
                while((line = bufReader.readLine()) != null) {
                    result.append(line);
                    Log.d("connect", line);
                    ReturnData += line;
                }
                bufReader.close();
                inReader.close();
                in.close();

                // プログレスダイアログを閉じる
                if (this.m_ProgressDialog != null && this.m_ProgressDialog.isShowing()) {
                    this.m_ProgressDialog.dismiss();
                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ProtocolException e1) {
            Log.d("aho", "ProtocolException" + e1);
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }
        ReturnData = result.toString();
        return result.toString();
    }
    @Override
    protected void onPostExecute(String result) {
        _mainAct.setResultHTML(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (this.m_ProgressDialog != null) {

            // プログレスダイアログ表示中の場合
            if (this.m_ProgressDialog.isShowing()) {

                // プログレスダイアログを閉じる
                this.m_ProgressDialog.dismiss();
            }
        }

        return;
    }

}
