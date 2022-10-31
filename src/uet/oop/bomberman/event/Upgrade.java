package uet.oop.bomberman.event;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.*;


public class Upgrade {

    public static boolean Endgame(String state) {
        Dialog<Boolean> dialog = new Dialog<>();
        if (state.equals("win")) {
            dialog.setHeaderText("VICTORY");
        } else dialog.setHeaderText("LOSE");
        ButtonType checkContinueButton = new ButtonType("OK!", ButtonBar.ButtonData.YES);
        dialog.getDialogPane().getButtonTypes().addAll(checkContinueButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> true);
        dialog.showAndWait();
        return dialog.getResult();
    }


    public static void guide(AnimationTimer timer) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setHeaderText("Guide");
        ButtonType checkContinueButton = new ButtonType("OK!", ButtonBar.ButtonData.YES);
        dialog.getDialogPane().getButtonTypes().addAll(checkContinueButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        dialog.showAndWait();
    }
}

