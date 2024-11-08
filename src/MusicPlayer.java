import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object(); // Για συγχρονισμό σε παύση/συνέχιση
    private MusicPlayerGUI musicPlayerGUI;  // GUI
    private Song currentSong;  // Τρέχον τραγούδι
    private AdvancedPlayer advancedPlayer;  // AdvancedPlayer
    private boolean isPaused = false;  // Σημαία παύσης
    private int currentFrame = 0;  // Σημερινό frame αναπαραγωγής
    private int currentTimeInMilli = 0;  // Χρόνος σε milliseconds
    private ArrayList<Song> playlist;  // Playlist
    private int currentPlaylistIndex = 0;  // Δείκτης τραγουδιού στην playlist

    // Constructor
    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    // Getter και Setter για frame και χρόνο
    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    public void setCurrentTimeInMilli(int timeInMilli) {
        currentTimeInMilli = timeInMilli;
    }

    // Φόρτωση ενός τραγουδιού
    public void loadSong(Song song) {
        currentSong = song;
        currentFrame = 0;
        currentTimeInMilli = 0;
        if (currentSong != null) {
            playCurrentSong();
        }
    }

    // Φόρτωση playlist από αρχείο
    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(playlistFile))) {
            String songPath;
            while ((songPath = bufferedReader.readLine()) != null) {
                playlist.add(new Song(songPath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (playlist.size() > 0) {
            currentPlaylistIndex = 0;
            loadSong(playlist.get(currentPlaylistIndex));
        }
    }

    // Παύση τραγουδιού
    public void pauseSong() {
        if (advancedPlayer != null && !isPaused) {
            isPaused = true;
            stopSong();
        }
    }

    // Διακοπή αναπαραγωγής
    public void stopSong() {
        if (advancedPlayer != null) {
            try {
                advancedPlayer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            advancedPlayer = null;
        }
    }

    // Αναπαραγωγή τρέχοντος τραγουδιού
    public void playCurrentSong() {
        if (currentSong == null) return;
        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();
            startPlaybackSliderThread();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Νήμα αναπαραγωγής
    private void startMusicThread() {
        new Thread(() -> {
            try {
                if (isPaused) {
                    synchronized (playSignal) {
                        isPaused = false;
                        playSignal.notify();
                    }
                    advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                } else {
                    currentFrame = 0;
                    advancedPlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Νήμα ενημέρωσης slider
    private void startPlaybackSliderThread() {
        new Thread(() -> {
            while (advancedPlayer != null && !isPaused) {
                try {
                    currentTimeInMilli++;
                    int calculatedFrame = (int) ((double) currentTimeInMilli * 2.08 * currentSong.getFrameRatePerMilliseconds());
                    musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Επόμενο τραγούδι στην playlist
    public void nextSong() {
        if (playlist == null || currentPlaylistIndex + 1 >= playlist.size()) return;
        stopSong();
        loadSong(playlist.get(++currentPlaylistIndex));
    }

    // Προηγούμενο τραγούδι στην playlist
    public void prevSong() {
        if (playlist == null || currentPlaylistIndex - 1 < 0) return;
        stopSong();
        loadSong(playlist.get(--currentPlaylistIndex));
    }

    // Έναρξη αναπαραγωγής
    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Playback Started");
    }

    // Τέλος αναπαραγωγής
    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Playback Finished");

        if (!isPaused) {
            currentFrame = 0;
        } else {
            currentFrame += evt.getFrame();
        }
    }
}
