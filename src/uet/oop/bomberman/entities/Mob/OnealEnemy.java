package uet.oop.bomberman.entities.Mob;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

public class OnealEnemy extends Mob {
    public OnealEnemy(int x, int y, Image img) {
        super( x, y, img);
    }

    @Override
    public void update(int time) {
        switch (state) {
            case ("left") : {
                this.img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, time, 3).getFxImage();
                break;
            }
            case ("up") : {
                this.img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, time, 3).getFxImage();
                break;
            }
            case ("right"): {
                this.img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, time, 3).getFxImage();
                break;
            }
            case ("down"): {
                this.img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, time, 3).getFxImage();
                break;
            }
            case ("dead"): {
                this.img = Sprite.oneal_dead.getFxImage();
                break;
            }
        }
    }

}