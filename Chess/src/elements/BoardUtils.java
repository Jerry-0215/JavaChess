package elements;

import java.util.*;

public class BoardUtils 
{
	private BoardUtils() //Stops people from trying to instantiate this class
	{
		throw new RuntimeException("Cannot instantiate this class");
	}
	
	public static final boolean[] FIRST_COLUMN=inColumn(0);
	public static final boolean[] SECOND_COLUMN = inColumn(1);
	public static final boolean[] SEVENTH_COLUMN = inColumn(6);
	public static final boolean[] EIGHTH_COLUMN = inColumn(7);
	
	public static final boolean[] EIGHTH_RANK=inRow(0);
	public static final boolean[] SEVENTH_RANK=inRow(8);
	public static final boolean[] SIXTH_RANK=inRow(16);
	public static final boolean[] FIFTH_RANK=inRow(24);
	public static final boolean[] FOURTH_RANK=inRow(32);
	public static final boolean[] THIRD_RANK=inRow(40);
	public static final boolean[] SECOND_RANK=inRow(48);
	public static final boolean[] FIRST_RANK=inRow(56);
	
	public static final List<String> ALGEBRAIC_NOTATION=initializeAlgebraicNotation();
	public static final Map<String,Integer> POSITION_TO_COORDINATE=initializePositionToCoordinateMap();
	public static final int START_TILE_INDEX = 0;
	
	private static boolean[] inColumn(int columnNumber) //Method that marks all tiles in specified column as true
	{
		final boolean column[]=new boolean[64];
		
		do
		{
			column[columnNumber]=true;
			columnNumber+=8;
		} while (columnNumber<64);
		
			return column;
	}
	
	private static boolean[] inRow (int rowNumber)
	{
		final boolean row[]=new boolean[64];
		
		do
		{
			row[rowNumber]=true;
			rowNumber++;
		} while (rowNumber%8!=0);
		
			return row;
	}
	
	private static List<String> initializeAlgebraicNotation() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
    }
	
	private static Map<String, Integer> initializePositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = START_TILE_INDEX; i < 64; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }
	
	
	static boolean isValidTileCoord(final int coord) //Universal method for all pieces to check whether a tile coord is valid (within range of possible coords)
	{
		return coord>=0 && coord<64;
	}

	public static int getCoordinateAtPosition(final String position)
	{
		return POSITION_TO_COORDINATE.get(position);
	}
	
	public static String getPositionAtCoordinate(final int coordinate)
	{
		return ALGEBRAIC_NOTATION.get(coordinate);
	}
}
