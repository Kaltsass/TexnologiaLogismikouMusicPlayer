import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MusicPlayerGUI extends JFrame {
    //colar configurations
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;
    
    
    
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
                songTitle.setBounds(0,285,getWidth()-10,30);
        songTitle.setFont(new Font("Dialog",Font.BOLD,24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);


        //song artist
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
        songMenu.add(loadSong);

        //now we will add the playlist menu
        JMenu playlistMenu=new JMenu("Playlist");
        menuBar.add(playlistMenu);

        //then add the item to the playlist menu
        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        playlistMenu.add(loadPlaylist);


        add(toolBar);

    }

    private void addPlaybackBtns(){
        playbackBtns.setBounds(0,435,getWidth()-10,80);
        playbackBtns.setBackground(null);

        //previous button

        JButton prevButton = new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/previous.png"));
        prevButton.setBackground(null);
        prevButton.setBorderPainted(false);
        playbackBtns.add(prevButton);

        //play button

        JButton playButton = new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/play.png"));
        playButton.setBackground(null);
        playButton.setBorderPainted(false);
        playbackBtns.add(playButton);

        //pause button

        JButton pauseButton =new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/pause.png"));
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.setBorderPainted(false);
        playbackBtns.add(pauseButton);

        //next button

        JButton nextButton= new JButton(loadImage("src/assets/drive-download-20241029T152703Z-001/next.png"));
        nextButton.setBackground(null);
        nextButton.setBorderPainted(false);
        playbackBtns.add(nextButton);

        add(playbackBtns);


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

