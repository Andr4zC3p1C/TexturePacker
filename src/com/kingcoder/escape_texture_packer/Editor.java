package com.kingcoder.escape_texture_packer;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;

public class Editor implements DragDropEvent{

	private static int IMG = 0;
	private static int ANIM = 1;
	private int state = IMG;
	
	private Image backgroundImg;
	private Texture texture;
	
	private Point mousePos = new Point();
	
	// GUI
	public static final Font DD_FONT = new Font(42);
	public static final Font SUPPORT_FONT = new Font(24);
	public static final Font IMG_FONT = new Font(16);
	public static final Color SELECTED_COLOR = new Color(100.0 / 255.0 , 149.0 / 255.0, 237.0 / 255.0, 200.0 / 255.0);
	private Button genButton;
	private Button browseButton;
	private TextField storeLoc;
	private TextField nameArea;
	private ImgHandle imgHandle;
	
	public Editor(){
		texture = new Texture();
		backgroundImg = new Image(getClass().getResourceAsStream("/img/background_grid.png"));
		Main.setDragDropEvent(this);
		
		// GUI
		genButton = new Button("Generate");
		genButton.setTranslateX(5);
		genButton.setTranslateY(Main.HEIGHT - 100);
		genButton.setPrefWidth(140);
		genButton.setPrefHeight(40);
		genButton.setVisible(false);
		genButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if(!storeLoc.getText().isEmpty() && !nameArea.getText().isEmpty()){
					texture.setName(nameArea.getText());
					texture.generate(storeLoc.getText());
				}
			}
		});
		Main.addToRoot(genButton);
		
		browseButton = new Button("Browse");
		browseButton.setTranslateX(150);
		browseButton.setTranslateY(Main.HEIGHT - 100);
		browseButton.setPrefWidth(140);
		browseButton.setPrefHeight(40);
		browseButton.setVisible(false);
		browseButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Main.choosingDir = true;
				DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(Main.getStage());
                Main.choosingDir = false;
                if(selectedDirectory == null) return;
                
                String path = selectedDirectory.getAbsolutePath();
                path = path.replace('\\', '/');
                storeLoc.setText(path);
			}
		});
		Main.addToRoot(browseButton);
		
		storeLoc = new TextField();
		storeLoc.setPrefWidth(290);
		storeLoc.setPrefHeight(15);
		storeLoc.setTranslateX(5);
		storeLoc.setTranslateY(Main.HEIGHT - 150);
		storeLoc.setVisible(false);
		storeLoc.setPromptText("Destination");
		Main.addToRoot(storeLoc);
		
		nameArea = new TextField();
		nameArea.setPrefWidth(290);
		nameArea.setPrefHeight(15);
		nameArea.setTranslateX(5);
		nameArea.setTranslateY(Main.HEIGHT - 190);
		nameArea.setVisible(false);
		nameArea.setPromptText("Name the pack");
		Main.addToRoot(nameArea);
		
		imgHandle = new ImgHandle(texture.getTabEvent());
	}
	
	public void update(){
		if(!imgHandle.isEnabled() && !texture.isEmpty()){
			imgHandle.setEnabled(true);
			genButton.setVisible(true);
			browseButton.setVisible(true);
			storeLoc.setVisible(true);
			nameArea.setVisible(true);
		}
		
		// move the camera around
		if(Main.in.isMouseButtonPressed(MouseButton.PRIMARY) && mousePos.x > 305 && imgHandle.getSelectedTab() == ImgHandle.PACK_TAB){
			Main.camera.x += Main.in.mousePos.x - mousePos.x;
			Main.camera.y += Main.in.mousePos.y - mousePos.y;
		}
		
		mousePos.x = Main.in.mousePos.x;
		mousePos.y = Main.in.mousePos.y;	
		
		// select img
		imgHandle.update();
		String key = imgHandle.getSelected();
		if(key != null){
			texture.setSelected(texture.getIndexOfImage(key));
		}else{
			texture.setSelected(-1);
		}
		
		if(Main.in.isKeyPressed(KeyCode.DELETE) && imgHandle.isSelected()){
			String selected = imgHandle.getSelected();
			imgHandle.removeSelected();
			texture.removeImage(texture.getIndexOfImage(selected));
			
			if(texture.isEmpty()){
				imgHandle.setEnabled(false);
				genButton.setVisible(false);
				browseButton.setVisible(false);
				storeLoc.setVisible(false);
				nameArea.setVisible(false);
			}
		}
	}
	
	public void render(GraphicsContext g){
		if(!texture.isEmpty()){
			g.drawImage(backgroundImg, 0, 0);
			texture.render(g);
			g.setFill(Color.DARKGRAY);
			g.fillRect(0, 0, 300, Main.HEIGHT);
			g.setFill(Color.GRAY);
			g.fillRect(300, 0, 5, Main.HEIGHT);
		}else{
			g.setFill(Color.gray(0.7));
			g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
			g.setFill(Color.LIGHTSKYBLUE);
			g.setFont(DD_FONT);
			g.fillText("Drag and drop images to create a NEW pack", 400, Main.HEIGHT / 2);
			g.fillText("Or drag and drop a dir with .tex to OPEN a pack ", 400, Main.HEIGHT / 2 + 100);
		}
		
		imgHandle.render();
		
		if(!texture.isEmpty()){
			g.setFill(Color.LIGHTSKYBLUE);
			g.setFont(IMG_FONT);
			g.fillText("Press DELETE key to remove an image", 10, 20);
		}
	}

	public void dropped(Dragboard db) {
		List<File> files = db.getFiles();
		for(int i=0; i < files.size(); i++){
			File file = files.get(i);
			
			if(file.isDirectory()){				// check if .tex in there
				if(isValidTexDir(file)){
					if(texture.isEmpty())
						openTex(file);
					else
						openPermissionPopUp();
					return;
				}
			}else if(isValidFile(file)){		// check if valid images
				addImage(file);
			}
		}
	}
	
	private boolean isValidFile(File file){
		String name = file.getName();
		return name.endsWith("jpg") | name.endsWith("jpeg") | name.endsWith("png") | name.endsWith("bmp") | name.endsWith("gif");
	}
	
	private boolean isValidTexDir(File file){
		return false;
	}
	
	private void addImage(File file){
		try {
			Image img = new Image(new FileInputStream(file));
			String name = file.getName().split("\\.")[0];
			imgHandle.addImage(name);
			texture.addImage(img, name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void openTex(File file){
		imgHandle.clear();
		texture.clear();
		
		// opening
		
	}
	
	private void openPermissionPopUp(){
		// create new stage
	}
	
}
