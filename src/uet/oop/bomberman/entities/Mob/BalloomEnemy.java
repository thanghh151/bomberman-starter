package uet.oop.bomberman.entities.Mob;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

public class BalloomEnemy extends Mob {
    public BalloomEnemy(int x, int y, Image img) {
        super( x, y, img);
    }

    @Override
    public void update(int time) {
        switch (state) {
            case ("left") : {
                this.img = Sprite.movingSprite(Sprite.balloom_left1, Sprite.balloom_left2, Sprite.balloom_left3, time, 3).getFxImage();
                break;
            }
            case ("up") : {
                this.img = Sprite.movingSprite(Sprite.balloom_left1, Sprite.balloom_left2, Sprite.balloom_left3, time, 3).getFxImage();
                break;
            }
            case ("right"): {
                this.img = Sprite.movingSprite(Sprite.balloom_right1, Sprite.balloom_right2, Sprite.balloom_right3, time, 3).getFxImage();
                break;
            }
            case ("down"): {
                this.img = Sprite.movingSprite(Sprite.balloom_right1, Sprite.balloom_right2, Sprite.balloom_right3, time, 3).getFxImage();
                break;
            }
            case ("dead"): {
                this.img = Sprite.balloom_dead.getFxImage();
                break;
            }
        }
    }

}