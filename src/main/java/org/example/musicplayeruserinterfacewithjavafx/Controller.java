package org.example.musicplayeruserinterfacewithjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Song;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    private HBox favoriteContainer;

    List<Song> recentlyPlayed;
    List<Song> favorites;


    @FXML
    private HBox recentlyPlayedContainer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        recentlyPlayed= new ArrayList<>(getRecentlyPlayed());
        try {
            for(Song song : recentlyPlayed){
                FXMLLoader fxmlLoader =new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("song.fxml"));

                VBox vBox = fxmlLoader.load();
                SongController songController =fxmlLoader.getController();
                songController.setData(song);

                recentlyPlayedContainer.getChildren().add(vBox);
            }

        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private List<Song> getRecentlyPlayed(){
        List<Song> ls = new ArrayList<>();

        Song song = new Song();
        song.setName("I Hronia Mou");
        song.setArtist("Mpelafon");
        song.setCover("@../../../img/Mpelafon.png");
        ls.add(song);

        song = new Song();
        song.setName("Pellegrino");
        song.setArtist("Mikros Kleftis,Dof Twogee");
        song.setCover("@../../../img/mk_pelegrino.png");
        ls.add(song);

        song = new Song();
        song.setName("20/20");
        song.setArtist("Mikros Kleftis,LEX");
        song.setCover("@../../../img/mk_20-20.png");
        ls.add(song);

        song = new Song();
        song.setName("Smooth Kai Hardcorila");
        song.setArtist("Thitis,Sadomas,ΔΠΘ,Buzz,MadnessKey");
        song.setCover("@../../../img/mk_20-20.png");
        ls.add(song);

        song = new Song();
        song.setName("Top Boys");
        song.setArtist("Sadam,LEX,Dof Twogee");
        song.setCover("@../../../img/sadam.png");
        ls.add(song);


        song = new Song();
        song.setName("Gioconda");
        song.setArtist("Immune");
        song.setCover("@../../../img/Gioconda-Immune.png");
        ls.add(song);



        return ls;
    }

}
