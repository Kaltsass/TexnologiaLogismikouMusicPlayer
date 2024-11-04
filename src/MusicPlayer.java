import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {
    private Song currentSong;
    //creating AdvancedPlayer
    private AdvancedPlayer advancedPlayer;
    //pauses boolean flag
    private boolean isPaused;
    //pausing resuming
    private int currentFrame;
    // playlist index
    private int currentPlaylistIndex;

    private ArrayList<Song> playlist;

    //constructor
    public MusicPlayer(){

    }
    public void loadSong(Song song){
        currentSong = song;

        if(currentSong != null){
            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile){
        playlist = new ArrayList<>();

        // store the paths from the text file into the playlist array list
        try{
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            // reach each line from the file and store the text int the songPath variable
            String songPath;
            while((songPath = bufferedReader.readLine()) !=null){
                // create song object based on song path
                Song song = new Song(songPath);

                // add to playlist array list
                playlist.add(song);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(playlist.size() > 0){


            //update current song to the first song in the playlist
            currentSong = playlist.get(0);

            //start from the beginning frame
            currentFrame = 0;


            //start song
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
    public void nextSong(){
        if (playlist == null)return;
        // check if we reached the end of the playlist, if so don't do anything
        if(currentPlaylistIndex + 1 > playlist.size() - 1) return;

        //stop the song
        stopSong();

        // increase playlist index
        currentPlaylistIndex++;

        // update current song
        currentSong = playlist.get(currentPlaylistIndex);

        //reset frame
        currentFrame = 0 ;



        playCurrentSong();

    }

    public void prevSong(){
        if (playlist == null)return;

        // check to see if we can go to previous song
        if(currentPlaylistIndex - 1 < 0) return;

        //stop the song
        stopSong();

        // decrease playlist index
        currentPlaylistIndex--;

        // update current song
        currentSong = playlist.get(currentPlaylistIndex);

        //reset frame
        currentFrame = 0 ;


        playCurrentSong();
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

