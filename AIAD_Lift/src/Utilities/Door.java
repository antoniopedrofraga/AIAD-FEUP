package utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

public class Door implements Drawable {
	int x;
	int y;
	static BufferedImage door;

	public Door(int x, int y){
		this.x=x;
		this.y=y;

		try {
			door = ImageIO.read(new File("img/door.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getY(){
		return y;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public void setX(int x){
		this.x = x;
	}

	@Override
	public void draw(SimGraphics D) {
		D.drawImageToFit(door);
	}
/*	
	public void setDirection(Direction d){
		this.direction = d;
	}
	
	public Direction getDirection(){
		return direction;
	}*/
}
