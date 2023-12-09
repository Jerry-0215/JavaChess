package elements;

import java.util.Objects;

import com.google.common.cache.CacheBuilderSpec;

import elements.Board.Builder;

public abstract class Move 
{
	protected final Board board;
	protected final Piece movedPiece;
	protected final int destinationCoord;
	protected final boolean isFirstMove;
	
	public static final Move NULL_MOVE=new NullMove();
	
	private Move (final Board board, final Piece movedPiece, final int destinationCoord) //Construction for a move
	{
		this.board=board;
		this.movedPiece=movedPiece;
		this.destinationCoord=destinationCoord;
		this.isFirstMove=movedPiece.isFirstMove;
	}
	
	private Move(final Board board, final int destinationCoord)
	{
		this.board=board;
		this.movedPiece=null;
		this.destinationCoord=destinationCoord;
		this.isFirstMove=false;
	}
	
	@Override
	public int hashCode() //Calculating a hashcode for every move
	{
		final int prime=31;
		int result=1;
		result=prime*result+this.destinationCoord;
		result=prime*result+this.movedPiece.hashCode();
		result=prime*result+this.movedPiece.getPiecePosition();
		return result;
	}
	
	@Override
	public boolean equals(final Object other) //An equals method to compare two pieces in terms of object equality (not reference equality like default)
	{
		if (this==other)
			return true;
		if (!(other instanceof Move))
			return false;
		
		final Move otherMove=(Move)other;
		return getCurrentCoord()==otherMove.getCurrentCoord() &&
			   getDestinationCoord()==otherMove.getDestinationCoord() &&
			   getMovedPiece().equals(otherMove.getMovedPiece());
			   
	}
	
	public int getCurrentCoord()
	{
		return this.movedPiece.getPiecePosition();
	}
	
	public int getDestinationCoord()
	{
		return this.destinationCoord;
	}
	
	public Piece getMovedPiece()
	{
		return this.movedPiece;
	}
	
	public boolean isAttack()
	{
		return false;
	}
	
	public boolean isCastlingMove()
	{
		return false;
	}
	
	public Piece getAttackedPiece()
	{
		return null;
	}
	
	public Board getBoard()
	{
		return this.board;
	}
	
	public Board execute() {
		final Builder builder=new Builder();
		for (final Piece piece:this.board.currentPlayer().getActivePieces())  //Looping through all current player's pieces and setting the unmoved ones
		{
			if (!this.movedPiece.equals(piece))
			{
				builder.setPiece(piece);
			}
		}
		
		for (final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()) //Setting all of the opponent's pieces
		{
			builder.setPiece(piece);
		}
		
		builder.setPiece(this.movedPiece.movePiece(this));
		builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());   //Setting the move maker to opponent
		return builder.build();
	}
	

	public static final class MajorMove extends Move //New Class for MajorMove, or a move for a major piece (not pawn)
	{
		public MajorMove(final Board board,final Piece piece,final int destinationCoord) 
		{
			super(board, piece, destinationCoord);
		}
		
		@Override
		public boolean equals(final Object other)
		{
			return this==other || other instanceof MajorMove && super.equals(other);
		}
		
		@Override
        public String toString() 
		{
            return movedPiece.getPieceType().toString()+BoardUtils.getPositionAtCoordinate(this.destinationCoord);
        }
	}
	
	public static class MajorAttackMove extends AttackMove
	{
		public MajorAttackMove(final Board board,final Piece piece,final int destinationCoord, final Piece attackedPiece) 
		{
			super(board, piece, destinationCoord,attackedPiece);
		}
		
		@Override
		public boolean equals(final Object other)
		{
			return this==other || other instanceof MajorAttackMove && super.equals(other);
		}
		
		@Override
        public String toString() 
		{
            return movedPiece.getPieceType()+BoardUtils.getPositionAtCoordinate(this.destinationCoord);
        }
	}
	
	public static class AttackMove extends Move //New Class for an AttackMove, a move to a tile occupied by opponent pieces
	{
		final Piece attackedPiece;
		
		public AttackMove(final Board board,final Piece piece,final int destinationCoord, final Piece attackedPiece) 
		{
			super(board, piece, destinationCoord);
			this.attackedPiece=attackedPiece;
		}

		@Override
		public boolean isAttack()
		{
			return true;
		}
		
		@Override
		public Piece getAttackedPiece()
		{
			return this.attackedPiece;
		}
		
		@Override
		public int hashCode()
		{
			return this.attackedPiece.hashCode()+super.hashCode();
		}
		
		@Override
		public boolean equals(final Object other)
		{
			if (this==other)
				return true;
			if (!(other instanceof AttackMove))
				return false;
			
			final AttackMove otherMove=(AttackMove)other;
			return super.equals(otherMove) &&
				   getAttackedPiece().equals(otherMove.getAttackedPiece());
		}
	}
	
	public static class PawnMove extends Move //New Class for PawnMove
	{

		public PawnMove(final Board board,final Piece piece,final int destinationCoord) 
		{
			super(board, piece, destinationCoord);
		}
		
		@Override
		public boolean equals(final Object other)
		{
			return this==other || other instanceof PawnMove && super.equals(other);
		}
		
		@Override
        public String toString() 
		{
            return BoardUtils.getPositionAtCoordinate(this.destinationCoord);
        }
	}
	
	public static class PawnAttackMove extends AttackMove //New Class for an attacking PawnMove
	{

		public PawnAttackMove(final Board board,final Piece piece,final int destinationCoord,final Piece attackedPiece)
		{
			super(board,piece,destinationCoord,attackedPiece);
		}
		
		@Override
		public boolean equals(final Object other)
		{
			return this==other || other instanceof PawnAttackMove && super.equals(other);
		}
		
		@Override
        public String toString() 
		{
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) + "x" +
                   BoardUtils.getPositionAtCoordinate(this.destinationCoord);
        }
		
	}
	
	public static final class PawnEnPassantAttackMove extends PawnAttackMove //New Class for En Passant
	{
		public PawnEnPassantAttackMove(final Board board,final Piece piece,final int destinationCoord,final Piece attackedPiece)
		{
			super(board,piece,destinationCoord,attackedPiece);
		}
		
		@Override
		public boolean equals(final Object other)
		{
			return this==other || other instanceof PawnEnPassantAttackMove && super.equals(other);
		}
		
		@Override
		public Board execute()
		{
			final Builder builder=new Builder();
			for (final Piece piece:this.board.currentPlayer().getActivePieces())
			{
				if (!this.movedPiece.equals(piece))
				{
					builder.setPiece(piece);
				}
			}
			for (final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces())
			{
				if (!piece.equals(this.attackedPiece))
				{
					builder.setPiece(piece);
				}
			}
			builder.setPiece(this.movedPiece.movePiece(this));
			builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
			return builder.build();
		}
	}
	
	public static class PawnPromotion extends PawnMove //New PawnPromotion class that wraps a move
	{
		final Move decoratedMove;
		final Pawn promotedPawn;
		public PawnPromotion(final Move decoratedMove)
		{
			super(decoratedMove.getBoard(),decoratedMove.getMovedPiece(),decoratedMove.getDestinationCoord());
			this.decoratedMove=decoratedMove;
			this.promotedPawn=(Pawn)decoratedMove.getMovedPiece();
		}
		
		@Override
		public int hashCode()
		{
			return decoratedMove.hashCode()+(31*promotedPawn.hashCode());
		}
		
		@Override
		public boolean equals(final Object other)
		{
			return Objects.equals(decoratedMove, this.decoratedMove) && Objects.equals(promotedPawn, this.promotedPawn);
		}
		
		@Override
		public Board execute()
		{
			final Board pawnMovedBoard=this.decoratedMove.execute();
			final Board.Builder builder=new Builder();
			for (final Piece piece:pawnMovedBoard.currentPlayer().getActivePieces())
			{
				if (!(this.promotedPawn.equals(piece)))
				{
					builder.setPiece(piece);
				}
			}
			
			for (final Piece piece:pawnMovedBoard.currentPlayer().getOpponent().getActivePieces())
			{
				builder.setPiece(piece);
			}
			builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
			builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
			return builder.build();
		}
		
		@Override
		public boolean isAttack()
		{
			return this.decoratedMove.isAttack();
		}
		
		@Override
		public Piece getAttackedPiece()
		{
			return this.decoratedMove.getAttackedPiece();
		}
		
		@Override
		public String toString()
		{
			return BoardUtils.getPositionAtCoordinate(destinationCoord)+"=Q";
		}
	}
	
	public static final class PawnJump extends Move //New Class for Pawnjump, when pawns move forward 2 squares from their starting position
	{

		public PawnJump(final Board board,final Piece piece,final int destinationCoord) 
		{
			super(board, piece, destinationCoord);
		}
		
		@Override
        public Board execute()
		{
            final Builder builder=new Builder();
            
            for(final Piece piece: this.board.currentPlayer().getActivePieces())
            {
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            
            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(piece);
            }
            final Pawn movedPawn=(Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
		
		@Override
		public String toString()
		{
			return BoardUtils.getPositionAtCoordinate(destinationCoord);
		}
		
	}
	
	static abstract class CastleMove extends Move //New abstract Class for castling
	{
		protected final Rook castleRook;
		protected final int castleRookStart;
		protected final int castleRookDestination;
		
		public CastleMove(final Board board,final Piece piece,final int destinationCoord,final Rook castleRook,final int castleRookStart,final int castleRookDestination) 
		{
			super(board, piece, destinationCoord);
			this.castleRook=castleRook;
			this.castleRookStart=castleRookStart;
			this.castleRookDestination=castleRookDestination;
		}
		
		public Rook getCastleRook()
		{
			return this.castleRook;
		}
		
		@Override
		public boolean isCastlingMove()
		{
			return true;
		}
		
		@Override
		public Board execute()
		{
			final Builder builder=new Builder();
			for (final Piece piece:this.board.currentPlayer().getActivePieces())
			{
				if (this.movedPiece.equals(piece) && !this.castleRook.equals(piece))
					builder.setPiece(piece);
			}
			
			for (final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces())
				builder.setPiece(piece);
			
			builder.setPiece(this.movedPiece.movePiece(this));
			builder.setPiece(new Rook(this.castleRook.getPieceAlliance(),this.castleRookDestination));
			builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
			return builder.build();
		}
		
		@Override
		public int hashCode()
		{
			final int prime=31;
			int result=super.hashCode();
			result=prime*result+this.castleRook.hashCode();
			result=prime*result+this.castleRookDestination;
			return result;
		}
		
		@Override
		public boolean equals(final Object other)
		{
			if (this==other)
			{
				return true;
			}
			else if (!(other instanceof CastleMove))
			{
				return false;
			}
			final CastleMove otherCastleMove=(CastleMove)other;
			return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
		}
	}
	
	public static final class KingSideCastleMove extends CastleMove //New concrete Class for King side castling extending CastleMove
	{
		public KingSideCastleMove (final Board board,final Piece piece,final int destinationCoord,final Rook castleRook,final int castleRookStart,final int castleRookDestination) 
		{
			super(board, piece, destinationCoord,castleRook,castleRookStart,castleRookDestination);
		}
		
		@Override
		public boolean equals(final Object other)
		{
			return this==other || other instanceof KingSideCastleMove && super.equals(other);
		}
		
		@Override
		public String toString()
		{
			return "O-O";
		}
	}
	
	public static final class QueenSideCastleMove extends CastleMove //New concrete Class for Queen side castling extending CastleMove
	{

		public QueenSideCastleMove (final Board board,final Piece piece,final int destinationCoord,final Rook castleRook,final int castleRookStart,final int castleRookDestination) 
		{
			super(board, piece, destinationCoord,castleRook,castleRookStart,castleRookDestination);
		}
		
		@Override
		public boolean equals(final Object other)
		{
			return this==other || other instanceof QueenSideCastleMove && super.equals(other);
		}
		
		@Override
		public String toString()
		{
			return "O-O-O";
		}
	}
	
	public static final class NullMove extends Move //New Class for an invalid move
	{
		public NullMove () 
		{
			super(null,65);
		}
		
		@Override
		public Board execute()
		{
			throw new RuntimeException ("Cannot execute null move!");
		}
		
		@Override
		public int getCurrentCoord()
		{
			return -1;
		}
	}
	
	public static class MoveFactory 
	{
		private MoveFactory()
		{
			throw new RuntimeException ("Not instantiable!");
		}
		
		public static Move createMove(final Board board, final int currentCoord, final int destinationCoord)
		{
			for (final Move move:board.currentPlayer().getLegalMoves())
			{
				if (move.getCurrentCoord()==currentCoord && move.getDestinationCoord()==destinationCoord)
				{
					return move;
				}
			}
			return NULL_MOVE;
		}
	}
}
