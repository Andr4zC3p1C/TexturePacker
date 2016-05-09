package com.kingcoder.escape_texture_packer;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import com.kingcoder.escape_texture_packer.ImgHandle.TabEvent;
import com.kingcoder.escape_texture_packer.pack_algorithm.PackTree;
import com.kingcoder.escape_texture_packer.pack_algorithm.Rect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Texture {

	private String name = "new_pack";			// The name of the packed texture
	
	private WritableImage texture;				// The texture image that we write to a file
	private ArrayList<Image> images;			// The images we want to pack in a texture
	private ArrayList<String> keys;				// The keys(names) of the images we want to pack
	private ArrayList<Point> points;			// The coordinates of the images we want to pack on the texture 
	private PackTree packTree;					// The Tree with the bin packing algorithm
	
	private int width = 1, height = 1;			// the width and the height of the texture --> ALWAYS POT (Power Of Two)
	private boolean anim = false;				// boolean if the texture pack is an animation
	private int selectedID = -1;				// ID of the selected image
	private TabEvent tabEvent;					// TabEvent object for calling the handleTabChage method
	
	/*
	 * Animation properties. Animation frames are the images from the pack
	 */
	private ArrayList<Animation> anims;
	private int selectedAnim = 0;
	private long a_timer = System.nanoTime();
	private int counter = 0;
	
	/**
	 * Initializing the lists
	 */
	public Texture(){
		images = new ArrayList<Image>();
		keys = new ArrayList<String>();
		points = new ArrayList<Point>();
		anims = new ArrayList<Animation>();
		
		packTree = new PackTree();
		tabEvent = new TabEvent() {
			public void handleTabChange(int tab) {
				anim = tab == ImgHandle.ANIM_TAB;
			}
		};
	}
	
	/**
	 * Renders the pack to the screen
	 * @param g
	 */
	public void render(GraphicsContext g){
		if(anim){
			if(anims.size() != 0){
				Animation a = anims.get(selectedAnim);
				if(System.nanoTime() - a_timer >= 1000000000 / a.getFPS()){
					String[] frames = a.getFrames();
					int index = getFrameLoc(frames[counter]);
					
					// actual render
					g.drawImage(images.get(index), 300 + ((Main.WIDTH - 300) / 2) - 100, 100);
					
					if(counter < frames.length){
						counter++;
						a_timer = System.nanoTime();
					}else{
						counter = 0;
						if(a.isLooping()){
							a_timer = System.nanoTime();
						}
					}
				}
			}
		}else{
			for(int i=0; i < images.size(); i++){
				Image img = images.get(i);
				Point p = points.get(i);
				
				g.drawImage(img, 330 + p.x + Main.camera.x, 30 + p.y + Main.camera.y);
				if(i==selectedID){
					g.setFill(Editor.SELECTED_COLOR);
					g.fillRect(330 + p.x + Main.camera.x, 30 + p.y + Main.camera.y, img.getWidth(), img.getHeight());
				}
			}
			
			// drawing the green line around the texture
			g.setFill(Color.GREEN);
			g.fillRect(330 + Main.camera.x, 27 + Main.camera.y, width, 3);
			g.fillRect(327 + Main.camera.x, 27 + Main.camera.y, 3, height + 6);
			g.fillRect(330 + Main.camera.x + width, 27 + Main.camera.y, 3, height + 6);
			g.fillRect(330 + Main.camera.x, 27 + Main.camera.y + height + 3, width, 3);
		}
		
	}
	
	/**
	 * Generates the texture, thus writes it to a .png image and generates a .txt file and .anim file if the texture is an animation.
	 * If there are no images to pack it just returns.
	 */
	public void generate(String path){
		// checking if empty
		if(images.size() == 0)
			return;
		
		// CREATING THE DIR
		path = path + "/" + name;
		File file = new File(path);
		
		// Overriding
		if(file.exists()){
			String[] files = file.list();
			for(int i=0; i < files.length; i++){
				File currentFile = new File(file.getPath(),files[i]);
			    currentFile.delete();
			}
			
			file.delete();
		}
		
		file = new File(path);
		file.mkdir();
		
		// CREATING THE IMAGE FILE
		texture = new WritableImage(width, height);
		PixelWriter pw = texture.getPixelWriter();

		// writing the pixels from images to the texture image
		for(int i=0; i < images.size(); i++){
			Image img = images.get(i);
			PixelReader pr = img.getPixelReader();	
			
			int px = points.get(i).x;
			int py = points.get(i).y;
			for(int y = py; y < img.getHeight() + py; y++){
				for(int x = px; x < img.getWidth() + px; x++){
					pw.setArgb(x, y, pr.getArgb(x - px, y - py));
				}
			}
		}
		FileUtil.writeImage(path + "/" + name + ".png", texture);
		
		// write the .tex file
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < images.size(); i++){
			sb.append(keys.get(i) + "," + points.get(i).x + "," + points.get(i).y + "," + (int)images.get(i).getWidth() + "," + (int)images.get(i).getHeight() + "\n");
		}
		
		FileUtil.writeString(path + "/" + name + ".tex", sb.toString());
	}
	
	private int getFrameLoc(String frame){
		for(int i=0; i < keys.size(); i++){
			if(keys.get(i).equals(frame))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Adds an image to the texture.
	 * Calculates the coordinates of the image and manages the size of the texture. The size is always POT(Power Of Two)
	 * @param image
	 * @param key
	 */
	public void addImage(Image image, String key){
		// get the index
		int index = 0;
		for(int i=0; i < images.size(); i++){
			Image img = images.get(i);
			if(img.getWidth() * img.getHeight() > image.getWidth() * image.getHeight())
				index++;
		}
		
		images.add(index, image);
		keys.add(index, key);
		packTexture();
	}
	
	public void removeImage(int id){
		images.remove(id);
		keys.remove(id);
		width = 1;
		height = 1;
		packTexture();
	}
	
	private void packTexture(){
		points.clear();
		packTree.reset();
		packTree.setSize(width, height);
		
		for(int i=0; i < images.size(); i++){
			Rect r = packTree.insertNode(images.get(i), i);
			if(r == null){
				width *= 2;
				height *= 2;
				packTexture();
				return;
			}
			
			points.add(new Point(r.x, r.y));
		}
	}
	
	public void clear(){
		images.clear();
		keys.clear();
		points.clear();
		width = 1;
		height = 1; 
		packTree.reset();
		packTree.setSize(width, height);
		anims.clear();
		selectedID = -1;
		anim = false;
		selectedAnim = 0;
		counter = 0;
	}
	
	public void addAnimation(Animation anim){
		anims.add(anim);
	}
	
	public void removeAnimation(int i){
		anims.remove(i);
	}
	
	public boolean isEmpty(){
		return images.size() == 0;
	}
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setSelected(int id){
		selectedID = id;
	}
	
	public Animation getAnimation(int i){
		return anims.get(i);
	}
	
	public int getIndexOfImage(String key){
		for(int i=0; i < keys.size(); i++){
			if(keys.get(i).equals(key))
				return i;
		}
		
		return -1;
	}
	
	public TabEvent getTabEvent(){
		return tabEvent;
	}
}
