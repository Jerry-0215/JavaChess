package elements;

import java.util.*;

import elements.Move.*;
import elements.Piece.PieceType;

public class Knight extends Piece
{
	private final static int[] CANDIDATE_MOVE_COORDINATES= {-17,-15,-10,-6,6,10,15,17}; //For every knight position, these are all the possible distances for a legal move's tile
	
	public Knight(Alliance pieceAlliance,int piecePosition) //Constructor for knight
	{
		super(PieceType.KNIGHT,piecePosition, pieceAlliance,true);
	}
	
	public Knight(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove)
	{
		super(PieceType.KNIGHT,piecePosition, pieceAlliance,isFirstMove);
	}
	
	@Override
	public Collection<Move> calculateLegalMoves (Board board)
	{
		final List <Move> legalMoves=new ArrayList<>();  //Creating an ArrayList for legal moves
		
		for (final int candidateOffset:CANDIDATE_MOVE_COORDINATES) //Looping through all candidate moves from above
		{
			final int candidateDestinationCoord=this.piecePosition+candidateOffset; //Initializing the candidate coord to piece position + possible offset
			if (BoardUtils.isValidTileCoord(candidateDestinationCoord))
			{
				if (isFirstColumnExclusion(this.piecePosition,candidateOffset) //Exclusion for edge cases with invalid moves with knights on first,second,seventh and eighth column
					|| isSecondColumnExclusion(this.piecePosition,candidateOffset)
					|| isSeventhColumnExclusion(this.piecePosition,candidateOffset)
					|| isEighthColumnExclusion(this.piecePosition,candidateOffset)) 
				{
					continue;
				}
				
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
				}
			}
		}
		return Collections.unmodifiableList(legalMoves);   //Returns list of legal moves
	}
	
	@Override
	public String toString() //Method from the PieceType enum
	{
		return PieceType.KNIGHT.toString();
	}
	
	@Override
	public Knight movePiece(Move move) {
		return new Knight(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoord());
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) //Boolean method to exclude edge cases on first column that doesn't follow the possible offsets rule
	{
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset==-17||candidateOffset==-10||candidateOffset==6||candidateOffset==15);
	}
	
	private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) //Boolean method to exclude edge cases on second column that doesn't follow the possible offsets rule
	{
		return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset==-10 || candidateOffset==6);
	}
	
	private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) //Boolean method to exclude edge cases on seventh column that doesn't follow the possible offsets rule
	{
		return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset==-6 || candidateOffset==10);
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) //Boolean method to exclude edge cases on eighth column that doesn't follow the possible offsets rule
	{
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset==-15||candidateOffset==-6||candidateOffset==10||candidateOffset==17);
	}
}
