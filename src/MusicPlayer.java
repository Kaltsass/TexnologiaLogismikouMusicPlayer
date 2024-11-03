import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();
    private MusicPlayerGUI musicPlayerGUI;
    private Song currentSong;
    private AdvancedPlayer advancedPlayer;
    private boolean isPaused = false;
    private int currentFrame = 0;
    private int currentTimeInMilli = 0;

    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    public void setCurrentTimeInMilli(int timeInMilli) {
        currentTimeInMilli = timeInMilli;
    }

    public void loadSong(Song song) {
        currentSong = song;
        currentFrame = 0;
        currentTimeInMilli = 0;
        if (currentSong != null) {
            playCurrentSong();
        }
    }

    public void pauseSong() {
        if (advancedPlayer != null && !isPaused) {
            isPaused = true;
            stopSong();
        }
    }

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

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Playback Started");
    }

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
