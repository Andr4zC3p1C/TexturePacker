package com.kingcoder.escape_texture_packer;

import java.util.ArrayList;

import com.kingcoder.escape_texture_packer.pack_algorithm.Rect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public class AnimationTab {

	private Rect button;
	private boolean popup = false;
	private int cWidth = 350, cHeight = 298; 
	private boolean editing = false;
	
	private ArrayList<Animation> animations;
	
	public AnimationTab(){
		button = new Rect(125, 60, 50, 30);
	}
	
	public void update(){
		// creating a popup
		if(!popup){ 
			if(Main.in.isMouseButtonClicked(MouseButton.PRIMARY)){
				if(Main.in.mousePos.x > button.x && Main.in.mousePos.x < button.x + button.w){
					if(Main.in.mousePos.y > button.y + 30 && Main.in.mousePos.y < button.y + button.h + 30){
						popUp();
					}
				}
			}
		}
		
		if(editing){
			
		}
	}
	
	public void render(GraphicsContext g){
		g.setFill(Color.CORNFLOWERBLUE);
		g.fillRect(button.x, button.y, button.w, button.h);
		
		if(editing){
			
		}
	}
	
	public void popUp(){
		editing = true;
	}

}
