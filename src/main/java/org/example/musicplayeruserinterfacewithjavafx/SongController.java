package org.example.musicplayeruserinterfacewithjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Song;

public class SongController {


    @FXML
    private Label artist;

    @FXML
    private ImageView img;

    @FXML
    private Label songName;

    public void setData(Song song){
        Image image = new Image(getClass().getResourceAsStream(song.getCover()));
        img.setImage(image);
        songName.setText(song.getName());
        artist.setText(song.getArtist());
    }
}
