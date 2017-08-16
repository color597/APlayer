package remix.myplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.BindView;
import remix.myplayer.R;
import remix.myplayer.adapter.holder.BaseViewHolder;
import remix.myplayer.theme.Theme;
import remix.myplayer.theme.ThemeStore;
import remix.myplayer.util.DensityUtil;
import remix.myplayer.util.SPUtil;

/**
 * Created by Remix on 2017/8/15.
 */

public class FloatColorAdapter extends BaseAdapter<FloatColorAdapter.FloatColorHolder> {
    private List<Integer> mColorList;
    //当前桌面歌词的字体颜色 默认为当前主题颜色
    private int mCurrentColor;
    private int mItemWidth;
    public FloatColorAdapter(Context Context,int width) {
        super(Context);
        mCurrentColor = SPUtil.getValue(mContext,"Setting", SPUtil.SPKEY.FLOAT_TEXT_COLOR,ThemeStore.getThemeColor());
        mColorList = ThemeStore.getAllThemeColor();
        mItemWidth = width / ThemeStore.getAllThemeColor().size();
        //宽度太小
        if(mItemWidth < DensityUtil.dip2px(mContext,20))
            mItemWidth = DensityUtil.dip2px(mContext,20);
    }

    /**
     * 判断是否是选中的颜色
     * @param color
     * @return
     */
    private boolean isColorChoose(int color){
        return color == mCurrentColor;
    }

    public void setCurrentColor(int theme){
        mCurrentColor = theme;
        SPUtil.putValue(mContext,"Setting", SPUtil.SPKEY.FLOAT_TEXT_COLOR,mCurrentColor);
    }

    @Override
    public int getItemCount() {
        return mColorList != null ? mColorList.size() : 0;
    }

    @Override
    public FloatColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FloatColorHolder holder = new FloatColorHolder(LayoutInflater.from(mContext).inflate(R.layout.item_float_lrc_color,parent,false));

        RelativeLayout.LayoutParams imgLayoutParam = new RelativeLayout.LayoutParams(DensityUtil.dip2px(mContext,18),DensityUtil.dip2px(mContext,18));
        imgLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        holder.mColor.setLayoutParams(imgLayoutParam);

        RecyclerView.LayoutParams rootLayoutParam = new RecyclerView.LayoutParams(mItemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        holder.mRoot.setLayoutParams(rootLayoutParam);

        return holder;
    }


    @Override
    public void onBindViewHolder(FloatColorHolder holder, final int position) {
        final int themeColor = mColorList.get(position);
        if(isColorChoose(themeColor)){
            holder.mColor.setBackground(Theme.getShape(GradientDrawable.OVAL, ThemeStore.getThemeColorInt(themeColor),0, DensityUtil.dip2px(mContext,1),Color.BLACK,
                    DensityUtil.dip2px(mContext,18),DensityUtil.dip2px(mContext,18),1));
        } else {
            holder.mColor.setBackground(Theme.getShape(GradientDrawable.OVAL,ThemeStore.getThemeColorInt(themeColor),DensityUtil.dip2px(mContext,18),DensityUtil.dip2px(mContext,18)));
        }
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(v,position);
            }
        });
//        holder.mColorBg.setVisibility(isColorChoose(mColorList.get(position)) ? View.VISIBLE : View.GONE);
    }

    static class FloatColorHolder extends BaseViewHolder {
        @BindView(R.id.item_color)
        ImageView mColor;
//        @BindView(R.id.item_color_bg)
//        SimpleDraweeView mColorBg;
        public FloatColorHolder(View itemView) {
            super(itemView);
        }
    }
}
