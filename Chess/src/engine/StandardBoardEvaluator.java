package engine;

import elements.*;
import players.*;

public final class StandardBoardEvaluator implements BoardEvaluator 
{
	private final static int CHECK_BONUS=50;
	private final static int CASTLE_BONUS=60;
	private final static int CHECKMATE_BONUS=10000;
	
	@Override
	public int evaluate(final Board board,final int depth) 
	{
		return scorePlayer(board,board.whitePlayer(),depth)-
			   scorePlayer(board,board.blackPlayer(),depth);
	}

	private int scorePlayer(final Board board,final Player player,final int depth) 
	{
		return pieceValue(player)+mobility(player)+check(player)
			   +checkMate(player,depth)+castled(player);
	}

	private int castled(Player player) 
	{
		return player.isCastled()?CASTLE_BONUS:0;
	}

	private static int check(Player player) 
	{
		return player.getOpponent().isInCheck()?CHECK_BONUS:0;
	}
	
	private static int checkMate(Player player,int depth) 
	{
		return player.getOpponent().isInCheckmate()?CHECKMATE_BONUS*depthBonus(depth):0;
	}

	private static int depthBonus(int depth) 
	{
		return depth==0?1:100*depth;
	}

	private static int mobility(Player player) 
	{
		return player.getLegalMoves().size();
	}

	private static int pieceValue(Player player)
	{
		int pieceValueScore=0;
		for (final Piece piece:player.getActivePieces())
		{
			pieceValueScore+=piece.getPieceValue();
		}
		return pieceValueScore;
	}
}
