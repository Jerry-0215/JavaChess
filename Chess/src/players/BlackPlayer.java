package players;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import elements.Alliance;
import elements.Board;
import elements.Move;
import elements.Piece;
import elements.Rook;
import elements.Tile;

public class BlackPlayer extends Player{

	public BlackPlayer(final Board board,final Collection<Move> whiteStandardLegalMoves,final Collection<Move> blackStandardLegalMoves) //Constructor for a player with black pieces
	{
		super(board,blackStandardLegalMoves,whiteStandardLegalMoves);
	}

	@Override
	public Collection<Piece> getActivePieces() 
	{
		return this.board.getBlackPieces();
	}
	
	@Override
	public Alliance getAlliance() 
	{
		return Alliance.BLACK;
	}

	@Override
	public Player getOpponent() 
	{
		return this.board.whitePlayer();
	}

	@Override
	protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,final Collection<Move> opponentLegals) //Method to calculate legal castling moves
	{
		final List<Move> kingCastles=new ArrayList<>();
		
		if (this.playerKing.isFirstMove()&&!this.isInCheck()) 
		{
			if (!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) //Black king side Castle
			{
				final Tile rookTile=this.board.getTile(7);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove())
				{
					if (Player.calculateAttacksOnTile(5, opponentLegals).isEmpty() && 
						Player.calculateAttacksOnTile(6, opponentLegals).isEmpty())
						kingCastles.add(new Move.KingSideCastleMove(this.board, this.playerKing, 6, (Rook)rookTile.getPiece(), rookTile.getTileCoord(), 5));
				}
			}
			
			if (!this.board.getTile(1).isTileOccupied() && !this.board.getTile(2).isTileOccupied() && !this.board.getTile(3).isTileOccupied()) //Black queen side Castle
			{
				final Tile rookTile=this.board.getTile(0);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
						Player.calculateAttacksOnTile(2, opponentLegals).isEmpty() && 
						Player.calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
						rookTile.getPiece().getPieceType().isRook())
				{
					kingCastles.add(new Move.QueenSideCastleMove(this.board, this.playerKing, 2, (Rook)rookTile.getPiece(), rookTile.getTileCoord(), 3));
				}
			}
		}
		
		return Collections.unmodifiableCollection(kingCastles);
	}
}
