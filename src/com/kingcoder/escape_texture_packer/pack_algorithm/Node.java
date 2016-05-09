package com.kingcoder.escape_texture_packer.pack_algorithm;

public class Node {
	
	public Node left, right;
	public Rect rect;
	public int imageID = -1;
	
	public Node insert(Rect r, int id){
		if(left != null && right != null){
			Node newNode = left.insert(r, id);
			if(newNode != null)
				return newNode;
			
			return right.insert(r, id);
		}
		
		// an image already in this node
		if(imageID != -1)
			return null;
		
		//doesn't fit
		if(r.w > rect.w || r.h > rect.h)
			return null;
		
		// fits just right
		if(r.w == rect.w && r.h == rect.h){
			imageID = id;
			return this;
		}
		
		left = new Node();
		right = new Node();
		
		int dw = rect.w - r.w;
		int dh = rect.h - r.h;
		
		if(dw > dh){
			left.rect = new Rect(rect.x, rect.y, r.w, rect.h);
			right.rect = new Rect(rect.x + r.w, rect.y, rect.w - r.w, rect.h);
		}else{
			left.rect = new Rect(rect.x, rect.y, rect.w, r.h);
			right.rect = new Rect(rect.x, rect.y + r.h, rect.w, rect.h - r.h);
		}
		
		return insert(r, id);
	}
	
}
