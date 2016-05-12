package supersix.aoi.vacancy;
import java.util.List;

import supersix.aoi.vacancy.R;
import supersix.aoi.vacancy.Listitem;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

        // テキストをセット
        TextView appInfoText = (TextView)view.findViewById(R.id.listrow_TextView);
        appInfoText.setText(item.getText());

        // アイコンをセット
        ImageView appInfoImage = (ImageView)view.findViewById(R.id.listrow_ImageView);
        appInfoImage.setImageResource(item.getImageId());

        // XMLで定義したアニメーションを読み込む
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.item_motion);
        // リストアイテムのアニメーションを開始
        view.startAnimation(anim);

        return view;
    }
}