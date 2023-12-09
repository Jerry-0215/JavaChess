package gui;

import java.util.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.SwingUtilities;

import elements.*;
import elements.Move.MoveFactory;
import engine.MiniMax;
import engine.MoveStrategy;
import players.MoveTransition;
import com.google.common.*;
import com.google.common.collect.Lists;

@SuppressWarnings("deprecation")
public class Table extends Observable
{
	private final JFrame gameFrame;
	private final GameHistoryPanel gameHistoryPanel;
	private final TakenPiecesPanel takenPiecesPanel;
	private final BoardPanel boardPanel;
	private final MoveLog moveLog;
	private final GameSetup gameSetup;
	private Board chessBoard;
	
	private Tile sourceTile;
	private Tile destinationTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;
	
	private Move computerMove;
	
	private boolean highlightLegalMoves;
	
	private final Color lightTileColor = Color.decode("#c4ac8c");
    private final Color darkTileColor = Color.decode("#6c4c34");
	
	private final static Dimension OUTER_FRAME_DIMENSION=new Dimension(700,600);
	private final static Dimension BOARD_PANEL_DIMENSION=new Dimension(500,350);
	private final static Dimension TILE_PANEL_DIMENSION=new Dimension(10,10);
	private static String defaultPieceImagesPath = "pieces/";
	
	private static final Table Instance=new Table();
	
	private Table ()
	{
		this.gameFrame=new JFrame("JChess");
		this.gameFrame.setLayout(new BorderLayout());
		final JMenuBar tableMenuBar=createTableMenuBar();
		this.gameFrame.setJMenuBar(tableMenuBar);
		this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
		this.chessBoard = Board.createStandardBoard();
		this.gameHistoryPanel=new GameHistoryPanel();
		this.takenPiecesPanel=new TakenPiecesPanel();
		this.boardPanel=new BoardPanel();
		this.moveLog=new MoveLog();
		this.addObserver(new TableGameAIWatcher());
		this.gameSetup=new GameSetup(this.gameFrame,true);
		this.gameFrame.add(this.takenPiecesPanel,BorderLayout.WEST);
		this.gameFrame.add(this.boardPanel,BorderLayout.CENTER);
		this.gameFrame.add(this.gameHistoryPanel,BorderLayout.EAST);
		this.gameFrame.setVisible(true);
		this.boardDirection=BoardDirection.NORMAL;
		this.highlightLegalMoves=false;
	}
	
	public static Table get()
	{
		return Instance;
	}
	
	public void show() 
	{
		Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
	}
	
	public GameSetup getGameSetup()
	{
		return this.gameSetup;
	}
	
	public Board getGameBoard()
	{
		return this.chessBoard;
	}
	
	private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }
	
	private JMenuBar createTableMenuBar()  //Method for adding file menus to the menu bar
	{
		final JMenuBar tableMenuBar=new JMenuBar();
		tableMenuBar.add(createFileMenu());
		tableMenuBar.add(createPreferencesMenu());
		tableMenuBar.add(createOptionsMenu());
		return tableMenuBar;
	}
	
	private JMenu createFileMenu() //Method to create a new file menu
	{
		final JMenu fileMenu=new JMenu("File");
		
		final JMenuItem openPGN=new JMenuItem("Load PGN FIle");
		openPGN.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("open up PGN!");
			}
		});
		
		final JMenuItem exitMenuItem=new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
		
		return fileMenu;
	}
	
	private JMenu createPreferencesMenu()   //Creating a preferences Menu with flip board function
	{
		final JMenu preferencesMenu=new JMenu("Preferences");
		final JMenuItem flipBoardMenuItem=new JMenuItem("Flip Board");
		flipBoardMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				boardDirection=boardDirection.opposite();
				boardPanel.drawBoard(chessBoard);
			}
		});
		preferencesMenu.add(flipBoardMenuItem);
		
		preferencesMenu.addSeparator();
		
		final JCheckBoxMenuItem legalMoveHighlighterCheckBox=new JCheckBoxMenuItem("Highlight Legal Moves",false);
		legalMoveHighlighterCheckBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				highlightLegalMoves=legalMoveHighlighterCheckBox.isSelected();
			}
			
		});
		
		preferencesMenu.add(legalMoveHighlighterCheckBox);
		
		return preferencesMenu;
	}
	
	private JMenu createOptionsMenu()
	{
		final JMenu optionsMenu=new JMenu("Options Menu");
		final JMenuItem setupGameMenuItem=new JMenuItem("Setup Game");
		setupGameMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				Table.get().getGameSetup().promptUser();
				Table.get().setupUpdate(Table.get().getGameSetup());
			}
		});
		
		optionsMenu.add(setupGameMenuItem);
		return optionsMenu;
	}
	
	public void setupUpdate(final GameSetup gameSetup)
	{
		setChanged();
		notifyObservers(gameSetup);
	}
	
	public static class TableGameAIWatcher implements Observer
	{

		@Override
		public void update(Observable o,final Object arg) 
		{
			if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
				!Table.get().getGameBoard().currentPlayer().isInCheckmate() &&
				!Table.get().getGameBoard().currentPlayer().isInStalemate()
				)
			{
				//Create Ai thread
				//Execute AI
				final AIThinkTank thinkTank=new AIThinkTank();
				thinkTank.execute();
			}
			
			if(Table.get().getGameBoard().currentPlayer().isInCheckmate())
			{
				JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
			}
			
			if(Table.get().getGameBoard().currentPlayer().isInStalemate())
			{
				JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
	}
	
	public void updateGameBoard(final Board board)
	{
		this.chessBoard=board;
	}
	
	public void updateComputerMove(final Move move)
	{
		this.computerMove=move;
	}
	
	public MoveLog getMoveLog()
	{
		return this.moveLog;
	}
	
	private GameHistoryPanel getGameHistoryPanel()
	{
		return this.gameHistoryPanel;
	}
	
	private TakenPiecesPanel getTakenPiecesPanel()
	{
		return this.takenPiecesPanel;
	}
	
	private void moveMadeUpdate(final PlayerType playerType)
	{
		setChanged();
		notifyObservers(playerType);
	}
	
	private static class AIThinkTank extends SwingWorker<Move, String>
	{
		private AIThinkTank()
		{
			
		}
		
		@Override
		protected Move doInBackground() throws Exception 
		{
			final MoveStrategy minimax=new MiniMax(4);
			
			final Move bestMove=minimax.execute(Table.get().getGameBoard());
			
			return bestMove;
		}
		
		@Override
		public void done()
		{
			try{
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            }catch(Exception e){
                e.printStackTrace();
            }
		}
	}
	
	public enum BoardDirection  //Enum for the direction of the board, used to flip the board
	{
		NORMAL
		{			
			@Override
			List<TilePanel> traverse(List<TilePanel> boardTiles) {
				return boardTiles;
			}

			@Override
			BoardDirection opposite() 
			{
				return FLIPPED;
			}
			
		},
		FLIPPED
		{

			@Override
			List<TilePanel> traverse(List<TilePanel> boardTiles) {
				return Lists.reverse(boardTiles);
			}

			@Override
			BoardDirection opposite() 
			{
				return NORMAL;
			}
			
		};
		abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
		abstract BoardDirection opposite();
	}
	
	private class BoardPanel extends JPanel //Visual panel that maps the chess board
	{
		final List<TilePanel> boardTiles;
		
		BoardPanel()
		{
			super (new GridLayout(8,8));   //Creating a 8 by 8 grid visually
			this.boardTiles=new ArrayList<>();
			for (int i=0;i<64;i++)   //Adding 64 tile panels to the board panel
			{
				final TilePanel tilePanel=new TilePanel(this,i);
				this.boardTiles.add(tilePanel);
				add(tilePanel);
			}
			setPreferredSize(BOARD_PANEL_DIMENSION);
			validate();
		}
		
		public void drawBoard(final Board board)
		{
			removeAll();
			for (final TilePanel tilePanel:boardDirection.traverse(boardTiles))
			{
				tilePanel.drawTile(board);
				add(tilePanel);
			}
			validate();
			repaint();
		}
	}
	
	public static class MoveLog  //class for a list that track of moves
	{
		private final List<Move> moves;
		
		MoveLog()
		{
			this.moves=new ArrayList<>();
		}
		
		public List<Move> getMoves()
		{
			return this.moves;
		}
		
		public void addMove(final Move move)
		{
			moves.add(move);
		}
		
		public int size()
		{
			return this.moves.size();
		}
		
		public void clear()
		{
			this.moves.clear();
		}
		
		public Move removeMove(final int index)
		{
			return this.moves.remove(index);
		}
		
		public boolean removeMove(final Move move)
		{
			return this.moves.remove(move);
		}
	}
	
	public enum PlayerType
	{
		HUMAN,
		COMPUTER;
	}
	
	private class TilePanel extends JPanel //Visual panel that maps the layout of a tile
	{
		private final int tileId;
		
		TilePanel(final BoardPanel boardPanel, final int tileId)
		{
			super(new GridBagLayout());
			this.tileId=tileId;
			setPreferredSize(TILE_PANEL_DIMENSION);
			assignTileColor();
			assignTilePieceIcon(chessBoard);
			
			
			addMouseListener(new MouseListener()  //Adding a mouse listener to detect user mouse clicks
			{
			    @Override
			    public void mouseClicked(final MouseEvent e) 
			    {
			    	if(SwingUtilities.isRightMouseButton(e)) //Canceling the selected move when right-clicking a tile
			    	{
			    		sourceTile = null;
			    	    destinationTile = null;
			    	    humanMovedPiece = null;
			        } 
			    	else if(SwingUtilities.isLeftMouseButton(e))
			    	{
			    		if(sourceTile==null) //First left click
			    		{
			    			sourceTile=chessBoard.getTile(tileId);
			    			humanMovedPiece=sourceTile.getPiece();
			    			if(humanMovedPiece==null)
			    			{
			    				sourceTile=null;
			    			}
			    		}
			    		else  //Second left click
			    		{
			    			destinationTile=chessBoard.getTile(tileId);
			    			final Move move=MoveFactory.createMove(chessBoard,sourceTile.getTileCoord(),destinationTile.getTileCoord());
			    			final MoveTransition transition=chessBoard.currentPlayer().makeMove(move);
			    			if (transition.getMoveStatus().isDone())
			    			{
			    				chessBoard=transition.getTransitionBoard();
			    				moveLog.addMove(move);
			    			}
			    			sourceTile = null;
				    	    destinationTile = null;
				    	    humanMovedPiece = null;
			    		}
			    		SwingUtilities.invokeLater(()->
			    		{
			    			gameHistoryPanel.redo(chessBoard, moveLog);
	                        takenPiecesPanel.redo(moveLog);
	                        if (gameSetup.isAIPlayer(chessBoard.currentPlayer()))
	                        {
	                        	Table.get().moveMadeUpdate(PlayerType.HUMAN);
	                        }  	
	                        boardPanel.drawBoard(chessBoard);
			    		});
			    		SwingUtilities.invokeLater(() -> {
                            gameHistoryPanel.redo(chessBoard, moveLog);
                            takenPiecesPanel.redo(moveLog);
                            
                            if(gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                Table.get().moveMadeUpdate(PlayerType.HUMAN);
                            }
                            boardPanel.drawBoard(chessBoard);
                        });
			    }
			}

				@Override
				public void mousePressed(MouseEvent e) {
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					
				}


				
			});
			
			validate();
		}
		
		public void drawTile(final Board board)
		{
			assignTileColor();
			assignTilePieceIcon(board);
			highlightLegalMoves(board);
			validate();
			repaint();
		}
		
		private void assignTilePieceIcon(final Board board) //Method to assign a gif of chess pieces to tiles
		{
			this.removeAll();
			if (board.getTile(tileId).isTileOccupied())
			{
				try {
					final BufferedImage image=ImageIO.read(new File(defaultPieceImagesPath+board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1)+
							board.getTile(this.tileId).getPiece().toString()+".gif"));
					add(new JLabel(new ImageIcon(image)));
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		public void highlightLegalMoves(final Board board)
		{
			if (highlightLegalMoves)
			{
				for (final Move move:pieceLegalMoves(board))
				{
					if (move.getDestinationCoord()==this.tileId)
					{
						try
						{
							add(new JLabel(new ImageIcon(ImageIO.read(new File("misc/green_dot.png")))));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		private Collection<Move> pieceLegalMoves(final Board board)
		{
			if (humanMovedPiece!=null && humanMovedPiece.getPieceAlliance()==board.currentPlayer().getAlliance())
			{
				return humanMovedPiece.calculateLegalMoves(board);
			}
			return Collections.emptyList();
		}
		
		private void assignTileColor() //Method to create checkered board by assigning light or dark colors to tiles according to their position
		{
            boolean isLight = ((tileId + tileId / 8) % 2 == 0);
            setBackground(isLight ? lightTileColor : darkTileColor);
		}
	}
	
}

