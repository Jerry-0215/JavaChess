package elements;

import java.util.*;

import elements.Move.*;

public class Bishop extends Piece
{
	private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES= {-9,-7,7,9}; //Offsets for possible 1-square diagonal moves for a bishop
	
	public Bishop(Alliance pieceAlliance,int piecePosition) //Constructor for Bishop
	{
		super(PieceType.BISHOP,piecePosition, pieceAlliance, true);
	}
	
	public Bishop(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove)
	{
		super(PieceType.BISHOP,piecePosition, pieceAlliance,isFirstMove);
	}
	
	
	@Override
	public Collection<Move> calculateLegalMoves(Board board) //Calculating legal moves of a bishop
	{
		final List<Move> legalMoves=new ArrayList<>(); //Arraylist for all legal moves
		
		for (int candidateCoordinateOffset:CANDIDATE_MOVE_VECTOR_COORDINATES) //Looping through all possible offsets
		{
			int candidateDestinationCoord=piecePosition; //Setting candidateDestinationCoord to current piecePosition
			
			while (BoardUtils.isValidTileCoord(candidateDestinationCoord)) //Checking if current position is valid
			{
				if (isFirstColumnExclusion(candidateDestinationCoord,candidateCoordinateOffset) ||  //checking whether the move is an exclusion
					isEighthColumnExclusion(candidateDestinationCoord,candidateCoordinateOffset))
				{
					break;
				}
				
				candidateDestinationCoord+=candidateCoordinateOffset; //Applying offset to current position
				
				if (BoardUtils.isValidTileCoord(candidateDestinationCoord)) //Checking if destination coord is valid
				{
					final Tile candidateDestinationTile=board.getTile(candidateDestinationCoord);  //Assigning the candidate tile to a variable
					
					if (!candidateDestinationTile.isTileOccupied()) //Add major move if candidate tile is empty
					{
						legalMoves.add(new MajorMove(board,this,candidateDestinationCoord));
					}
					else //Add attack move if candidate tile contains enemy piece (opposite alliance)
					{
						final Piece pieceAtDestination=candidateDestinationTile.getPiece();
						final Alliance pieceAlliance=pieceAtDestination.getPieceAlliance();
						
						if (this.pieceAlliance!=pieceAlliance)
						{
							legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoord,pieceAtDestination));
						}
						break; //Important: breaking out of the loop if we hit a tile with another piece (bishop can't move any farther)
					}
					
				}
			}
		}
		
		return Collections.unmodifiableList(legalMoves);
	}
	
	@Override    //Method from the PieceType enum
	public String toString()
	{
		return PieceType.BISHOP.toString();
	}
	
	private static boolean isFirstColumnExclusion (final int currentPosition,final int candidateOffset) //Method to check whether move is an edge case on column 1
	{
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset==-9 || candidateOffset==7);
	}
	
	private static boolean isEighthColumnExclusion (final int currentPosition,final int candidateOffset) //Method to check whether move is an edge case on column 1
	{
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset==9 || candidateOffset==-7);
	}


	@Override
	public Bishop movePiece(Move move) {
		return new Bishop(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoord());
	}
}
