package elements;

import java.util.*;

public abstract class Piece 
{
	protected final PieceType pieceType;
	protected final int piecePosition; //Coords of the piece
	protected final Alliance pieceAlliance; //Color of piece
	protected final boolean isFirstMove;
	private final int cachedHashCode;
	
	Piece (final PieceType pieceType,final int piecePosition, final Alliance pieceAlliance,final boolean isFirstMove) //Piece constructor, passing in piecePosition and piece ALlaince
	{
		this.pieceType=pieceType;
		this.piecePosition=piecePosition;
		this.pieceAlliance=pieceAlliance;
		this.isFirstMove=isFirstMove;
		this.cachedHashCode=computeHashCode();
	}
	
	private int computeHashCode() //Method to generate a hashcode for each piece
	{
		int result=pieceType.hashCode();
		result=31*result+pieceAlliance.hashCode();
		result=31*result+piecePosition;
		result=31*result+(isFirstMove? 1:0);
		return result;
	}

	@Override
	public boolean equals(final Object other) //An equals method to compare two pieces in terms of object equality (not reference equality like default)
	{
		if (this==other)
			return true;
		if (!(other instanceof Piece))
			return false;
		
		final Piece otherPiece=(Piece)other;
		return pieceAlliance==otherPiece.getPieceAlliance() && pieceType==otherPiece.getPieceType() &&
			   piecePosition==otherPiece.getPiecePosition() && isFirstMove==otherPiece.isFirstMove;
	}
	
	@Override
	public int hashCode() 
	{
		return this.cachedHashCode;
	}
	
	public int getPiecePosition()
	{
		return this.piecePosition;
	}
	
	public PieceType getPieceType()
	{
		return this.pieceType;
	}
	
	public int getPieceValue()
	{
		return this.pieceType.getPieceValue();
	}
	
	public abstract Piece movePiece (Move move);
	
	public Alliance getPieceAlliance()
	{
		return this.pieceAlliance;
	}
	
	public boolean isFirstMove()
	{
		return this.isFirstMove;
	}
	
	public abstract Collection<Move> calculateLegalMoves (final Board board); //A method that will return a list of all legal moves in a position
	
	public enum PieceType //Enum to convert each type of piece into their specified format
	{
		PAWN(100,"P") {
			@Override
			public boolean isKing() 
			{
				return false;
			}

			@Override
			public boolean isRook() 
			{
				return false;
			}
		},
		KNIGHT(300,"N") {
			@Override
			public boolean isKing() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isRook() 
			{
				return false;
			}
		},
		BISHOP(300,"B") {
			@Override
			public boolean isKing() 
			{
				return false;
			}
			
			@Override
			public boolean isRook() 
			{
				return false;
			}
		},
		ROOK(500,"R") {
			@Override
			public boolean isKing() 
			{
				return false;
			}
			
			@Override
			public boolean isRook() 
			{
				return true;
			}
		},
		QUEEN(900,"Q") {
			@Override
			public boolean isKing() 
			{
				return false;
			}
			
			@Override
			public boolean isRook() 
			{
				return false;
			}
		},
		KING(10000,"K") {
			@Override
			public boolean isKing() 
			{
				return true;
			}
			
			@Override
			public boolean isRook() 
			{
				return false;
			}
		};
			
		private String pieceName;
		private int pieceValue;
		
		PieceType(final int pieceValue,final String pieceName)
		{
			this.pieceName=pieceName;
			this.pieceValue=pieceValue;
		}
		
		@Override
		public String toString()
		{
			return this.pieceName;
		}
		
		public int getPieceValue()
		{
			return this.pieceValue;
		}
		public abstract boolean isKing();
		public abstract boolean isRook();
	}
}
