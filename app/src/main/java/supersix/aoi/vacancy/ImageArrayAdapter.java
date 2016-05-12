package supersix.aoi.vacancy;
import java.util.List;

import supersix.aoi.vacancy.R;
import supersix.aoi.vacancy.Listitem;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.BootstrapThumbnail;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;

import org.w3c.dom.Text;

public class ImageArrayAdapter extends ArrayAdapter<Listitem> {

    private List<Listitem> items;
    private LayoutInflater inflater;

    public ImageArrayAdapter(Context context, int resourceId, List<Listitem> items) {
        super(context, resourceId, items);

        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = this.inflater.inflate(R.layout.item_layout, null);
        }

        Listitem item = this.items.get(position);

        //タイトルをセット
        TextView TitleText = (TextView) view.findViewById(R.id.listrow_TitleView);
        TitleText.setText(item.getTitle());

        // テキストをセット
        TextView appInfoText = (TextView) view.findViewById(R.id.listrow_TextView);
        appInfoText.setText(item.getText());

        // アイコンをセット
        BootstrapThumbnail appInfoImage = (BootstrapThumbnail) view.findViewById(R.id.listrow_ImageView);
        appInfoImage.setImageResource(item.getImageId());

        //色を変える
        LinearLayout bg = (LinearLayout)view.findViewById(R.id.bg);

        if(position%2==0){
            bg.setBackgroundColor(Color.parseColor("#CCEECC"));
        }else {
            bg.setBackgroundColor(Color.parseColor("#99EE99"));
        }

        // XMLで定義したアニメーションを読み込む
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.item_motion);
        // リストアイテムのアニメーションを開始
        view.startAnimation(anim);

        return view;
    }
}