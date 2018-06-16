package com.example.simon.easylike;

/**
 * Created by Simon on 5/18/2018.
 */

public class Song {

    private String songName;
    private  int id;
    private String songSinger;
    private  String singerPic;

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSongSinger() {
        return songSinger;
    }

    public void setSongSinger(String songSinger) {
        this.songSinger = songSinger;
    }

    public String getSingerPic() {
        return singerPic;
    }

    public void setSingerPic(String singerPic) {
        this.singerPic = singerPic;
    }
}
