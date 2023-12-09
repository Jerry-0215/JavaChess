package players;

import java.util.*;

import elements.Alliance;
import elements.Board;
import elements.Move;
import elements.Piece;
import elements.Rook;
import elements.Tile;

public class WhitePlayer extends Player{

	public WhitePlayer(final Board board,final Collection<Move> whiteStandardLegalMoves,final Collection<Move> blackStandardLegalMoves) //Constructor for a player with white pieces
	{
		super(board,whiteStandardLegalMoves,blackStandardLegalMoves);
	}

	@Override
	public Collection<Piece> getActivePieces() 
	{
		return this.board.getWhitePieces();
	}
	
	@Override
	public Alliance getAlliance() 
	{
		return Alliance.WHITE;
	}

	@Override
	public Player getOpponent() 
	{
		return this.board.blackPlayer();
	}

	@Override
	protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,final Collection<Move> opponentLegals) //Method to calculate legal castling moves
	{
		final List<Move> kingCastles=new ArrayList<>();
		
		if (this.playerKing.isFirstMove()&&!this.isInCheck()) 
		{
			if (!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()) //White king side Castle
			{
				final Tile rookTile=this.board.getTile(63);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove())
				{
					if (Player.calculateAttacksOnTile(61, opponentLegals).isEmpty() && 
						Player.calculateAttacksOnTile(62, opponentLegals).isEmpty() &&
						rookTile.getPiece().getPieceType().isRook())
						kingCastles.add(new Move.KingSideCastleMove(this.board, this.playerKing, 62, (Rook)rookTile.getPiece(), rookTile.getTileCoord(), 61));
				}
			}
			
			if (!this.board.getTile(59).isTileOccupied() && !this.board.getTile(58).isTileOccupied() && !this.board.getTile(57).isTileOccupied()) //Black queen side Castle
			{
				final Tile rookTile=this.board.getTile(56);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
						Player.calculateAttacksOnTile(58, opponentLegals).isEmpty() && 
						Player.calculateAttacksOnTile(59, opponentLegals).isEmpty() &&
						rookTile.getPiece().getPieceType().isRook())
				{
					kingCastles.add(new Move.QueenSideCastleMove(this.board, this.playerKing, 58, (Rook)rookTile.getPiece(), rookTile.getTileCoord(), 59));
				}
			}
		}
		
		return Collections.unmodifiableCollection(kingCastles);
	}
	
}
