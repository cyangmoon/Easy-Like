package com.example.simon.easylike;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.simon.uiwidgettest.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created by Simon on 5/18/2018.
 */

public class ParseHtml {

    private static String songListUrl;
    private static String liricUrl;

    interface FinishListener{

        abstract void showSongList();

    }

    /**
     * 解析song list HTML数据
     * @param url1 网易云‘’喜欢音乐‘’的网址
     */
    public static void getSongListDataByJsoup(String url1, final FinishListener listener) {
        songListUrl = url1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Song song;
                Document doc = null;
                try {
                    doc = Jsoup.connect(songListUrl).get();    //ParseHtml.url
                    Elements elements_ul, elements_li;
                    String s, subs;
                    elements_ul = doc.select("ul.f-hide");
                    for (Element element1 : elements_ul) {
                        elements_li = element1.getElementsByTag("li");
                        for (Element element : elements_li) {
                            song = new Song();
                            s = element.select("a").attr("href");
                            subs = s.substring(s.indexOf("=") + 1);
                            song.setId(Integer.parseInt(subs));
                            s = element.select("a").text();
                            song.setSongName(s);
               //              song.setSongSinger(element.select("a").attr("data-res-author"));
              //               song.setSingerPic(element.select("a").attr("data-res-pic"));
                            MainActivity.songs.add(song);
                        }
                    }
                    listener.showSongList();
                    MainActivity.getInstance().mHandler.sendEmptyMessage(3);
                } catch (IOException e) {
                    MainActivity.getInstance().editText_search.setHint(R.string.editText_search_hint_no_ID);
                }
            }
        }).start();
    }

    /**
     * 获取歌词
     * @param url
     * @return
     */
    public static File getLyricByJsoup(String url) {
        liricUrl = url;
        File liricFile= null;
        Document doc ;

        try{
            doc = Jsoup.connect(liricUrl).get();
            liricFile = null;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return liricFile;
    }
}
