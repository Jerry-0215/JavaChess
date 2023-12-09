package engine;

import elements.Board;
import elements.Move;
import players.MoveTransition;

public class MiniMax implements MoveStrategy
{
	private final BoardEvaluator boardEvaluator;
	private final int searchDepth;
	
	public MiniMax(final int searchDepth)
	{
		this.boardEvaluator=new StandardBoardEvaluator();
		this.searchDepth = searchDepth;
	}
	
	@Override
	public String toString()
	{
		return "MiniMax";
	}
	
	@Override
	public Move execute(Board board) 
	{
		final long startTime=System.currentTimeMillis();
		Move bestMove=null;
		int highestSeenValue=Integer.MIN_VALUE;
		int lowestSeenValue=Integer.MAX_VALUE;
		int currentValue;
		
		System.out.println(board.currentPlayer()+" THINKING with depth = "+searchDepth);
		
		int numMoves=board.currentPlayer().getLegalMoves().size();
		for (final Move move:board.currentPlayer().getLegalMoves())
		{
			final MoveTransition moveTransition=board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone())
			{
				currentValue=board.currentPlayer().getAlliance().isWhite()?
						     min(moveTransition.getTransitionBoard(),searchDepth-1):
						     max(moveTransition.getTransitionBoard(),searchDepth-1);
				
				if (board.currentPlayer().getAlliance().isWhite() && currentValue>=highestSeenValue)
				{
					highestSeenValue=currentValue;
					bestMove=move;
				}
				else if (board.currentPlayer().getAlliance().isBlack() && currentValue<=lowestSeenValue)
				{
					lowestSeenValue=currentValue;
					bestMove=move;
				}
			}
			
		}
		
		final long executionTime=System.currentTimeMillis()-startTime;
		
		return null;
	}
	
	private static boolean isEndGameScenario(Board board)
	{
		return board.currentPlayer().isInCheckmate() ||
			   board.currentPlayer().isInStalemate();
	}
	
	public int min(final Board board, final int searchDepth) //min method to return the lowest value in a node, part of the co-recursive MiniMax algorithm
	{
		if (searchDepth==0 || isEndGameScenario(board))
			return this.boardEvaluator.evaluate(board, searchDepth);
		
		int lowestSeenValue=Integer.MAX_VALUE;
		for (final Move move:board.currentPlayer().getLegalMoves())
		{
			final MoveTransition moveTransition=board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone())
			{
				final int currentValue=max(moveTransition.getTransitionBoard(),searchDepth-1);
				if (currentValue<=lowestSeenValue)
					lowestSeenValue=currentValue;
			}
		}
		
		return lowestSeenValue;
	}
	
	public int max(final Board board, final int searchDepth) //max method to return the highest value in a node, part of the co-recursive MiniMax algorithm
	{
		if (searchDepth==0 || isEndGameScenario(board))
			return this.boardEvaluator.evaluate(board, searchDepth);
		
		int highestSeenValue=Integer.MIN_VALUE;
		for (final Move move:board.currentPlayer().getLegalMoves())
		{
			final MoveTransition moveTransition=board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone())
			{
				final int currentValue=min(moveTransition.getTransitionBoard(),searchDepth-1);
				if (currentValue>=highestSeenValue)
					highestSeenValue=currentValue;
			}
		}
		
		return highestSeenValue;
	}
}
