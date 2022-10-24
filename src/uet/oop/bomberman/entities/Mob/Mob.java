package uet.oop.bomberman.entities.Mob;

import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import uet.oop.bomberman.entities.Bomb.Flame;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Brick;
import uet.oop.bomberman.entities.Wall;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;

public class Mob extends Entity {

    public Mob(int x, int y, Image img) {
        super( x, y, img);
    }


    public void update(int time) {

    }

    public boolean collidesWith(Entity other) {
        if((x% Sprite.SCALED_SIZE==0||y%Sprite.SCALED_SIZE==0)) {
            if(other instanceof Wall ||other instanceof Brick) {
                Rectangle s1 = new Rectangle(x, y);
                s1.setHeight(Sprite.SCALED_SIZE);
                s1.setWidth(Sprite.SCALED_SIZE);
                Rectangle s2 = new Rectangle(other.getX(), other.getY());
                s2.setHeight(Sprite.SCALED_SIZE);
                s2.setWidth(Sprite.SCALED_SIZE);
                return !s1.getBoundsInParent().intersects(s2.getBoundsInParent());
            }
        }
        return true;
    }

    public void createBomb(List<Bomb> bomb, int maxBomb, int flameLevel) {
        int realX = this.getX()/Sprite.SCALED_SIZE;
        int realY = this.getY()/Sprite.SCALED_SIZE;
        int tempX = this.getX()%Sprite.SCALED_SIZE;
        int tempY = this.getY()%Sprite.SCALED_SIZE;
        if(tempX > 16) realX += 1;
        if(tempY > 16) realY += 1;
        if(bomb.size() < maxBomb) {
            Bomb newBomb = new Bomb(realX, realY, Sprite.bomb_2.getFxImage());
            newBomb.setLevel(flameLevel);
            bomb.add(newBomb);
            newBomb.startCount();
        }
    }

    public boolean collidesWithBomb(List<Bomb> bomb) {
        for(Entity other : bomb) {
            if((x-(other.getX()-32)==0 && state.equals("right") && y == other.getY())
                    || (x-(other.getX()+32)==0 && state.equals("left") && y == other.getY())
                    || (y-(other.getY()-32)==0 && state.equals("down") && x == other.getX())
                    || (y-(other.getY()+32)==0 && state.equals("up") && x == other.getX())) return false;
        }
        return true;
    }

    public void checkDeadFlame(List<Bomb> bomb) {

        for (Bomb other :bomb) {
            if(other.getState().equals("dead")) {
                if (this.getX() < other.getX() + (other.getMaxSizeRight() + 1) * Sprite.SCALED_SIZE && this.getX() > other.getX() - (other.getMaxSizeLeft() + 1) * Sprite.SCALED_SIZE && this.getY() == other.getY()) {
                    this.state = "dead";
                    break;
                }
                if (this.getY() < other.getY() + (other.getMaxSizeDown() + 1) * Sprite.SCALED_SIZE && this.getY() > other.getY() - (other.getMaxSizeTop() + 1) * Sprite.SCALED_SIZE && this.getX() == other.getX()) {
                    this.state = "dead";
                    break;
                }
            }
        }
    }

    public void checkDeadEnemy(List<Mob> entities) {
        for(Mob other :entities) {
            if(!(other instanceof Bomber)) {
                if(this.getX() < other.getX() + Sprite.SCALED_SIZE && this.getX() > other.getX() - Sprite.SCALED_SIZE && this.getY() == other.getY()) {
                    this.state = "dead";
                    break;
                }
                if(this.getY() < other.getY() + Sprite.SCALED_SIZE && this.getY() > other.getY() - Sprite.SCALED_SIZE && this.getX() == other.getX()) {
                    this.state = "dead";
                    break;
                }
            }
        }
    }


}