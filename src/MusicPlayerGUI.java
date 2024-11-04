import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;
    private JFileChooser jFileChooser;
    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;

    public MusicPlayerGUI() {
        super("Music Player");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("src/assets"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        addGUIComponents();
    }

    private void addGUIComponents() {
        addToolbar();
        JLabel songImage = new JLabel(loadImage("src/assets/drive-download-20241029T152703Z-001/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.BOLD, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth() / 2 - 300 / 2, 365, 300, 40);
        playbackSlider.setBackground(null);

        // Add change listener to handle slider changes
        playbackSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            int frame = source.getValue();
            musicPlayer.setCurrentFrame(frame);
            int timeInMilli = (int) (frame / (2.08 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds()));
            musicPlayer.setCurrentTimeInMilli(timeInMilli);
        });

        // Add mouse listener for play/pause functionality
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Resume playback from the new position when slider is released
                musicPlayer.playCurrentSong();
                enablePauseButtonDisablePlayButton();
            }
        });

        add(playbackSlider);
        addPlaybackBtns();
    }

    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);
        toolBar.setFloatable(false);

        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        JMenu songMenu = new JMenu("song");
        menuBar.add(songMenu);

        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(e -> {
            int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
            File selectedFile = jFileChooser.getSelectedFile();

            if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                Song song = new Song(selectedFile.getPath());
                musicPlayer.loadSong(song);
                updateSongTitleAndArtist(song);
                updatePlaybackSlider(song);
                enablePauseButtonDisablePlayButton();
            }
        });
        songMenu.add(loadSong);

        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);
        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // load music playlist dialog
                new MusicPlaylistDialog(MusicPlayerGUI.this).setVisible(true);
            }
        });
        playlistMenu.add(createPlaylist);
        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist","txt"));
                jFileChooser.setCurrentDirectory(new File("src/assets"));

                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    // stop the music
                    musicPlayer.stopSong();

                    // load playlist
                    musicPlayer.loadPlaylist(selectedFile);
                }
            }
        });
        playlistMenu.add(loadPlaylist);

        add(toolBar);
    }

    private void addPlaybackBtns() {
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null);

        JButton prevButton = new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/previous.png"));
        prevButton.setBackground(null);
        prevButton.setBorderPainted(false);
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // go to the previous song
                musicPlayer.prevSong();
            }
        });
        playbackBtns.add(prevButton);

        JButton playButton = new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/play.png"));
        playButton.setBackground(null);
        playButton.setBorderPainted(false);
        playButton.addActionListener(e -> {
            enablePauseButtonDisablePlayButton();
            musicPlayer.playCurrentSong();
        });
        playbackBtns.add(playButton);

        JButton pauseButton = new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/pause.png"));
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.setBorderPainted(false);
        pauseButton.addActionListener(e -> {
            enablePlayButtonDisablePauseButton();
            musicPlayer.pauseSong();
        });
        playbackBtns.add(pauseButton);

        JButton nextButton = new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/next.png"));
        nextButton.setBackground(null);
        nextButton.setBorderPainted(false);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

             // go to the next song
                musicPlayer.nextSong();
            }
        });
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }

    public void setPlaybackSliderValue(int frame) {
        playbackSlider.setValue(frame);
    }

    private void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    private void updatePlaybackSlider(Song song) {
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        JLabel labelBeginning = new JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(TEXT_COLOR);
        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnd.setForeground(TEXT_COLOR);
        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);
        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    private void enablePauseButtonDisablePlayButton() {
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);
        playButton.setVisible(false);
        playButton.setEnabled(false);
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    private void enablePlayButtonDisablePauseButton() {
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);
        playButton.setVisible(true);
        playButton.setEnabled(true);
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
