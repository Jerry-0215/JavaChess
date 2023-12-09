package elements;

import java.util.*;

public abstract class Tile //NOTE: Following suggestion in the tutorial, most parts of this class is immutable
{
	protected final int tileCoord; //Coordinate of the tile
	
	Tile (int tileCoord)      //Constructor for the tile
	{
		this.tileCoord=tileCoord;
	}
	
	private static final Map<Integer, EmptyTile> EMPTY_TILES_Cache=createAllPossibleEmptyTiles(); //Creating a map of empty tiles using method below
	
	private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() //This method creates a map of corresponding coordinates and tiles
	{
		final Map<Integer, EmptyTile> emptyTileMap=new HashMap(); //Creates Hashmap with integer(coord) and tile
		
		for (int i=0;i<64;i++) //Adds all 64 empty tiles to the map
			emptyTileMap.put(i,new EmptyTile(i));
		
		return Collections.unmodifiableMap(emptyTileMap); //Returns the generated map and making it immutable
	}
	
	public static Tile createTile (final int tileCoord,final Piece piece) //This method creates an OccupiedTile or Empty_Tiles depending on whether the tile has a piece on it
	{
		return piece!=null? new OccupiedTile(tileCoord, piece):EMPTY_TILES_Cache.get(tileCoord);
	}
	
	public abstract boolean isTileOccupied(); //Boolean method for checking whether the tile is empty
	
	public abstract Piece getPiece(); //Method for getting the piece on tile
	
	public int getTileCoord() //Getter for tile coord
	{
		return this.tileCoord;
	}
	
	public static final class EmptyTile extends Tile  //IMPORTANT: This is a new class called Empty Tile that extends the Tile class
	{
		EmptyTile (final int coordinate) //Takes the tile coordinate
		{
			super(coordinate);
		}
		
		@Override
		public boolean isTileOccupied() //Returns false for the isTileOccupied method as this is an empty tile
		{
			return false;
		}
		
		@Override
		public String toString() //Returns a hyphen to represent a blank tile as string
		{
			return "-";
		}
		
		@Override
		public Piece getPiece() //Returns null for the getpiece method as this is an empty tile
		{
			return null;
		}
	}
	
	public static final class OccupiedTile extends Tile //IMPORTANT: This is a new class called Occupied Tile that extends the Tile class
	{
		private final Piece pieceOnTile;
		
		private OccupiedTile(int tileCoord,Piece pieceOnTile) //Takes the tile coordinate and piece on tile
		{
			super(tileCoord);
			this.pieceOnTile=pieceOnTile;
		}
		
		@Override
		public boolean isTileOccupied() //Returns true for the isTileOccupied method as this is an occupied tile
		{
			return true;
		}
		
		@Override 
		public String toString()  //Returns the string for the piece on tile in upper or lower case depending on its alliance
		{
			return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase():
				   getPiece().toString();
		}
		
		@Override
		public Piece getPiece() //Returns the piece on this tile
		{
			return this.pieceOnTile;
		}
	}
}

