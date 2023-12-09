package game;

import java.io.IOException;

import elements.Board;
import elements.BoardUtils;
import gui.Table;

public class JChess 
{
	public static void main (String[] args) throws IOException
	{
		Board board=Board.createStandardBoard();
		Table.get().show();
	}
}
