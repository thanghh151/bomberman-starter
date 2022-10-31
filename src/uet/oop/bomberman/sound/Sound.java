package uet.oop.bomberman.sound;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Sound {
    static final String Main = "res/computer-love-121106.mp3";
    static Media sound = new Media((new File(Main)).toURI().toString());
    public static MediaPlayer mediaPlayer = new MediaPlayer(sound);

    public Sound() {
    }

    public static void play() {
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }
}