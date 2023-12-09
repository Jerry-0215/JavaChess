package elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import elements.Move.AttackMove;
import elements.Move.MajorAttackMove;
import elements.Move.MajorMove;

public class Rook extends Piece
{
	private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES= {-8,-1,1,8};//Offsets for possible 1 tile moves for a rook
	
	public Rook(Alliance pieceAlliance,int piecePosition) 
	{
		super(PieceType.ROOK,piecePosition, pieceAlliance,true);
	}
	
	public Rook(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove)
	{
		super(PieceType.ROOK,piecePosition, pieceAlliance,isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMoves(Board board) //Calculating legal moves of a rook (NOTE: Very similar to that of bishop, only tweaks to candidate vectors and exclusions)
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
	
	@Override
	public String toString() //Method from the PieceType enum
	{
		return PieceType.ROOK.toString();
	}
	
	@Override
	public Rook movePiece(Move move) {
		return new Rook(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoord());
	}
	
	private static boolean isFirstColumnExclusion (final int currentPosition,final int candidateOffset) //Method to check whether move is an edge case on column 1
	{
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset==-1);
	}
	
	private static boolean isEighthColumnExclusion (final int currentPosition,final int candidateOffset) //Method to check whether move is an edge case on column 1
	{
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset==1);
	}
}
