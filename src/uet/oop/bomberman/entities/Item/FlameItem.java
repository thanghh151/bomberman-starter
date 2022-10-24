package uet.oop.bomberman.entities.Item;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.graphics.Sprite;

public class FlameItem extends Entity {
    public FlameItem(int x, int y, Image img) {
        super( x, y, img);
        super.state = "live";
    }

    public boolean collidesWithBomber(Entity bomber){
        if ((x-bomber.getX())/ Sprite.SCALED_SIZE == 0 && (y- bomber.getY())/Sprite.SCALED_SIZE == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void update(int time) {

    }

}
