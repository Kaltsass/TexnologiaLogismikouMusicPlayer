import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class MusicPlayerGUI extends JFrame {
    //colar configurations
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;

    private JFileChooser jFileChooser;

    private JLabel songTitle,songArtist;
    private JPanel playbackBtns;

    public MusicPlayerGUI() {
        //calls JFrame constructor to configure out gui and set the title header to "Music Player"
        super("Music Player");

        //set the width and height
        setSize( 400, 600 );

        //end process when app is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //launch the app at the center of the screen
        setLocationRelativeTo(null);

        //prevent the app from being resized
        setResizable(false);

        //set layout to null wivh allows to control the (x,y) coordinates of our components
        //and also set height and width
        setLayout(null);

        //change the frame color
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer();
        jFileChooser = new JFileChooser();

        jFileChooser.setCurrentDirectory(new File("src/assets"));


        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));

        addGUIComponents();
    }

    private void addGUIComponents(){
        //add toolbar
        addToolbar();

        //load record image
        JLabel songImage = new JLabel(loadImage("src/assets/drive-download-20241029T152703Z-001/record.png"));
        songImage.setBounds(0,50,getWidth() - 20 , 225);
        add(songImage);


        //song title
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0,285,getWidth()-10,30);
        songTitle.setFont(new Font("Dialog",Font.BOLD,24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);


        //song artist
        songArtist =new JLabel("Artist");
        songArtist.setBounds(0,315,getWidth()-10,30);
        songArtist.setFont(new Font("Dialog",Font.BOLD,24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);


        //playbackslider
        JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL,0,100,0);
        playbackSlider.setBounds(getWidth()/2 - 300/2,365,300,40 );
        playbackSlider.setBackground(null);
        add(playbackSlider);

        //playbackbuttons

        addPlaybackBtns();

    }
    private void addToolbar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0,0,getWidth(),20);

        // prevent toolbar from being moved
        toolBar.setFloatable(false);

        //add drop down menu
        JMenuBar menuBar=new JMenuBar();
        toolBar.add(menuBar);

        //now we will add a song menu where we will place the loading song option
        JMenu songMenu=new JMenu ("song");
        menuBar.add(songMenu);

        //add the "load song"item in the songMenu
        JMenuItem loadSong=new JMenuItem ("Load Song");
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //integer returned from user input
                int result=jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                //checking if "open" button is pressed
                if(result==JFileChooser.APPROVE_OPTION && selectedFile !=null){

                    Song song = new Song(selectedFile.getPath());


                    musicPlayer.loadSong(song);

                    //update song title artist
                    updateSongTitleAndArtist(song);

                    enablePauseButtonDisablePlayButton();


                }
            }
        });
        songMenu.add(loadSong);

        //now we will add the playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        //then add the item to the playlist menu
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

    private void addPlaybackBtns(){
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0,435,getWidth()-10,80);
        playbackBtns.setBackground(null);

        //previous button

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

        //play button

        JButton playButton = new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/play.png"));
        playButton.setBackground(null);
        playButton.setBorderPainted(false);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //toggle off play toggle on pause
                enablePauseButtonDisablePlayButton();

                //play or resume song
                musicPlayer.playCurrentSong();
            }
        });
        playbackBtns.add(playButton);

        //pause button

        JButton pauseButton =new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/pause.png"));
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.setBorderPainted(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // toggle off pause toggle on play

                enablePlayButtonDisablePauseButton();

                //pause song

                musicPlayer.pauseSong();
            }
        });
        playbackBtns.add(pauseButton);

        //next button

        JButton nextButton= new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/next.png"));
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

    private void updateSongTitleAndArtist(Song song){

        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    private void    enablePauseButtonDisablePlayButton(){

        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton= (JButton) playbackBtns.getComponent(2);

        playButton.setVisible(false);
        playButton.setEnabled(false);

        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    private void    enablePlayButtonDisablePauseButton(){

        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton= (JButton) playbackBtns.getComponent(2);

        playButton.setVisible(true);
        playButton.setEnabled(true);

        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath){

        try {
            //read the image file from the given path
            BufferedImage image = ImageIO.read(new File(imagePath));


            //return on image icon so that our component can render the image
            return new ImageIcon(image);

        }catch (Exception e){
            e.printStackTrace();

        }
        //could not find resource
        return null;
    }
}