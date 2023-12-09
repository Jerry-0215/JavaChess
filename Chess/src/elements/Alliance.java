package elements;

import players.BlackPlayer;
import players.Player;
import players.WhitePlayer;

public enum Alliance 
{
	WHITE
	{
		@Override
		public int getdirection() 
		{
			return -1;
		}

		@Override
		public boolean isWhite() 
		{
			return true;
		}

		@Override
		public boolean isBlack() 
		{
			return false;
		}

		@Override
		protected Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) 
		{
			return whitePlayer;
		}

		@Override
		public int getOppositeDirection() 
		{
			return 1;
		}

		@Override
		public boolean isPawnPromotionSquare(int position) 
		{
			return BoardUtils.EIGHTH_RANK[position];
		}
	},
	BLACK
	{
		@Override
		public int getdirection() 
		{
			return 1;
		}
		
		@Override
		public boolean isWhite() 
		{
			return false;
		}

		@Override
		public boolean isBlack() 
		{
			return true;
		}

		@Override
		protected Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) 
		{
			return blackPlayer;
		}

		@Override
		public int getOppositeDirection() 
		{
			return -1;
		}

		@Override
		public boolean isPawnPromotionSquare(int position) 
		{
			return BoardUtils.FIRST_RANK[position];
		}
	};
	
	public abstract int getdirection();
	public abstract int getOppositeDirection();
	public abstract boolean isPawnPromotionSquare(int position);
	public abstract boolean isWhite();
	public abstract boolean isBlack();
	protected abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
