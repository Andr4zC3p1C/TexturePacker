package com.kingcoder.escape_texture_packer;

import java.awt.Point;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


public class Input{

	private ArrayList<String> keyEvents = new ArrayList<String>();
	private ArrayList<String> mouseEvents = new ArrayList<String>();
	private boolean clicked = false;
	public Point mousePos = new Point();
	
	public Input(Scene scene){
		scene.setOnKeyPressed(e -> keyPressed(e));
		scene.setOnKeyReleased(e -> keyReleased(e));
		scene.setOnMousePressed(e -> mousePressed(e));
		scene.setOnMouseReleased(e -> mouseReleased(e));
		scene.setOnMouseMoved(e -> mouseMoved(e));
		scene.setOnMouseDragged(e -> mouseMoved(e));
	}
	
	public void keyPressed(KeyEvent e){
		String code = e.getCode().toString();
		if(!keyEvents.contains(code))
			keyEvents.add(code);
	}
	
	public void keyReleased(KeyEvent e){
		String code = e.getCode().toString();
		keyEvents.remove(code);
	}
	
	public void mousePressed(MouseEvent e){
		String code = e.getButton().toString();
		if(!mouseEvents.contains(code))
			mouseEvents.add(code);
	}
	
	public void mouseReleased(MouseEvent e){
		String code = e.getButton().toString();
		mouseEvents.remove(code);
		clicked = false;
	}
	
	public void mouseMoved(MouseEvent e){
		mousePos.x = (int)e.getX();
		mousePos.y = (int)e.getY();
	}
	
	public boolean isKeyPressed(KeyCode c){
		return keyEvents.contains(c.toString());
	}
	
	public boolean isMouseButtonPressed(MouseButton b){
		return mouseEvents.contains(b.toString());
	}
	
	public boolean isMouseButtonClicked(MouseButton b){
		boolean result = mouseEvents.contains(b.toString()) && !clicked;
		if(result)
			clicked = true;
		
		return result;
	}
	
}
