package TileMap;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class TileMap {

	private File loadThis;
	private FileInputStream in;
	
	//position
	private double x;
	private double y;
	
	//bounds
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;
	
	//follows player smoothly
	private double tween;
	
	//map
	private int[][] map;
	private int tileSize;
	private int numRows;
	private int numCols;
	private int width;
	private int height;
	
	//Tile Set
	private BufferedImage tileset;
	private int numTilesAcross;
	private Tile[][] tiles;
	
	//drawing bounds
	private int rowOffset;	//which row to start drawing
	private int colOffset;	//which col to start drawing
	private int numRowsToDraw;
	private int numColsToDraw;
	
	public TileMap(int tileSize){
		this.tileSize = tileSize;
		numRowsToDraw = GamePanel.HEIGHT/ tileSize + 2;
		numColsToDraw = GamePanel.WIDTH/ tileSize + 2;
		tween = 1;
	}
	
	public void loadTiles(String s) {
		
		try{
			System.out.println("Load Level 1 Tiles...");
			loadThis = new File(s);
			String path = loadThis.getAbsolutePath();
			tileset = ImageIO.read(new FileInputStream(path));
			numTilesAcross = tileset.getWidth() / tileSize;
			tiles = new Tile [2] [numTilesAcross];
			
			BufferedImage subimage;
			for(int col = 0; col < numTilesAcross; col++){
				subimage = tileset.getSubimage(col*tileSize, 0, tileSize,tileSize);
				tiles[0][col] = new Tile(subimage, Tile.NORMAL);
				subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize,tileSize);
				tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
			}
			
			System.out.println("Level 1 Tiles successfully loaded.");
		}
		catch(Exception e){
			System.out.println("Level 1  Tiles failed to load");
			e.printStackTrace();
		}
	}
	
	public void loadMap(String s) {
		try{
			System.out.println("Loading Level 1 Map...");
			loadThis = new File(s);
			String path = loadThis.getAbsolutePath();
			//in = getClass().getResourceAsStream(path);
			in = new FileInputStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			//
			numCols = Integer.parseInt(br.readLine());
			numRows = Integer.parseInt(br.readLine());
			map = new int [numRows] [numCols];
			width = numCols * tileSize;
			height = numRows * tileSize;
			
			xmin = GamePanel.WIDTH - width;
			xmax = 0;
			ymin = GamePanel.HEIGHT - height;
			ymax = 0;
			
			String delims = "\\s+";
			for(int row = 0; row < numRows; row++){
				String line = br.readLine();
				String[] tokens = line.split(delims);
				for(int col = 0; col < numCols; col++){
					map[row][col] = Integer.parseInt(tokens[col]);
				}
			}
			System.out.println("Level 1 Map successfully loaded.");
		}
		catch(Exception e){
			System.out.println("Level 1  Map failed to load");
			e.printStackTrace();
		}
		
	}
	
	public int getTileSize(){
		return tileSize;
	}
	
	public int getx(){
		return (int)x;
	}
	
	public int gety(){
		return (int)y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getType(int row, int col){
		int rc = map[row][col];
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].getType();
	}
	
	public void setPosition(double x , double y){
		this.x += (x - this.x) * tween;
		this.y += (y - this.y) * tween;
		
		fixBounds();
		
		colOffset = (int)-this.x / tileSize;
		rowOffset = (int)-this.y / tileSize;
	}
	
	private void fixBounds(){
		if(x < xmin)
			x = xmin;
		if(y < ymin)
			y = ymin;
		if(x > xmax)
			x = xmax;
		if(y > ymax)
			y = ymax;
	}
	
	public void draw(Graphics2D g){
		
		for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++){
			if(row >= numRows)
				break;
			for(int col = colOffset; col< colOffset + numColsToDraw; col++){
				if(col >= numCols)
					break;
				
				if(map[row][col] == 0)
					continue;
				
				int rc = map[row][col];
				int r = rc / numTilesAcross;
				int c = rc % numTilesAcross;
				
				g.drawImage(tiles[r][c].getImage(), (int)x + col* tileSize, (int)y + row * tileSize, null);
			}
		}
	}
}
