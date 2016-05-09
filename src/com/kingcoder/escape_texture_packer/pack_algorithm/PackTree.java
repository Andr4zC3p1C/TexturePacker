package com.kingcoder.escape_texture_packer.pack_algorithm;

import javafx.scene.image.Image;

public class PackTree {
	
	private Node root;
	
	public PackTree(){
		reset();
	}
	
	public Rect insertNode(Image img, int id){
		Rect imgRect = new Rect(0,0, (int)img.getWidth(), (int)img.getHeight());
		Node n = root.insert(imgRect, id);
		if(n == null)
			return null;
		
		return n.rect;
	}
	
	public void reset(){
		root = new Node();
		root.rect = new Rect(0,0,0,0);
	}
	
	public void setSize(int w, int h){
		root.rect.w = w;
		root.rect.h = h;
	}
}
