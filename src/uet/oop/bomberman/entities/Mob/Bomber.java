package uet.oop.bomberman.entities.Mob;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import uet.oop.bomberman.graphics.Sprite;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Bomber extends Mob {

    public Bomber(int x, int y, Image img) {
        super( x, y, img);
        super.state = "rightStop";
    }

    @Override
    public void update(int time) {
        switch (state) {
            case "right" : {
                this.img = Sprite.movingSprite(Sprite.player_right, Sprite.player_right_1, Sprite.player_right_2, time, 3).getFxImage();
                break;
            }
            case "left" : {
                this.img = Sprite.movingSprite(Sprite.player_left, Sprite.player_left_1, Sprite.player_left_2, time, 3).getFxImage();
                break;
            }
            case "up" : {
                this.img = Sprite.movingSprite(Sprite.player_up, Sprite.player_up_1, Sprite.player_up_2, time, 3).getFxImage();
                break;
            }
            case "down" : {
                this.img = Sprite.movingSprite(Sprite.player_down, Sprite.player_down_1, Sprite.player_down_2, time, 3).getFxImage();
                break;
            }
            case "rightStop" : {
                this.img = Sprite.player_right.getFxImage();
                break;
            }
            case "leftStop" : {
                this.img = Sprite.player_left.getFxImage();
                break;
            }
            case "upStop" : {
                this.img = Sprite.player_up.getFxImage();
                break;
            }
            case "downStop" : {
                this.img = Sprite.player_down.getFxImage();
                break;
            }
            case "dead" : {
                this.img = Sprite.movingSprite(Sprite.player_dead1,Sprite.player_dead2,Sprite.player_dead3,time,3).getFxImage();
                break;
            }
        }
    }

}