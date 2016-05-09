package com.kingcoder.escape_texture_packer;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public class ImgHandle {
	
	public static int PACK_TAB = 0;
	public static int ANIM_TAB = 1;
	
	// GRAPHICS
	private ScrollPane scrollPane;
	private Canvas canvas;
	private GraphicsContext g;
	private boolean enabled;
	
	// NAMES
	private ArrayList<String> images;
	private int width, height;
	private int selectedID = -1;
	
	// TABS
	private int selectedTab = PACK_TAB;
	private AnimationTab animTab;
	private TabEvent tabEvent;
	
	public ImgHandle(TabEvent event){
		width = 300;
		height = Main.HEIGHT - 232;
		canvas = new Canvas(width, height);
		
		scrollPane = new ScrollPane(canvas);
		scrollPane.setTranslateY(30);
		scrollPane.setPrefHeight(Main.HEIGHT - 230);
		scrollPane.setPrefWidth(300);
		scrollPane.setVisible(false);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		enabled = false;
		
		Main.addToRoot(scrollPane);
		
		g = canvas.getGraphicsContext2D();
		
		images = new ArrayList<String>();
		animTab = new AnimationTab();
		
		tabEvent = event;
	}
	
	public void update(){
		if(!enabled) return;			
		
		if(Main.in.isMouseButtonPressed(MouseButton.PRIMARY)){
			if(Main.in.mousePos.y > 30 && Main.in.mousePos.y < 70){
				if(Main.in.mousePos.x < width/2){
					selectedTab = PACK_TAB;
				}else if(Main.in.mousePos.x < width){
					selectedTab = ANIM_TAB;
				}
				tabEvent.handleTabChange(selectedTab);
			}
		}
		
		if(selectedTab == ANIM_TAB){
			animTab.update();
			return;
		}
		
		if(Main.in.isMouseButtonClicked(MouseButton.PRIMARY)){
			if(Main.in.mousePos.x > 10 && Main.in.mousePos.x < 270){
				for(int i=0; i < images.size(); i++){
					if(Main.in.mousePos.y > 90 + 35 * i && Main.in.mousePos.y < 120 + 35 * i){
						if(selectedID != i)
							selectedID = i;
						else
							selectedID = -1;
						
						return;
					}
				}
				selectedID = -1;
			}		
		}
	}
	
	public void render(){
		if(!enabled) return;
		if(g == null) return;
		
		g.setFill(Color.GRAY);
		g.fillRect(0, 0, width, height);
		
		// two tabs
		if(images.size() > 0){
			g.setFill(Color.gray(0.7));
			g.fillRect(0, 0, width, 40);
			g.setFont(Editor.SUPPORT_FONT);
			
			if(selectedTab == PACK_TAB){
				g.setFill(Color.GRAY);
				g.fillRect(0, 0, width/2, 40);			
				g.fillText("ANIMATION", 158, 27);
				g.setFill(Color.gray(0.7));
				g.fillText("PACK", 45, 27);
			}else if(selectedTab == ANIM_TAB){
				g.setFill(Color.GRAY);
				g.fillRect(width/2, 0, width/2, 40);
				g.fillText("PACK", 45, 27);
				g.setFill(Color.gray(0.7));
				g.fillText("ANIMATION", 158, 27);
			}
			
		}
		
		if(selectedTab == ANIM_TAB){
			animTab.render(g);
			return;
		}
		
		for(int i=0; i < images.size(); i++){
			g.setFill(Color.gray(0.7));
			g.fillRect(10, 60 + 35 * i, 260, 30);
			
			// The color for text
			g.setFill(Color.DODGERBLUE);
			
			if(i == selectedID){
				g.setFill(Color.CORNFLOWERBLUE);
				g.fillRect(10, 60 + 35 * i, 260, 30);
				
				// the color for text
				g.setFill(Color.gray(0.9));
			}
			
			// drawing the names
			String name = images.get(i);
			if(name.length() > 30){
				char[] newName = new char[31];
				newName[28] = '.';
				newName[29] = '.';
				newName[30] = '.';
				char[] nameArray = name.toCharArray();
				for(int j=0; j < 28; j++){
					newName[j] = nameArray[j];
				}
				
				name = String.copyValueOf(newName);
			}
			
			g.setFont(Editor.IMG_FONT);
			g.fillText(name, 13, 80 + 35 * i);
		}
	}
	
	public void setEnabled(boolean b){
		scrollPane.setVisible(b);
		enabled = b;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void addImage(String name){
		images.add(name);
		
		if(height < 50 * images.size() + 50){
			height += 100;
			canvas.setHeight(height);
		}
	}
	
	public void setSelectedID(int id){
		selectedID = id;
	}
	
	public String getSelected(){
		if(selectedID < 0) return null;
		
		return images.get(selectedID);
	}
	
	public void removeSelected(){
		images.remove(selectedID);
		selectedID = -1;
	}
	
	public void clear(){
		images.clear();
	}
	
	public boolean isSelected(){
		return selectedID >= 0;
	}
	
	public interface TabEvent{
		public abstract void handleTabChange(int tab);
	}
	
	public int getSelectedTab(){
		return selectedTab;
	}
}
