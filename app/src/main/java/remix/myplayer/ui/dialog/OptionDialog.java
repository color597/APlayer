package remix.myplayer.ui.dialog;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import remix.myplayer.R;
import remix.myplayer.activities.BaseActivity;
import remix.myplayer.activities.BaseAppCompatActivity;
import remix.myplayer.adapters.AllSongAdapter;
import remix.myplayer.adapters.FolderAdapter;
import remix.myplayer.infos.MP3Info;
import remix.myplayer.ui.customviews.CircleImageView;
import remix.myplayer.utils.Constants;
import remix.myplayer.utils.DBUtil;

/**
 * Created by Remix on 2015/12/6.
 */
public class OptionDialog extends BaseAppCompatActivity {
    private ImageView mAdd;
    private ImageView mRing;
    private ImageView mShare;
    private ImageView mDelete;
    private Button mCancel;
    private TextView mTitle;
    private MP3Info mInfo = null;
    private CircleImageView mCircleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_option);
//        int pos = getIntent().getIntExtra("Position",-1);
//        if(pos > 0 && pos < DBUtil.mAllSongList.size() - 1)
//            mInfo = new MP3Info(DBUtil.getMP3InfoById(DBUtil.mAllSongList.get(pos)));
        mInfo = (MP3Info)getIntent().getExtras().getSerializable("MP3Info");
        if(mInfo == null)
            return;
        mTitle = (TextView)findViewById(R.id.popup_title);
        mTitle.setText(mInfo.getDisplayname() + "-" + mInfo.getArtist());

        mCircleView = (CircleImageView)findViewById(R.id.popup_image);
        ImageLoader.getInstance().displayImage("content://media/external/audio/albumart/" + mInfo.getAlbumId(),
                mCircleView);
        //改变高度，并置于底部
        Window w = getWindow();
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.height = (int) (176 * metrics.densityDpi / 160);
        lp.width = (int) (metrics.widthPixels);
        w.setAttributes(lp);
        w.setGravity(Gravity.BOTTOM | Gravity.END);

        mAdd = (ImageButton)findViewById(R.id.popup_add);
        mRing= (ImageButton)findViewById(R.id.popup_ring);
        mShare = (ImageButton)findViewById(R.id.popup_share);
        mDelete= (ImageButton)findViewById(R.id.popup_delete);
//        mCancel= (Button)findViewById(R.id.popup_cancel);
        //添加到播放列表
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionDialog.this,AddtoPlayListDialog.class);
                Bundle arg = new Bundle();
                arg.putString("SongName",mInfo.getDisplayname());
                arg.putLong("Id",mInfo.getId());
                arg.putLong("AlbumId",mInfo.getAlbumId());
                intent.putExtras(arg);
                startActivity(intent);
                finish();
            }
        });
        //设置手机铃声
        mRing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVoice(mInfo.getUrl(), (int) mInfo.getId());
                finish();
            }
        });
        //分享
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionDialog.this,ShareDialog.class);
                Bundle arg = new Bundle();
                arg.putSerializable("MP3Info",mInfo);
                arg.putInt("Type",Constants.SHARESONG);
                intent.putExtras(arg);
                startActivity(intent);
                finish();
            }
        });
        //删除
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    new AlertDialog.Builder(OptionDialog.this)
//                            .setTitle("确认删除?")
//                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    String result = DBUtil.deleteSong(mInfo.getUrl(), Constants.DELETE_SINGLE) == true ? "删除成功" : "删除失败";
//                                    Toast.makeText(OptionDialog.this,result,Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            })
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            }).create().show();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }

                String result = DBUtil.deleteSong(mInfo.getUrl(), Constants.DELETE_SINGLE) == true ? "删除成功" : "删除失败";
                Toast.makeText(OptionDialog.this,result,Toast.LENGTH_SHORT).show();
                finish();


            }
        });
//        mCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    public void delete() {
        ContentResolver resolver = getContentResolver();
        if(resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,MediaStore.MediaColumns.DATA + "=?",new String[]{mInfo.getUrl()}) > 0)
        {
            //删除播放列表与全部歌曲列表中该歌曲
            for(Long id : DBUtil.mPlayingList)
            {
                if(mInfo.getId() == id)
                {
                    DBUtil.mPlayingList.remove(id);
                    break;
                }
            }
            //删除文件夹列表中该歌曲
            //获得歌曲所在文件夹
//            Iterator it = CommonUtil.mFolderList.keySet().iterator();
//            while(it.hasNext())
//            {
//                String Key = (String)it.next();
//                String Name = mInfo.getUrl().substring(0,mInfo.getUrl().lastIndexOf('/'));
//                if(!Key.equals(Name))
//                    continue;
//                ArrayList<MP3Info> list = CommonUtil.mFolderList.get(Key);
//                for(MP3Info mp3Info : list)
//                {
//                    if(mp3Info.getDisplayname().equals(mInfo.getDisplayname()))
//                    {
//                        list.remove(mp3Info);
//                        break;
//                    }
//                }
//                break;
//            }
            //通知适配器刷新
            AllSongAdapter.mInstance.notifyDataSetChanged();
            if(FolderAdapter.mInstance != null)
                FolderAdapter.mInstance.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(), "删除成功！", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText( getApplicationContext (),"删除失败！", Toast.LENGTH_SHORT ).show();

    }

    private void setVoice(String path,int Id) {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        cv.put(MediaStore.Audio.Media.IS_ALARM, false);
        cv.put(MediaStore.Audio.Media.IS_MUSIC, false);
        // 把需要设为铃声的歌曲更新铃声库
        if(getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cv, MediaStore.MediaColumns.DATA + "=?", new String[]{path}) > 0)
        {
            Uri newUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Id);
            RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText( getApplicationContext (),"设置铃声成功!", Toast.LENGTH_SHORT ).show();
        }
        else
            Toast.makeText( getApplicationContext (),"设置铃声失败!", Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
