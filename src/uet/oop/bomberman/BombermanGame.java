package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import uet.oop.bomberman.entities.Item.BombItem;
import uet.oop.bomberman.entities.Item.FlameItem;
import uet.oop.bomberman.entities.Item.SpeedItem;
import uet.oop.bomberman.entities.Brick;
import uet.oop.bomberman.entities.Grass;
import uet.oop.bomberman.entities.Portal;
import uet.oop.bomberman.entities.Wall;
import uet.oop.bomberman.entities.Bomb.*;
import uet.oop.bomberman.entities.Mob.*;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.event.*;

import java.util.*;

public class BombermanGame extends Application {

    public static String[] map = {
            "###############################",
            "#p     ** *  1 * 2 *  * * *   #",
            "# # # #*# # #*#*# # # #*#*#*# #",
            "#  x*     ***  *  1   * 2 * * #",
            "# # # # # #*# # #*#*# #*#*# #*#",
            "#f         x **  *  *         #",
            "# # # # # # # # # #*# #*# # # #",
            "#*  *      *  *      *        #",
            "# # # # #*# # # #*#*# # # # # #",
            "#* 1  **  *       *           #",
            "# #*# # # # # # #*# # # # # # #",
            "#           *   *  *          #",
            "###############################"
    };

    public static final int WIDTH = 31;
    public static final int HEIGHT = 13;

    private long lastUpdate; // Last time in which `handle()` was called
    private int speed = 2 ; // tốc độ nhân vật di chuyển
    private int maxSpeed = 4;// toc do toi da
    private int enemySpeed = 1; //tốc độ địch di chuyển
    private int maxBomb = 1;// Số bomb tối đa được đặt
    private int maxBombCanPowerUp = 3;//so bom toi da co the nang cap
    private int flameLevel = 1;//do dai cua flame
    private int maxFlameLevel = 3;//so level lua toi da

    private int dx;
    private int dy;

    private GraphicsContext gc;
    private Canvas canvas;

    private List<Mob> players = new ArrayList<>();
    private List<Mob> entities = new ArrayList<>();
    private List<Entity> stillObjects = new ArrayList<>();
    private List<Bomb> bomb = new ArrayList<>();
    private List<Flame> temp = new ArrayList<>();
    private List<Entity> ItemList = new ArrayList<>();
    private List<Entity> renderUnderBrick = new ArrayList<>();

    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Tao Canvas
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Tao root container
        Group root = new Group();
        root.getChildren().add(canvas);

        // Tao scene
        Scene scene = new Scene(root);

        // Them scene vao stage
        stage.setScene(scene);
        stage.show();

        lastUpdate = System.nanoTime();

        createMap();
        createItem();
        createEntity();


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedNanoSeconds = now - lastUpdate;
                // 1 second = 1,000,000,000 (1 billion) nanoseconds
                double elapsedSeconds = 6 * elapsedNanoSeconds / 1_000_000_000.0;

                update((int) elapsedSeconds);

                render();

                move();
                if (players.isEmpty()) {
                    Task<Void> task = new Task<Void>() {
                        @Override
                        public Void call() throws Exception {
                            stop();
                            return null ;
                        }
                    };
                    task.setOnSucceeded(e -> {
                        boolean ktra = Upgrade.Endgame("lose",(int) elapsedSeconds);
                        if(ktra) Platform.exit();
                    });
                    new Thread(task).start();
                }
                if (entities.isEmpty()) {
                    if(((Portal) ItemList.get(0)).collidesWithBomber(players.get(0))) {
                        Task<Void> task = new Task<Void>() {
                            @Override
                            public Void call() throws Exception {
                                stop();
                                return null;
                            }
                        };
                        task.setOnSucceeded(e -> {
                            boolean ktra = Upgrade.Endgame("win", (int) elapsedSeconds);
                            if (ktra) Platform.exit();
                        });
                        new Thread(task).start();
                    }
                }
            }
        };
        timer.start();

        scene.setOnKeyPressed(e->{
            if(!players.isEmpty()) {
                if (!players.get(0).getState().equals("dead")) {
                    KeyCode key = e.getCode();

                    if (key == KeyCode.LEFT) {
                        players.get(0).setState("left");
                        dx = -speed;
                    }

                    if (key == KeyCode.RIGHT) {
                        players.get(0).setState("right");
                        dx = speed;
                    }

                    if (key == KeyCode.UP) {
                        players.get(0).setState("up");
                        dy = -speed;
                    }

                    if (key == KeyCode.DOWN) {
                        players.get(0).setState("down");
                        dy = speed;
                    }

                    if (key == KeyCode.SPACE) {
                        players.get(0).createBomb(bomb, maxBomb, flameLevel);
                    }

                    if (key == KeyCode.ESCAPE) {
                        timer.stop();
                        Upgrade.guide(timer);
                    }

                }
            }
        });

        scene.setOnKeyReleased(e->{
            if(!players.isEmpty()) {
                if (!players.get(0).getState().equals("dead")) {
                    KeyCode key = e.getCode();

                    if (key == KeyCode.LEFT) {
                        players.get(0).setState("leftStop");
                        dx = 0;
                    }

                    if (key == KeyCode.RIGHT) {
                        players.get(0).setState("rightStop");
                        dx = 0;
                    }

                    if (key == KeyCode.UP) {
                        players.get(0).setState("upStop");
                        dy = 0;
                    }

                    if (key == KeyCode.DOWN) {
                        players.get(0).setState("downStop");
                        dy = 0;
                    }
                }
            }
        });

    }

    public void createMap() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Entity object;
                if (map[j].charAt(i) == '#') {
                    object = new Wall(i, j, Sprite.wall.getFxImage());
                } else if (map[j].charAt(i) == '*') {
                    object = new Brick(i, j, Sprite.brick.getFxImage());
                } else {
                    object = new Grass(i, j, Sprite.grass.getFxImage());
                }
                stillObjects.add(object);
            }
        }
    }

    public void createItem() {
        FlameItem f1 = new FlameItem(7, 1, Sprite.powerup_flames.getFxImage());
        FlameItem f2 = new FlameItem(11, 4, Sprite.powerup_flames.getFxImage());
        BombItem b1 = new BombItem(3,10,Sprite.powerup_bombs.getFxImage());
        BombItem b2 = new BombItem(17,10,Sprite.powerup_bombs.getFxImage());
        SpeedItem s1 = new SpeedItem(25, 2, Sprite.powerup_speed.getFxImage());
        SpeedItem s2 = new SpeedItem(29, 4, Sprite.powerup_speed.getFxImage());
        Portal p = new Portal(1, 7, Sprite.portal.getFxImage());
        ItemList.add(p);
        ItemList.add(f1);
        ItemList.add(f2);
        ItemList.add(b1);
        ItemList.add(b2);
        ItemList.add(s1);
        ItemList.add(s2);
    }

    public void createEntity() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (map[j].charAt(i) == 'p') {
                    Mob object = new Bomber(i, j, Sprite.player_right.getFxImage());
                    players.add(object);
                } else if (map[j].charAt(i) == '1') {
                    Mob object = new BalloomEnemy(i, j, Sprite.balloom_left1.getFxImage());
                    object.setState("right");
                    entities.add(object);
                } else if (map[j].charAt(i) == '2') {
                    Mob object = new OnealEnemy(i, j, Sprite.oneal_left1.getFxImage());
                    object.setState("right");
                    entities.add(object);
                }
            }
        }
    }

    public void update(int elapsedSeconds) {

        for (Entity stillObject : stillObjects) {
            stillObject.update(elapsedSeconds);
        }

        for (Entity ItemList : ItemList) {
            ItemList.update((int) elapsedSeconds);
        }

        if(!entities.isEmpty()) {
            for (Mob entity : entities) {
                entity.update(elapsedSeconds);
            }
        }

        players.forEach(g -> {
            g.update(elapsedSeconds);
        });

        bomb.forEach(g -> {
            g.update((elapsedSeconds));
        });

        if(!ItemList.isEmpty()){
            itemBuff();
        }
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        stillObjects.forEach(g -> {
            if(g instanceof Brick && g.getState().equals("dead"))  {
                int realX = g.getX()/Sprite.SCALED_SIZE;
                int realY = g.getY()/Sprite.SCALED_SIZE;
                renderUnderBrick.add(new Grass(realX,realY,Sprite.grass.getFxImage()));
                renderUnderBrick.forEach(v->v.render(gc));
                ((Brick) g).destroyBrick(stillObjects,HEIGHT);
                renderUnderBrick.clear();
            }
            g.render(gc);
        });
        ItemList.forEach(g->{
            int realX = g.getX()/Sprite.SCALED_SIZE, realY = g.getY()/Sprite.SCALED_SIZE;
            if(stillObjects.get(realX*HEIGHT+realY) instanceof Grass) {
                g.render(gc);
            }

        });
        if(!ItemList.isEmpty()) {
            ItemList.removeIf(g->{
                return g.getState().equals("dead");
            });
        }
        bomb.removeIf(g -> {
            return g.getState().equals("explode");
        });
        bomb.forEach(g -> g.render(gc));
        bomb.forEach(g -> {
            if(g.getState().equals("dead")) {
                destroyAround(g);
                g.collideWithBrick(stillObjects,HEIGHT);
            }
        });
        entities.forEach(g -> {
            if(!temp.isEmpty()) g.checkDeadFlame(bomb);
            g.render(gc);
            if(g.getState().equals("dead")) {
                Timer count = new Timer();
                count.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        entities.remove(g);
                        count.cancel();
                    }},200,1);
            }
        });
        players.forEach(g-> {
            if(g.getState().equals("dead")) {
                Timer count = new Timer();
                count.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        players.remove(g);
                        count.cancel();
                    }},200,1);
            }
        });
        players.forEach(g -> {
            if(!temp.isEmpty()) g.checkDeadFlame(bomb);
            g.checkDeadEnemy(entities);
            g.render(gc);

        });
        temp.clear();
    }

    public void destroyAround(Bomb g) {
        g.explode(stillObjects,HEIGHT,temp);
        g.collideWithAnotherBomb(bomb,HEIGHT,temp,stillObjects);
        temp.forEach(v -> v.render(gc));
        Timer count = new Timer();
        count.schedule(new TimerTask() {
            @Override
            public void run() {
                g.setState("explode");

                count.cancel();
            }},200,1);
    }

    public void randomState(Mob e) {
        int realX = e.getX()/Sprite.SCALED_SIZE;
        int realY = e.getY()/Sprite.SCALED_SIZE;
        Random generator = new Random();
        int randomStateNumber;
        if(e.getX()%Sprite.SCALED_SIZE==0 && (e.getY()%Sprite.SCALED_SIZE==0)){
            if (e.getState().equals("right")) {
                if(!e.collidesWith(stillObjects.get(realX * HEIGHT + realY +HEIGHT)) || !e.collidesWithBomb(bomb)){
                    randomStateNumber = generator.nextInt(4) + 1;
                    setRandomState(randomStateNumber,e);
                }
            } else if (e.getState().equals("up")) {
                if(!e.collidesWith(stillObjects.get(realX * HEIGHT + realY -1)) || !e.collidesWithBomb(bomb)){
                    randomStateNumber = generator.nextInt(4) + 1;
                    setRandomState(randomStateNumber,e);
                }
            } else if (e.getState().equals("down")) {
                if(!e.collidesWith(stillObjects.get(realX * HEIGHT + realY +1)) || !e.collidesWithBomb(bomb)){
                    randomStateNumber = generator.nextInt(4) + 1;
                    setRandomState(randomStateNumber,e);
                }
            } else if (e.getState().equals("left")) {
                if (!e.collidesWith(stillObjects.get(realX * HEIGHT + realY-HEIGHT)) || !e.collidesWithBomb(bomb)){
                    randomStateNumber = generator.nextInt(4) + 1;
                    setRandomState(randomStateNumber,e);
                }
            }
        }
    }

    public void randomStateForOneal(OnealEnemy e) {
        int realX = e.getX()/Sprite.SCALED_SIZE;
        int realY = e.getY()/Sprite.SCALED_SIZE;
        Random generator = new Random();
        Timer count = new Timer();

        count.schedule(new TimerTask() {
            @Override
            public void run() {
                int randomStateNumber;
                if(e.getX()%Sprite.SCALED_SIZE==0 && (e.getY()%Sprite.SCALED_SIZE==0)){
                    if (e.getState().equals("right")) {
                        if(!e.collidesWith(stillObjects.get(realX * HEIGHT + realY +HEIGHT)) || !e.collidesWithBomb(bomb)){
                            randomStateNumber = generator.nextInt(4) + 1;
                            setRandomState(randomStateNumber,e);
                        }
                    } else if (e.getState().equals("up")) {
                        if(!e.collidesWith(stillObjects.get(realX * HEIGHT + realY -1)) || !e.collidesWithBomb(bomb)){
                            randomStateNumber = generator.nextInt(4) + 1;
                            setRandomState(randomStateNumber,e);
                        }
                    } else if (e.getState().equals("down")) {
                        if(!e.collidesWith(stillObjects.get(realX * HEIGHT + realY +1)) || !e.collidesWithBomb(bomb)){
                            randomStateNumber = generator.nextInt(4) + 1;
                            setRandomState(randomStateNumber,e);
                        }
                    } else if (e.getState().equals("left")) {
                        if (!e.collidesWith(stillObjects.get(realX * HEIGHT + realY-HEIGHT)) || !e.collidesWithBomb(bomb)){
                            randomStateNumber = generator.nextInt(4) + 1;
                            setRandomState(randomStateNumber,e);
                        }
                    }
                }
                count.cancel();
            }},2000,1);
    }

    public void setRandomState(int i, Mob e) {
        switch (i) {
            case 1: {
                e.setState("up");
                break;
            } case 2: {
                e.setState("down");
                break;
            } case 3: {
                e.setState("left");
                break;
            } default: {
                e.setState("right");
                break;
            }
        }
    }

    public void move() {
        players.forEach(g -> {
            Movement.move(g, dx, dy, stillObjects, HEIGHT, WIDTH, bomb);
        });

        for(int i = 0; i < entities.size();i++) {
            if (entities.get(i).getState().equals("right")) {
                Movement.move(entities.get(i), enemySpeed, enemySpeed, stillObjects, HEIGHT, WIDTH, bomb);
            } else if (entities.get(i).getState().equals("up")) {
                Movement.move(entities.get(i), enemySpeed, -enemySpeed, stillObjects, HEIGHT, WIDTH, bomb);
            } else if (entities.get(i).getState().equals("down")) { 
                Movement.move(entities.get(i), enemySpeed, enemySpeed, stillObjects, HEIGHT, WIDTH, bomb);
            } else if (entities.get(i).getState().equals("left")) {
                Movement.move(entities.get(i), -enemySpeed, enemySpeed, stillObjects, HEIGHT, WIDTH, bomb);
            }
            if (entities.get(i) instanceof BalloomEnemy) {
                randomState(entities.get(i));
            } else if (entities.get(i) instanceof OnealEnemy) {
                randomStateForOneal(((OnealEnemy)entities.get(i)));
            }
        }
    }

    public void itemBuff () {
        if(!players.isEmpty()) {
            ItemList.forEach(item -> {
                if (item instanceof FlameItem && flameLevel < maxFlameLevel) {
                    if (((FlameItem) item).collidesWithBomber(players.get(0))) {
                        flameLevel++;
                        item.setState("dead");
                    }
                } else if (item instanceof BombItem && maxBomb < maxBombCanPowerUp) {
                    if (((BombItem) item).collidesWithBomber(players.get(0))) {
                        maxBomb++;
                        item.setState("dead");
                    }
                } else if (item instanceof SpeedItem && speed < maxSpeed) {
                    if (((SpeedItem) item).collidesWithBomber(players.get(0))) {
                        speed++;
                        item.setState("dead");
                    }
                }
            });
        }
    }

}
