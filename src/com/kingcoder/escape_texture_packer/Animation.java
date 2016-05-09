package com.kingcoder.escape_texture_packer;

import java.util.ArrayList;

public class Animation {

	private String name;
	private ArrayList<String> frames;
	private int fps = 0;
	private boolean looping = false;
	
	public Animation(){
		frames = new ArrayList<String>();
	}
	
	public void addFrame(String frame){
		frames.add(frame);
	}
	
	public boolean removeFrame(String frame){
		for(int i=0; i < frames.size(); i++){
			if(frames.get(i).equals(frame)){
				frames.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	public void setFPS(int fps){
		this.fps = fps;
	}
	
	public void setLooping(boolean looping){
		this.looping = looping;
	}
	
	public String[] getFrames(){
		String[] frames = new String[this.frames.size()];
		for(int i=0; i < frames.length; i++){
			frames[i] = this.frames.get(i);
		}
		
		return frames;
	}
	
	public int getFPS(){
		return fps;
	}
	
	public boolean isLooping(){
		return looping;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
}
