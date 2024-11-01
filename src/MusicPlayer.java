import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    private Song currentSong;
    //creating AdvancedPlayer
    private AdvancedPlayer advancedPlayer;
    //pauses boolean flag
    private boolean isPaused;
    //pausing resuming
    private int currentFrame;

    //constructor
    public MusicPlayer(){

    }
    public void loadSong(Song song){
        currentSong = song;

        if(currentSong != null){
            playCurrentSong();
        }
    }
    //flag
    public void pauseSong(){
        if(advancedPlayer != null){
            //updating flag
            isPaused = true;
            //stopping the mp3 player
            stopSong();
        }
    }

    public void stopSong(){
        if(advancedPlayer!=null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer=null;
        }
    }

    public void playCurrentSong() {
        if(currentSong == null)return;
        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);


            //advanced player

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);


            //start music

            startMusicThread();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void    startMusicThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if(isPaused){
                        //resume music from last frame
                        advancedPlayer.play(currentFrame,Integer.MAX_VALUE);
                    }else{
                        //reset music
                        advancedPlayer.play();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        //called in the beginning of the song

        System.out.println("Playback Started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        //gets called when the song finishes or if the player gets called
        System.out.println("Playback Finished");


        if(isPaused){
            currentFrame+= (int)((double)evt.getFrame()*currentSong.getFrameRatePerMilliseconds());

        }
    }
}
