package com.kingcoder.escape_texture_packer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

public class FileUtil {

	public static void writeString(String path, String data){
		try {
			PrintWriter out = new PrintWriter(path);
			out.print(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeImage(String path, WritableImage img){
		try{
			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(path));
		}catch(IOException e){}
	}
	
}
