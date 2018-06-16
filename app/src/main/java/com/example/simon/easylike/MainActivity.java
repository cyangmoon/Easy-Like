package com.example.simon.easylike;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simon.uiwidgettest.R;

import java.util.LinkedList;

public class MainActivity extends Activity {

    public static LinkedList<Song> songs = new LinkedList<Song>();

    public static final int PLAT_MUSIC = 1;
    public static final int PAUSE_MUSIC = 2;
    public static final int PREVIOUS_MUSIC = 4;
    public static final int NEXT_MUSIC = 5;
    public static final int LOOP_MUSIC = 6;
    public static int currentPlaySongOrder = 0;
    public static boolean isLoopOne = false;

    public String[] data=null;
    public EditText editText_search;
    private ImageButton imageButton_music_next;
    private ImageButton imageButton_search;
    private ImageButton imageButton_music_previous;
    private ImageButton imageButton_music_play_pause;
    public LinearLayout linearLayout_main_root;
    public ProgressBar progressBar_music_progress;
    public TextView textView_music_time_current;
    public TextView textView_music_time_totle;
    private ImageButton imageButton_music_repeat;
    private ListView listView;

    private static MainActivity instance;
    public static MainActivity getInstance() {
        return instance;
    }

    private ParseHtml.FinishListener listener = new ParseHtml.FinishListener() {
        @Override
        public void showSongList() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showSong();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initComponent();
        ParseHtml.getSongListDataByJsoup("http://music.163.com/playlist/533564713",listener);
        currentPlaySongOrder = 0;
        instance = this;
    }


    /**
     * 获取按键并绑定事件
     */
    private void initComponent() {
        imageButton_music_next = findViewById(R.id.imagebutton_music_next);
        imageButton_music_next.setOnClickListener(onClickListener);
        imageButton_music_previous = findViewById(R.id.imagebutton_music_previous);
        imageButton_music_previous.setOnClickListener(onClickListener);
        imageButton_music_play_pause = findViewById(R.id.imageButton_music_play_pause);
        imageButton_music_play_pause.setOnClickListener(onClickListener);
        editText_search = findViewById(R.id.edittext_search);
        editText_search.setOnClickListener(onClickListener);
        imageButton_search = findViewById(R.id.imageButton_search);
        imageButton_search.setOnClickListener(onClickListener);
        linearLayout_main_root = findViewById(R.id.layout_main_root);
        progressBar_music_progress = findViewById(R.id.progressBar_music_procress);
        textView_music_time_current = findViewById(R.id.textView_music_time_current);
        textView_music_time_totle = findViewById(R.id.textView_music_time_totle);
        imageButton_music_repeat = findViewById(R.id.imageButton_music_repeat);
        imageButton_music_repeat.setOnClickListener(onClickListener);
        listView = findViewById(R.id.list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = ((TextView)view).getText().toString();
                currentPlaySongOrder = Integer.parseInt(s.substring(0,s.indexOf(":")))-1;
                controlMusic(PLAT_MUSIC);
                imageButton_music_play_pause.setImageResource(R.drawable.ic_music_play);
                isPlay=false;
            }
        });

        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    try {
                        int i = Integer.parseInt(editText_search.getText().toString());
                    } catch (NumberFormatException e) {
                        editText_search.setText("");
                        editText_search.setHint(R.string.editText_search_hint_format_fault);
                    }
                    if (editText_search.getText().equals("")) return false;
                    songs.clear();
                    ParseHtml.getSongListDataByJsoup("http://music.163.com/playlist/" + editText_search.getText(),listener);
                    Toast.makeText(MainActivity.this,"处理中，请稍等",Toast.LENGTH_SHORT).show();
                    linearLayout_main_root.setFocusableInTouchMode(false);
                    currentPlaySongOrder = 0;
                }
                return true;
            }
        });

    }

    public void showSong(){
        data = new String[songs.size()];
        for (int m=0;m<songs.size();m++){
            data[m] =( m+1)+":  "+songs.get(m).getSongName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }

    /**
     * 处理界面点击事件
     */
    boolean isPlay = true;
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageButton_music_play_pause:
                    if (isPlay) {
                        ((ImageButton) v).setImageResource(R.drawable.ic_music_play);
                        controlMusic(PLAT_MUSIC);
                        isPlay = false;
                    } else {
                        ((ImageButton) v).setImageResource(R.drawable.ic_music_pause);
                        controlMusic(PAUSE_MUSIC);
                        isPlay = true;
                    }
                    break;
                case R.id.imagebutton_music_previous:
                    // TODO: 5/16/2018
                    PlayingMusicServices.handlerMusic.sendEmptyMessage(1);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    controlMusic(PREVIOUS_MUSIC);
                    imageButton_music_play_pause.setImageResource(R.drawable.ic_music_play);
                    break;
                case R.id.imagebutton_music_next:
                    // TODO: 5/16/2018
                    PlayingMusicServices.handlerMusic.sendEmptyMessage(1);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    controlMusic(NEXT_MUSIC);
                    imageButton_music_play_pause.setImageResource(R.drawable.ic_music_play);
                    break;
                case R.id.imageButton_search:
                    try {
                        int i = Integer.parseInt(editText_search.getText().toString());
                    } catch (NumberFormatException e) {
                        editText_search.setText("");
                        editText_search.setHint(R.string.editText_search_hint_format_fault);
                        break;
                    }
                    if (editText_search.getText().equals("")) break;
                    songs.clear();
                    ParseHtml.getSongListDataByJsoup("http://music.163.com/playlist/"+ editText_search.getText(),listener);
                    Toast.makeText(MainActivity.this,"处理中，请稍等",Toast.LENGTH_SHORT).show();
                    linearLayout_main_root.setFocusableInTouchMode(false);
                    currentPlaySongOrder = 0;
                    break;
                case R.id.imageButton_music_repeat:
                    if (isLoopOne) {
                        imageButton_music_repeat.setImageResource(R.drawable.ic_repeat_all);
                    } else {
                        imageButton_music_repeat.setImageResource(R.drawable.ic_repeat_one);
                    }
                    controlMusic(LOOP_MUSIC);
                    break;
                default:
            }
        }
    };

    /**
     * 打开音乐服务
     *
     * @param type 音乐播放控制类型
     */

    private void controlMusic(int type) {
        Intent intent;
        if (songs != null) {
            intent = new Intent(this, PlayingMusicServices.class);
            intent.putExtra("type", type);
            startService(intent);
        }
    }

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (PlayingMusicServices.getMediaPlayer() != null) {
                int musicTotleTime = PlayingMusicServices.getMediaPlayer().getDuration();
                switch (msg.what){
                    case 0:
                        String totleTime;
                        if((musicTotleTime / 1000 % 60)<10)
                            totleTime = musicTotleTime / 60000 + ":0" + musicTotleTime / 1000 % 60;
                        else
                            totleTime = musicTotleTime / 60000 + ":" + musicTotleTime / 1000 % 60;
                        textView_music_time_totle.setText(totleTime);
                        editText_search.setText("");
                        editText_search.setHint("Playing: "+songs.get(currentPlaySongOrder).getSongName());
                        progressBar_music_progress.setMax(musicTotleTime);
                        break;
                    case 1:
                        updateShow();
                        break;
                    case 2:
                        controlMusic(NEXT_MUSIC);
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this,"处理完成",Toast.LENGTH_SHORT).show();
                        linearLayout_main_root.setFocusableInTouchMode(true);
                        break;
                    default:
                }
            }
        }
    };

    public void updateShow(){
        int musicTotleTime = PlayingMusicServices.getMediaPlayer().getDuration();
        String currentTime;
        int musicCurrentTime = PlayingMusicServices.getMediaPlayer().getCurrentPosition();
        if((musicCurrentTime / 1000 % 60)<10)
            currentTime = musicCurrentTime / 60000 + ":0" + musicCurrentTime / 1000 % 60;
        else
            currentTime = musicCurrentTime / 60000 + ":" + musicCurrentTime / 1000 % 60;
        textView_music_time_current.setText(currentTime);
        progressBar_music_progress.setProgress(musicCurrentTime);
        if(musicCurrentTime >= musicTotleTime&&!PlayingMusicServices.getMediaPlayer().isLooping()) {    //play next if compelech
            controlMusic(NEXT_MUSIC);
        }
    }
}

