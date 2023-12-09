package players;

import java.util.*;
import java.util.stream.*;

import elements.*;

public abstract class Player 
{
	protected final Board board;
	protected final King playerKing;
	protected final Collection<Move> legalMoves;
	private final boolean isInCheck;
	
	Player(final Board board,final Collection<Move> legalMoves,final Collection<Move>opponentMoves) //Constructor for Player
	{
		this.board=board;
		this.playerKing=establishKing();
		this.legalMoves=Stream.concat(legalMoves.stream(), calculateKingCastles(legalMoves,opponentMoves).stream()).collect(Collectors.toList());; 
		this.isInCheck=!Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(),opponentMoves).isEmpty(); //Checks whether king is in check by using method below
	}
	
	public King getPlayerKing()
	{
		return this.playerKing;
	}
	
	public Collection<Move> getLegalMoves()
	{
		return this.legalMoves;
	}
	
	public static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> opponentMoves) //Method that checks all tiles under attack by opponent pieces
	{
		final List<Move> attackMoves=new ArrayList<>();
		for (final Move move:attackMoves)
		{
			if (piecePosition==move.getDestinationCoord())
				attackMoves.add(move);
		}
		return Collections.unmodifiableList(attackMoves); 
	}

	private King establishKing()   //Method to establish the King and make sure the player has a king
	{
		for(final Piece piece : getActivePieces()){
            if(piece.getPieceType().isKing()){
            	return (King) piece;
                
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board");
	}
	
	public boolean isMoveLegal (final Move move)
	{
		return this.legalMoves.contains(move);
	}
	
	public boolean isInCheck()
	{
		return this.isInCheck;
	}
	
	public boolean isInCheckmate()
	{
		return this.isInCheck && !hasEscapeMoves();
	}
	
	public boolean isInStalemate()
	{
		return !this.isInCheck && !hasEscapeMoves();
	}
	
	public boolean isCastled()
	{
		return false;
	}
	
	protected boolean hasEscapeMoves() //Checking whether a king has escape moves by testing moves and checking whether that move was done successfully
	{
		for (final Move move:this.legalMoves)
		{
			final MoveTransition transition=makeMove(move);
			if (transition.getMoveStatus().isDone())
				return true;
		}
		return false;
	}
	
	
	public MoveTransition makeMove(final Move move)     //A method that takes a move and returns move transitions with different status
	{
		if (!isMoveLegal(move))       //Returning the same board and a status of illegal move
			return new MoveTransition(this.board,move,MoveStatus.ILLEGAL_MOVE);
		
		final Board transitionBoard=move.execute();  //execute move if it is legal
		
		
		final Collection<Move> kingAttacks=Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(), 
										   transitionBoard.currentPlayer().getLegalMoves());  //Calculating whether there is an attack on the king of the player who just made the move
		
		if (!kingAttacks.isEmpty())  //Returns the same board and a status of leaving player in check
			return new MoveTransition(this.board,move,MoveStatus.LEAVES_PLAYER_IN_CHECK);
		
		return new MoveTransition(transitionBoard,move,MoveStatus.DONE);  //If it passed all prior cases, return a new move transition with the new board and status of done
	}
	
	public abstract Collection<Piece> getActivePieces();
	public abstract Alliance getAlliance();
	public abstract Player getOpponent();
	
	protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);
}
