package elements;

import java.util.*;

import elements.Move.*;
import elements.Piece.*;

public class Pawn extends Piece
{
	private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES= {7,8,9,16}; //Possible offsets for pawn moves
	
	public Pawn(Alliance pieceAlliance,int piecePosition) //Constructor for Pawn
	{
		super(PieceType.PAWN,piecePosition, pieceAlliance,true);
	}
	
	public Pawn(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove)
	{
		super(PieceType.PAWN,piecePosition, pieceAlliance,isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMoves(Board board) //Method to calculate legal moves of a pawn
	{
		final List<Move> legalMoves=new ArrayList<>(); //Arraylist for all legal moves
		
		for (int currentCandidateOffset:CANDIDATE_MOVE_VECTOR_COORDINATES) //Looping through all possible offset
		{
			final int candidateDestinationCoord=this.piecePosition+currentCandidateOffset*this.pieceAlliance.getdirection(); //Setting candidateDestinationCoord to current piecePosition + offset
			
			if (!BoardUtils.isValidTileCoord(candidateDestinationCoord)) //Checking if current position is valid
				continue;
			
			if (currentCandidateOffset==8 && !board.getTile(candidateDestinationCoord).isTileOccupied()) //Checking if it's legal to push the pawn one square 
			{
				if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoord))
				{
					legalMoves.add(new PawnPromotion(new PawnMove(board,this,candidateDestinationCoord)));
				}
				else
				{
					legalMoves.add(new PawnMove(board,this,candidateDestinationCoord)); 
				}
			}
			else if (currentCandidateOffset==16 && this.isFirstMove() && 
					((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.getPieceAlliance().isBlack()) || 
					(BoardUtils.SECOND_RANK[this.piecePosition] && this.getPieceAlliance().isWhite()))) //Checking if it's legal to push the pawn forward 2 squares (first move of the pawn)
			{
				final int skippedCoord=this.piecePosition+this.pieceAlliance.getdirection()*8;
				if (!board.getTile(skippedCoord).isTileOccupied() && !board.getTile(candidateDestinationCoord).isTileOccupied())
					legalMoves.add(new PawnJump(board,this,candidateDestinationCoord));	
			}
			
			else if (currentCandidateOffset==7 &&
					!(BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
				    (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) //Checking if it's legal to capture diagonally to the right
			{
				if (board.getTile(candidateDestinationCoord).isTileOccupied())
				{
					final Piece pieceOnCandidate=board.getTile(candidateDestinationCoord).getPiece();
					if (this.pieceAlliance!=pieceOnCandidate.pieceAlliance)
					{
						if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoord))
						{
							legalMoves.add(new PawnPromotion(new PawnAttackMove(board,this,candidateDestinationCoord,pieceOnCandidate)));
						}
						else
						{
							legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoord,pieceOnCandidate));
						}
					}
				}
				
				else if (board.getEnPassantPawn()!=null && 
						board.getEnPassantPawn().getPiecePosition()==(this.piecePosition+(this.pieceAlliance.getOppositeDirection()))) //Checking whether there is a valid En Passant move
				{
					final Piece pieceOnCandidate=board.getEnPassantPawn();
					if (this.pieceAlliance!=pieceOnCandidate.getPieceAlliance())
					{
						legalMoves.add(new PawnEnPassantAttackMove(board,this,candidateDestinationCoord,pieceOnCandidate));
					}
				}
			}
			
			else if (currentCandidateOffset==9 &&
					!(BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
					 (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) //Checking if it's legal to capture diagonally to the left
			{
				if (board.getTile(candidateDestinationCoord).isTileOccupied())
				{
					final Piece pieceOnCandidate=board.getTile(candidateDestinationCoord).getPiece();
					if (this.pieceAlliance!=pieceOnCandidate.pieceAlliance)
					{
						if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoord))
						{
							legalMoves.add(new PawnPromotion(new PawnAttackMove(board,this,candidateDestinationCoord,pieceOnCandidate)));
						}
						else
						{
							legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoord,pieceOnCandidate));
						}
					}
				}
				
				else if (board.getEnPassantPawn()!=null && 
						board.getEnPassantPawn().getPiecePosition()==(this.piecePosition-(this.pieceAlliance.getOppositeDirection()))) //Checking whether there is a valid En Passant move
				{
					final Piece pieceOnCandidate=board.getEnPassantPawn();
					if (this.pieceAlliance!=pieceOnCandidate.getPieceAlliance())
					{
						legalMoves.add(new PawnEnPassantAttackMove(board,this,candidateDestinationCoord,pieceOnCandidate));
					}
				}
			}
				
		}
		
		return Collections.unmodifiableList(legalMoves); //Returning list of legal moves
	}
	@Override
	public String toString() //Method from the PieceType enum
	{
		return PieceType.PAWN.toString();
	}
	
	@Override
	public Pawn movePiece(Move move) {
		return new Pawn(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoord());
	}
	
	public Piece getPromotionPiece() //NOTE: for simplicity, pawns will always promote to a Queen
	{
		return new Queen(this.pieceAlliance,this.piecePosition,false);
	}
}
