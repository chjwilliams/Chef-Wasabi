package TileMap;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class Background {

	private Image image;
	private File loadThis;

	private double x;
	private double y;
	private double dx;
	private double dy;

	private double moveScale;

	public Background(String s, double ms) {
		try {

			System.out.println("Loading Background...");
			loadThis = new File(s);
			String path = loadThis.getAbsolutePath();
			image = ImageIO.read(new FileInputStream(path));
			System.out.println("Background successfully loaded.");
			moveScale = ms;
		} catch (Exception e) {
			System.out.println("Background Not Found");
			e.printStackTrace();
		}
	}

	public void setPosition(double x, double y) {
		this.x = (x * moveScale) % GamePanel.WIDTH;
		this.y = (y * moveScale) % GamePanel.HEIGHT;
	}

	public void setVector(double dx, double dy) {
		this.dx = (dx * moveScale) % GamePanel.WIDTH;
		this.dy = (dy * moveScale) % GamePanel.HEIGHT;
	}

	public void update() {
		x += dx;
		y += dy;
	}

	public void draw(Graphics2D g) {
		g.drawImage(image, (int) x, (int) y, null);
		if (x < 0) {
			g.drawImage(image, (int) x + GamePanel.WIDTH, (int) y, null);
		}
		if (x > 0) {
			g.drawImage(image, (int) x - GamePanel.WIDTH, (int) y, null);
		}
	}

}