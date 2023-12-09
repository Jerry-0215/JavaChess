package elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import elements.Move.AttackMove;
import elements.Move.MajorMove;
import elements.Piece.PieceType;

public class King extends Piece
{
	private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES= {-9,-8,-7,-1,1,7,8,9}; //Possible offsets for king moves
	
	public King(Alliance pieceAlliance,int piecePosition) 
	{
		super(PieceType.KING,piecePosition, pieceAlliance,true);
	}
	
	public King(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove)
	{
		super(PieceType.KING,piecePosition, pieceAlliance,isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMoves(Board board) 
	{
		final List<Move> legalMoves=new ArrayList<>(); //Arraylist for all legal moves
		
		for (int currentCandidateOffset:CANDIDATE_MOVE_VECTOR_COORDINATES) //Looping through all possible offset
		{
			final int candidateDestinationCoord=this.piecePosition+currentCandidateOffset; //Setting candidateDestinationCoord to current piecePosition + offset
			
			if (isFirstColumnExclusion(this.piecePosition,currentCandidateOffset) || isEighthColumnExclusion(this.piecePosition,currentCandidateOffset)) 
				continue; //Skipping exclusions
			
			if (BoardUtils.isValidTileCoord(candidateDestinationCoord)) //Checking if current position is valid
			{
				final Tile candidateDestinationTile=board.getTile(candidateDestinationCoord);
				
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
						legalMoves.add(new AttackMove(board,this,candidateDestinationCoord,pieceAtDestination));
					}
				}
			}
		}
		
		return Collections.unmodifiableList(legalMoves); //Returning list of legal moves
	}
	
	@Override
	public String toString() //Method from the PieceType enum
	{
		return PieceType.KING.toString();
	}
	
	@Override
	public King movePiece(Move move) {
		return new King(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoord());
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) //Boolean method to exclude edge cases on first column that doesn't follow the possible offsets rule
	{
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset==-9||candidateOffset==-1||candidateOffset==7);
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) //Boolean method to exclude edge cases on second column that doesn't follow the possible offsets rule
	{
		return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset==-7 || candidateOffset==1 || candidateOffset==9);
	}
}
