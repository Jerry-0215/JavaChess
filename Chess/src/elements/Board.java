package elements;

import java.util.*;
import java.lang.*;

import players.*;

public class Board 
{
	private final List<Tile> gameBoard;
	private final Collection<Piece> whitePieces;
	private final Collection<Piece> blackPieces;
	
	private final WhitePlayer whitePlayer;
	private final BlackPlayer blackPlayer;
	private final Player currentPlayer;
	
	private final Pawn enPassantPawn;
	
	private Board(final Builder builder) //Board constructor using builder
	{
		this.gameBoard=createGameBoard(builder);
		this.whitePieces=calculateActivePieces(this.gameBoard,Alliance.WHITE);
		this.blackPieces=calculateActivePieces(this.gameBoard,Alliance.BLACK);
		this.enPassantPawn=builder.EnPassantPawn;
		
		final Collection<Move> whiteStandardLegalMoves=calculateLegalMoves(this.whitePieces);
		final Collection<Move> blackStandardLegalMoves=calculateLegalMoves(this.blackPieces);
		
		this.whitePlayer=new WhitePlayer(this,whiteStandardLegalMoves,blackStandardLegalMoves);
		this.blackPlayer=new BlackPlayer(this,whiteStandardLegalMoves,blackStandardLegalMoves);
		this.currentPlayer=builder.nextMoveMaker.choosePlayer(this.whitePlayer,this.blackPlayer);
	}
	
	private Collection<Move> calculateLegalMoves(Collection<Piece> pieces)  //Method to calculate all legal moves for a collection of pieces
	{
		final List<Move> legalMoves=new ArrayList<>();
		for (final Piece piece:pieces)
		{
			legalMoves.addAll(piece.calculateLegalMoves(this));
		}
		return Collections.unmodifiableList(legalMoves);
	}
	
	@Override 
	public String toString()     //Method to convert a game board to a string
	{
		final StringBuilder builder=new StringBuilder();
		for (int i=0;i<64;i++)
		{
			final String tileText=this.gameBoard.get(i).toString();
			builder.append(String.format("%3s", tileText));
			if ((i+1)%8==0)
				builder.append("\n");
		}
		return builder.toString();
	}
	
	
	public Player whitePlayer()
	{
		return this.whitePlayer;
	}
	
	public Player blackPlayer()
	{
		return this.blackPlayer;
	}
	
	public Player currentPlayer()
	{
		return this.currentPlayer;
	}
	
	public Pawn getEnPassantPawn()
	{
		return this.enPassantPawn;
	}
	
	public Collection<Piece> getBlackPieces()
	{
		return this.blackPieces;
	}
	
	public Collection<Piece> getWhitePieces()
	{
		return this.whitePieces;
	}

	public static class Builder //Using a builder to design the Board class as it is complex and has many parameters
	{
		Map<Integer,Piece> boardConfig;
		Alliance nextMoveMaker;
		Pawn EnPassantPawn;
		
		public Builder() 
		{
			this.boardConfig=new HashMap<>();
		}
		
		public Builder setPiece(final Piece piece)
		{
			this.boardConfig.put(piece.getPiecePosition(),piece);
			return this;
		}
		
		public Builder setMoveMaker(final Alliance nextMoveMaker)
		{
			this.nextMoveMaker=nextMoveMaker;
			return this;
		}
		
		public Board build()
		{
			return new Board(this);
		}

		public void setEnPassantPawn(Pawn EnPassantPawn) {
			this.EnPassantPawn=EnPassantPawn;
			
		}
	}
	
	private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard,final Alliance alliance) //Method to return a list of all pieces on the board for white/black
	{
		final List<Piece> activePieces=new ArrayList<>();
		for (final Tile tile:gameBoard)
		{
			if (tile.isTileOccupied())
			{
				final Piece piece=tile.getPiece();
				if (piece.pieceAlliance==alliance)
					activePieces.add(piece);
			}
		}
		return Collections.unmodifiableList(activePieces);
	}

	public Tile getTile (final int tileCoord)    //Method for getting the tile at a coordinate
	{
		return gameBoard.get(tileCoord);
	}
	
	private static List<Tile> createGameBoard(final Builder builder)  //Creating a list of tiles as the game board
	{
		final Tile[]tiles = new Tile[64];
		for (int i=0;i<64;i++)
			tiles[i]=(Tile.createTile(i,builder.boardConfig.get(i)));
		return Arrays.asList(tiles);
	}
	
	public static Board createStandardBoard()  //Setting the initial game board with all the pieces using the builder.setPiece method
	{
		final Builder builder=new Builder();
		//Black Pieces Layout
		builder.setPiece(new Rook(Alliance.BLACK,0));
		builder.setPiece(new Knight(Alliance.BLACK,1));
		builder.setPiece(new Bishop(Alliance.BLACK,2));
		builder.setPiece(new Queen(Alliance.BLACK,3));
		builder.setPiece(new King(Alliance.BLACK,4));
		builder.setPiece(new Bishop(Alliance.BLACK,5));
		builder.setPiece(new Knight(Alliance.BLACK,6));
		builder.setPiece(new Rook(Alliance.BLACK,7));
		
		builder.setPiece(new Pawn(Alliance.BLACK,8));
		builder.setPiece(new Pawn(Alliance.BLACK,9));
		builder.setPiece(new Pawn(Alliance.BLACK,10));
		builder.setPiece(new Pawn(Alliance.BLACK,11));
		builder.setPiece(new Pawn(Alliance.BLACK,12));
		builder.setPiece(new Pawn(Alliance.BLACK,13));
		builder.setPiece(new Pawn(Alliance.BLACK,14));
		builder.setPiece(new Pawn(Alliance.BLACK,15));
		
		//White Pieces layout
		builder.setPiece(new Pawn(Alliance.WHITE,48));
		builder.setPiece(new Pawn(Alliance.WHITE,49));
		builder.setPiece(new Pawn(Alliance.WHITE,50));
		builder.setPiece(new Pawn(Alliance.WHITE,51));
		builder.setPiece(new Pawn(Alliance.WHITE,52));
		builder.setPiece(new Pawn(Alliance.WHITE,53));
		builder.setPiece(new Pawn(Alliance.WHITE,54));
		builder.setPiece(new Pawn(Alliance.WHITE,55));
		
		builder.setPiece(new Rook(Alliance.WHITE,56));
		builder.setPiece(new Knight(Alliance.WHITE,57));
		builder.setPiece(new Bishop(Alliance.WHITE,58));
		builder.setPiece(new Queen(Alliance.WHITE,59));
		builder.setPiece(new King(Alliance.WHITE,60));
		builder.setPiece(new Bishop(Alliance.WHITE,61));
		builder.setPiece(new Knight(Alliance.WHITE,62));
		builder.setPiece(new Rook(Alliance.WHITE,63));
		
		builder.setMoveMaker(Alliance.WHITE);
		return builder.build();
	}

	
	
	
}
