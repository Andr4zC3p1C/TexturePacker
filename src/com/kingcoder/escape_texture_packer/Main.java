package com.kingcoder.escape_texture_packer;

import java.awt.Point;
import java.io.File;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application{

	public static final String TITLE = "TexturePacker";
	public static final int WIDTH = 1600;
	public static final int HEIGHT = 900;
	
	public static boolean choosingDir = false;
	
	// Graphics
	private static Group root;
	private Scene scene;
	private Canvas canvas;
	private GraphicsContext g;
	private static Stage stage;
	public static Point camera;
	private static DragDropEvent dde;
	
	// Buttons
	private Button newTextureB;
	private Button generate;
	
	// Util
	private Editor editor;
	public static Input in;
	
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage){
		Main.stage = stage;
		stage.setTitle(TITLE);
		stage.requestFocus();
		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT);
		stage.setResizable(false);

		init_m();
		
		g = canvas.getGraphicsContext2D();
		new AnimationTimer() {
			public void handle(long now) {
				if(!choosingDir){
					update();
					render();
				}
			}
		}.start();
		
		stage.show();
	}

	private void init_m(){
		// creating the root and a stage
		root = new Group();
		scene = new Scene(root);
		scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        
        // Dropping over surface
        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    dde.dropped(db);
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
		
		stage.setScene(scene);
		
		// setting the canvas
		canvas = new Canvas(WIDTH, HEIGHT);
		root.getChildren().add(canvas);
		
		// initializing other objects
		editor = new Editor();
		camera = new Point(0,0);
		in = new Input(scene);
	}
	
	public static void addToRoot(Node n){
		root.getChildren().add(n);
	}
	
	public static void setDragDropEvent(DragDropEvent dde){
		Main.dde = dde;
	}
	
	private void update(){
		editor.update();
	}
	
	private void render(){
		g.setFill(Color.LIGHTSLATEGRAY);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		editor.render(g);
	}
	
	public static Stage getStage(){
		return stage;
	}
	
}
