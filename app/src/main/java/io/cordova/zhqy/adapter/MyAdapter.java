package io.cordova.zhqy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import io.cordova.zhqy.R;
import io.cordova.zhqy.bean.OAMsgListBean;
import io.cordova.zhqy.utils.CircleCrop;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;

import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;

/**
 * Created by Administrator on 2019/4/17 0017.
 */

public class MyAdapter extends CommonAdapter<OAMsgListBean.ObjBean> {
    Context mContext;
    public MyAdapter(Context context, int layoutId, List<OAMsgListBean.ObjBean> datas) {
        super(context, layoutId, datas);
        mContext = context;
    }

    @Override
    protected void convert(ViewHolder holder, final OAMsgListBean.ObjBean s, int position) {

        holder.setText(R.id.tv_name,s.getYwlx());
        holder.setTextColor(R.id.tv_name, Color.parseColor("#000000"));
        holder.setText(R.id.tv_present,s.getTitle());
        holder.setTextColor(R.id.tv_present,Color.parseColor("#000000"));

        ImageView iv = holder.getConvertView().findViewById(R.id.oa_img);
        switch (position%6) {
            case 0:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon2)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 1:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon1)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 2:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon2)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 3:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon4)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 4:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon3)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 5:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon5)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
        }
        holder.setOnClickListener(R.id.ll_msg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                }else {
                    intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                }
                intent.putExtra("appUrl",s.getTodourl());
                intent.putExtra("oaMsg","oaMsg");
                intent.putExtra("appName",s.getYwlx());
                mContext.startActivity(intent);
            }
        });
    }

}
