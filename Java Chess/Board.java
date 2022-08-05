import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Board{

    Color color1 = new Color(17, 17, 17);
    Color color2 = new Color(47, 47, 47);
    Color optionsColor = new Color(197, 197, 197);
    Color captureColor = new Color(153, 153, 102);
    
    //frame for everything
    protected JFrame frame;

    //the board is a 2d array of buttons
    protected JButton[][] board;

    //labels are the unicode sequences for each chess piece
    protected ChessLabels labels;

    //button listeners one for first click one for second click 
    //(so which piece is moving is first click, where its going is second click)
    protected PositionListener buttonlistener;
    protected SecondPosListener secButtonListener;

    //the label of the piece that is moving is stored in temp so that it can replace the new place text
    protected StringBuilder temp;

    //to determine if a move was made or can be made (helps with determining if a piece has no available moves)
    protected boolean moveMade = false;

    //the button that needs it text cleared when a piece moves from it is stored here
    //so that when it moves to where it wants to go the original button has its text cleared
    JButton deleteText = new JButton();

    //this class has popups for the user like when there is no moves or theyre in check
    protected ChessErrors chessErrors = new ChessErrors();

    //helps to know when throw popups from chessErrors
    protected boolean blackCheck = false;
    protected boolean whiteCheck = false;

    //users choose they want their future pawn promotions to be in the start menu, passed in through board constructor
    protected String whitePromotion;
    protected String blackPromotion;

    //users choose their color on start menu then passed into board constructor
    protected Color whiteColor;
    protected Color blackColor;
    
    public Board(String white, String black, Color p1Color, Color p2Color) 
    {
        //these are for when a pawn reaches the opposite side, it will "promote" to another piece
        //the colors are if the player wants to pick another besides white/black
        whitePromotion = white;
        whiteColor = p1Color;
        blackPromotion = black;
        blackColor = p2Color;

        //frame for the chess board and close operation
        frame = new JFrame("Chess");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        
        //jpanel to add buttons to using gridlayout
        JPanel boardPanel = new JPanel();
        GridLayout boardLayout = new GridLayout(8, 8);
        boardPanel.setLayout(boardLayout);
        boardPanel.setSize(800,800);

        //board is a 2d array of buttons
        board = new JButton[8][8];
        labels = new ChessLabels(); //used for unicode

        
        //unicode for the white pieces
        String whiteKing = labels.get(0); //("\u2654");
        String whiteQueen = labels.get(1); //("\u2655");
        String whiteRook = labels.get(2); //("\u2656");
        String whiteBishop = labels.get(3); //("\u2657");
        String whiteKnight = labels.get(4); //("\u2658");
        String whitePawn = labels.get(5); //("\u2659");

        
        //unicode for the black pieces
        String blackKing = labels.get(6); //("\u265A");
        String blackQueen = labels.get(7); //("\265B");
        String blackRook = labels.get(8); //("\u265C");
        String blackBishop = labels.get(9); //("\u265D");
        String blackKnight = labels.get(10); //("\u265E");
        String blackPawn = labels.get(11); //("\u265F");
        
        for(int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                //make a new button for each spot, change font to read unicode
                board[i][j] = new JButton();
                board[i][j].setFont(new Font("Ariel Unicode MS", Font.PLAIN, 45));
                board[i][j].setText(" ");
                
                //to make colors show
                board[i][j].setBorderPainted(false);
                board[i][j].setOpaque(true);
                boardPanel.add(board[i][j]);
            }
        }

        //checkered colors
        resetCheckered();

        //adding white pieces to board
        board[7][0].setText(whiteRook);
        board[7][1].setText(whiteKnight);
        board[7][2].setText(whiteBishop);
        board[7][3].setText(whiteQueen);
        board[7][4].setText(whiteKing);
        board[7][5].setText(whiteBishop);
        board[7][6].setText(whiteKnight);
        board[7][7].setText(whiteRook);

        //placing white pawns and setting white text color
        for (int i=0; i<8; i++)
        {
            board[6][i].setText(whitePawn);
            board[7][i].setForeground(whiteColor);
            board[6][i].setForeground(whiteColor);
        }

        //adding black pieces to board
        board[0][0].setText(blackRook);
        board[0][1].setText(blackKnight);
        board[0][2].setText(blackBishop);
        board[0][3].setText(blackQueen);
        board[0][4].setText(blackKing);
        board[0][5].setText(blackBishop);
        board[0][6].setText(blackKnight);
        board[0][7].setText(blackRook);

        //placing black pawns and setting black text color
        for (int i=0; i<8; i++)
        {
            board[1][i].setText(blackPawn);
            board[0][i].setForeground(blackColor);
            board[1][i].setForeground(blackColor);
        }

        //adding the board to get ready to show
        frame.add(boardPanel);
        frame.setSize(800, 800);

        //listeners for the buttons
        buttonlistener = new PositionListener();
        secButtonListener = new SecondPosListener();

        //for placing the character on a new button 
        temp = new StringBuilder("");

        //turning on listener for white pieces
        whiteTurn();
    }
    //show the board, used to start game
    public void show()
    {
        frame.setVisible(true);
    } 
    //resets the checker board colors of the board to orginal colors
    public void resetCheckered()
    {
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                //checkered colors
                if (i % 2 == 0 && j % 2 == 0)
                    board[i][j].setBackground(color1);
                else if (i % 2 != 0 && j % 2 != 0)
                    board[i][j].setBackground(color1);
                else
                    board[i][j].setBackground(color2);
            }
        }
    }
    //check if a spot is empty (empty is a space)
    public boolean isEmpty(JButton b)
    {
        boolean empty = false;
        for (int i=0; i<8; i++)
            for (int j=0; j<8; j++)
                if (b == board[i][j])
                    if (board[i][j].getText() == " ")
                        empty = true;
        return empty;
    }
    //makes it whiteplayer turn (turns on action listener for white pieces)
    public void whiteTurn()
    {
        //when it is whites turn then actionlistener should only be on for white pieces
        //so black can not move (if you click on black nothing will happen)

        //this loop searches for black pieces and turns off actionlistener
        //then searches for white pieces and turns on actionlistner
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                board[i][j].removeActionListener(secButtonListener);
                if(board[i][j].getText() == labels.get(6))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(7))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(8))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(9))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(10))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(11))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(0))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(1))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(2))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(3))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(4))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(5))
                    board[i][j].addActionListener(buttonlistener);
                else
                    board[i][j].removeActionListener(buttonlistener);
            }
        }
    }
    //makes it blackplayer turn (turns on action listener for black pieces)
    public void blackTurn()
    {
        //when it is blacks turn then actionlistener should only be on for black pieces
        //so white can not move (if you click on white nothing will happen bc actionlistner is not on)

        //this loops searches for the spots with the white pieces and turns off actionlistener
        //then searches for black pieces and turns on actionlistener
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                board[i][j].removeActionListener(secButtonListener);
                if(board[i][j].getText() == labels.get(0))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(1))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(2))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(3))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(4))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(5))
                    board[i][j].removeActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(6))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(7))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(8))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(9))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(10))
                    board[i][j].addActionListener(buttonlistener);
                else if (board[i][j].getText() == labels.get(11))
                    board[i][j].addActionListener(buttonlistener);
                else
                    board[i][j].removeActionListener(buttonlistener);
            }
        }
    }
    //finds the available moves for the piece clicked and turns them on as an available space by adding action listener
    public boolean availableMoves(JButton b)
    {
        //need this boolean to see if their are available moves for a piece or not
        moveMade = false;

        //first remove actionlisteners from the whole board
        boolean queenMove;
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                board[i][j].removeActionListener(buttonlistener);
                board[i][j].removeActionListener(secButtonListener);
            }
        }

        //then search for the button that was clicked
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                if (board[i][j] == b)  //when you find the button that was clicked
                {
                    board[i][j].removeActionListener(buttonlistener);
                    board[i][j].removeActionListener(secButtonListener);

                    //call the function for the specific piece that was clicked
                    if (b.getText().compareTo(labels.get(5))==0)
                    {
                        moveMade = whitePawnMoves(i, j);
                    }
                    else if (b.getText().compareTo(labels.get(11))==0)
                    {
                        moveMade = blackPawnMoves(i, j);
                    }
                    else if (b.getText().compareTo(labels.get(3))==0 || b.getText().compareTo(labels.get(9))==0)
                    {
                        moveMade = bishopMoves(i, j);
                    }
                    else if (b.getText().compareTo(labels.get(2))==0 || b.getText().compareTo(labels.get(8))==0)
                    {
                        moveMade = rookMoves(i, j);
                    }
                    else if (b.getText().compareTo(labels.get(1))==0 || b.getText().compareTo(labels.get(7))==0)
                    {
                        //queen acts like bishop and rook combined so theyre both called here
                        moveMade = bishopMoves(i, j);
                        queenMove = rookMoves(i, j);    //setting to a different boolean so it doesnt overide moveMade from bishop
                        moveMade = moveMade || queenMove;   //if either was true then a move can be made
                        
                    }
                    else if (b.getText().compareTo(labels.get(4))==0 || b.getText().compareTo(labels.get(10))==0)
                    {
                        moveMade = knightMoves(i, j);
                    }
                    else if (b.getText().compareTo(labels.get(0))==0 || b.getText().compareTo(labels.get(6))==0)
                    {
                        moveMade = kingMoves(i, j);
                    }
                    
                    if (moveMade == true) //if a move was available for the piece that was clicked
                    {
                        //store the piece name in temp and set the button to deleteText
                        //the temp will be placed in the new button and this spot will be erased
                        temp.delete(0, temp.length());    
                        temp.append(board[i][j].getText());
                        deleteText = board[i][j];
                    }
                    else    //if there was no available moves
                    {
                        //the turn and board will reset and a popup will show for the user
                        resetTurn(i, j);
                        resetCheckered();
                        chessErrors.noMoves();
                        board[i][j].removeActionListener(buttonlistener);
                        board[i][j].removeActionListener(secButtonListener);
                    }
                }
            }
        }
        return moveMade;
    }
    //resets the user turn because the piece they clicked had no available moves
    public void resetTurn(int row, int col)
    {
        boolean white = true;
        String piece = board[row][col].getText();

        if (piece.compareTo(labels.get(6)) == 0 || piece.compareTo(labels.get(7)) == 0 ||
            piece.compareTo(labels.get(8)) == 0 || piece.compareTo(labels.get(9)) == 0 ||
            piece.compareTo(labels.get(10)) == 0 || piece.compareTo(labels.get(11)) == 0)
            white = false;
        
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                if (white == true)  //if the piece was white turn on action listener for only whites
                {
                    if (board[i][j].getText().compareTo(labels.get(0)) == 0 || board[i][j].getText().compareTo(labels.get(1)) == 0 ||
                        board[i][j].getText().compareTo(labels.get(2)) == 0 || board[i][j].getText().compareTo(labels.get(3)) == 0 ||
                        board[i][j].getText().compareTo(labels.get(4)) == 0 || board[i][j].getText().compareTo(labels.get(5)) == 0)
                    {
                        board[i][j].addActionListener(buttonlistener);
                    }
                    else
                    {
                        board[i][j].removeActionListener(buttonlistener);
                        board[i][j].removeActionListener(secButtonListener);
                    }

                }
                else if (white == false)    //same but for black
                {
                    if (board[i][j].getText().compareTo(labels.get(6)) == 0 || board[i][j].getText().compareTo(labels.get(7)) == 0 ||
                        board[i][j].getText().compareTo(labels.get(8)) == 0 || board[i][j].getText().compareTo(labels.get(9)) == 0 ||
                        board[i][j].getText().compareTo(labels.get(10)) == 0 || board[i][j].getText().compareTo(labels.get(11)) == 0)
                    {
                        board[i][j].addActionListener(buttonlistener);
                    }
                    else
                    {
                        board[i][j].removeActionListener(buttonlistener);
                        board[i][j].removeActionListener(secButtonListener);
                    }
                }
            }
        }
    }
    //finds moves for white pawn
    public boolean whitePawnMoves(int row, int col)
    {
        boolean firstMove = row==6; //its on its first move if its in the starting row
        boolean availableMove = false;
        if (row!=0 && isEmpty(board[row-1][col]))   //if the space in front of pawn is empty
        {
            availableMove = true;                   //then there is an available move for it
            if (firstMove == true && isEmpty(board[row-2][col]))    //if its on its first move and the space to jump twice is empty
            {
                board[row-2][col].addActionListener(secButtonListener);
                board[row-2][col].setBackground(optionsColor);
            }
                board[row-1][col].addActionListener(secButtonListener);
                board[row-1][col].setBackground(optionsColor);
        }
        else
        {
            availableMove = false;
        }

        if (col != 0 && isEmpty(board[row-1][col-1]) == false)  //look for capture top left
        {
            if (board[row-1][col-1].getText().compareTo(labels.get(6)) == 0 || board[row-1][col-1].getText().compareTo(labels.get(7)) == 0 ||
                board[row-1][col-1].getText().compareTo(labels.get(8)) == 0 || board[row-1][col-1].getText().compareTo(labels.get(9)) == 0 ||
                board[row-1][col-1].getText().compareTo(labels.get(10)) == 0 || board[row-1][col-1].getText().compareTo(labels.get(11)) == 0)
            {
                availableMove = true;
                board[row-1][col-1].addActionListener(secButtonListener);     //set option for capture
                board[row-1][col-1].setBackground(captureColor);
            }
        }
        if (col!=7 && isEmpty(board[row-1][col+1]) == false)    //look for capture top right
        {
            if (board[row-1][col+1].getText().compareTo(labels.get(6)) == 0 || board[row-1][col+1].getText().compareTo(labels.get(7)) == 0 ||
                board[row-1][col+1].getText().compareTo(labels.get(8)) == 0 || board[row-1][col+1].getText().compareTo(labels.get(9)) == 0 ||
                board[row-1][col+1].getText().compareTo(labels.get(10)) == 0 || board[row-1][col+1].getText().compareTo(labels.get(11)) == 0)
            {
                availableMove = true;
                board[row-1][col+1].addActionListener(secButtonListener);     //set option for capture
                board[row-1][col+1].setBackground(captureColor);
            }
        }
        return availableMove;
    }
    //finds moves for black pawn
    public boolean blackPawnMoves(int row, int col)
    {
        boolean firstMove = row==1; //its on its first move if its in the starting row
        boolean availableMove = false;
        if ((row!=7) && (isEmpty(board[row+1][col])))   //if the space in front is empty
        {
            availableMove=true;
            if (firstMove == true && isEmpty(board[row+2][col]))
            {
                board[row+2][col].addActionListener(secButtonListener); //set option for two forward
                board[row+2][col].setBackground(optionsColor);     
            }
            board[row+1][col].addActionListener(secButtonListener);     //set option for one forward
            board[row+1][col].setBackground(optionsColor);
        }
        else
        {
            availableMove=false;
        }

        if (col != 0 && isEmpty(board[row+1][col-1]) == false)  //look for capture bottom left
        {
            if (board[row+1][col-1].getText().compareTo(labels.get(0)) == 0 || board[row+1][col-1].getText().compareTo(labels.get(1)) == 0 ||
                board[row+1][col-1].getText().compareTo(labels.get(2)) == 0 || board[row+1][col-1].getText().compareTo(labels.get(3)) == 0 ||
                board[row+1][col-1].getText().compareTo(labels.get(4)) == 0 || board[row+1][col-1].getText().compareTo(labels.get(5)) == 0)
            {
                availableMove = true;
                board[row+1][col-1].addActionListener(secButtonListener);     //set option for one forward
                board[row+1][col-1].setBackground(captureColor);
            }
        }
        if (col != 7 && isEmpty(board[row+1][col+1]) == false)  //look for capture bottom right
        {
            if (board[row+1][col+1].getText().compareTo(labels.get(0)) == 0 || board[row+1][col+1].getText().compareTo(labels.get(1)) == 0 ||
                board[row+1][col+1].getText().compareTo(labels.get(2)) == 0 || board[row+1][col+1].getText().compareTo(labels.get(3)) == 0 ||
                board[row+1][col+1].getText().compareTo(labels.get(4)) == 0 || board[row+1][col+1].getText().compareTo(labels.get(5)) == 0)
            {
                availableMove = true;
                board[row+1][col+1].addActionListener(secButtonListener);     //set option for one forward
                board[row+1][col+1].setBackground(captureColor);
            }
        }
        
        return availableMove;
    }
    //finds moves for bishops and queens
    public boolean bishopMoves(int row, int col)
    {
        boolean moveUpLeft = true;
        boolean moveUpRight = true;
        boolean moveDownLeft = true;
        boolean moveDownRight = true;
        boolean availableMove = false;
        boolean white = true;

        //if its a black piece change white to false
        if (board[row][col].getText().compareTo(labels.get(6)) == 0 || board[row][col].getText().compareTo(labels.get(7)) == 0 ||
            board[row][col].getText().compareTo(labels.get(8)) == 0 || board[row][col].getText().compareTo(labels.get(9)) == 0 ||
            board[row][col].getText().compareTo(labels.get(10)) == 0 || board[row][col].getText().compareTo(labels.get(11)) == 0)
            white = false;
        
        int newCol = col;
        int newRow = row;

        //checking if it can even look for available moves in that direction
        //without going out of bounds
        if (col == 0) //if on left edge
        {
            moveUpLeft = false; //then it can't move left anymore
            moveDownLeft = false;
        }
        else if (col == 7) //if on right edge
        {
            moveUpRight = false;  //then it can't move right anymore
            moveDownRight = false;
        }
        else if (row == 0) //if at the top 
        {
            moveUpLeft = false; //cant go anymore up
            moveUpRight = false;
        }
        else if (row == 7)  //of at bottom 
        {
            moveDownLeft = false;   //cant go anymore down
            moveDownRight = false;
        }
        
        //if it can move up check if there is available moves 
        while (moveUpLeft == true)
        {
            //set new cords to the next up left diagonal space
            newRow = newRow-1;
            newCol = newCol-1;
            //check if the new cord space is empty
            if (isEmpty(board[newRow][newCol]) == true)
            {
                availableMove=true;     //if yes change color and add listner to maark available space
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in top left, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveUpLeft=false;    //once it hits a piece it theres no more top left pieces to move to
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in top left, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveUpLeft=false;   //once it hits a piece it theres no more top left pieces to move to
            }
            else//if nothing above applies then there is no possible moves in the top left
                moveUpLeft=false;
            
            if (newCol == 0 || newRow == 0)
                moveUpLeft=false;
        }
        newCol=col;
        newRow=row;
        while (moveUpRight == true)
        {
           newRow = newRow-1;
           newCol = newCol+1;
            if (isEmpty(board[newRow][newCol]) == true)
            {
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in top right, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveUpRight=false;    //once it hits a piece it theres no more top right pieces to move to
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in top right, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveUpRight=false;   //once it hits a piece it theres no more top right pieces to move to
            }
            else
                moveUpRight=false;
            
            if (newCol == 7 || newRow == 0)
                moveUpRight=false;
        }
        newCol=col;
        newRow=row;
        while (moveDownLeft == true)
        {
            newRow = newRow+1;
            newCol = newCol-1;
           
            if (isEmpty(board[newRow][newCol]) == true)
            {
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in bottom left, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveDownLeft=false;    //once it hits a piece it theres no more bottom left pieces to move to
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in bottom left, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveDownLeft=false;   //once it hits a piece it theres no more bottom left pieces to move to
            }
            else
                moveDownLeft=false;
            
            if (newCol == 0 || newRow == 7)
                moveDownLeft=false;
        }
        newCol=col;
        newRow=row;
        while (moveDownRight == true)
        {
            newRow = newRow+1;
            newCol = newCol+1;

            if (isEmpty(board[newRow][newCol]) == true)
            {
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in bottom right, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveDownRight=false;    //once it hits a piece it theres no more bottom right pieces to move to
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in bottom right, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveDownRight=false;   //once it hits a piece it theres no more bottom right pieces to move to
            }
            else
                moveDownRight=false;
            
            if (newCol == 7 || newRow == 7)
                moveDownRight=false;
        }
        return availableMove;
    }
    //find movees for rooks and queens
    public boolean rookMoves(int row, int col)
    {
        boolean moveUp = true;
        boolean moveDown = true;
        boolean moveLeft = true;
        boolean moveRight = true;
        boolean availableMove = false;
        boolean white = true;

        //if its a black piece change white to false
        if (board[row][col].getText().compareTo(labels.get(6)) == 0 || board[row][col].getText().compareTo(labels.get(7)) == 0 ||
            board[row][col].getText().compareTo(labels.get(8)) == 0 || board[row][col].getText().compareTo(labels.get(9)) == 0 ||
            board[row][col].getText().compareTo(labels.get(10)) == 0 || board[row][col].getText().compareTo(labels.get(11)) == 0)
            white = false;

        //checking if you can even check in that direction without
        //going out of index
        if (row == 0)   //if at top cant go up
            moveUp=false;
        else if (row == 7)  //if at bottom cant go down
            moveDown=false;
        if (col == 0)
            moveLeft=false; //if on left edge cant go more left
        else if (col == 7)
            moveRight=false; //if on right edge cant go more right

        int newRow = row;
        int newCol = col;
        while (moveUp == true)
        {
            newRow = newRow-1;
            if (isEmpty(board[newRow][newCol]) == true)
            {
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in above, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveUp=false;    //once it hits a piece it theres no more upward spots to move to
                
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in above, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveUp=false;   //once it hits a piece it theres no more upward pieces to move to
            }
            else
                moveUp=false;
            
            if (newRow == 0)
                moveUp=false;
        }
        newRow = row;
        while (moveDown == true)
        {
            newRow = newRow+1;
            if (isEmpty(board[newRow][newCol]) == true)
            {
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in below, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveDown=false;    //once it hits a piece it theres no more downward spots to move to
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in below, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveDown=false;   //once it hits a piece it theres no more downward pieces to move to
            }
            else
                moveDown=false;
            
            if (newRow == 7)
                moveDown=false;
        }
        newRow = row;
        while(moveLeft == true)
        {
            newCol = newCol-1;
            if (isEmpty(board[newRow][newCol]) == true)
            {
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in left of, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveLeft=false;    //once it hits a piece it theres no more leftward spots to move to
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in left of, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveLeft=false;   //once it hits a piece it theres no more leftward pieces to move to
            }
            else
                moveLeft=false;
            
            if (newCol == 0)
                moveLeft=false;
        }
        newCol = col;
        while (moveRight == true)
        {
            newCol = newCol+1;
            if (isEmpty(board[newRow][newCol]) == true)
            {
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right of, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveRight=false;    //once it hits a piece it theres no more rightward spots to move to
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
                moveRight=false;   //once it hits a piece it theres no more rightward pieces to move to
            }
            else
                moveRight=false;
            
            if (newCol == 7)
                moveRight=false;
        }
        return availableMove;
    }
    //find moves for knights
    public boolean knightMoves(int row, int col)
    {
        boolean upLeft = true;
        boolean upRight = true;
        boolean leftUp = true;
        boolean leftDown = true;
        boolean downLeft = true;
        boolean downRight = true;
        boolean rightUp = true;
        boolean rightDown = true;
        boolean availableMove = false;
        boolean white = true;

        //if its a black piece change white to false
        if (board[row][col].getText().compareTo(labels.get(6)) == 0 || board[row][col].getText().compareTo(labels.get(7)) == 0 ||
            board[row][col].getText().compareTo(labels.get(8)) == 0 || board[row][col].getText().compareTo(labels.get(9)) == 0 ||
            board[row][col].getText().compareTo(labels.get(10)) == 0 || board[row][col].getText().compareTo(labels.get(11)) == 0)
            white = false;

        //if the horse is in a certain spot, checking for the spots in the brackets will cause out of index error
        if (row == 0)
        {
            upLeft=false;
            upRight=false;
            rightUp=false;
            leftUp=false;
        }
        else if (row == 1)
        {
            upLeft=false;
            upRight=false;
        }
        else if (row == 6)
        {
            downLeft=false;
            downRight=false;
        }
        else if (row == 7)
        {
            downLeft=false;
            downRight=false;
            rightDown=false;
            leftDown=false;
        }
        if (col == 0)
        {
            leftUp=false;
            leftDown=false;
            upLeft=false;
            downLeft=false;
        }
        else if (col == 1)
        {
            leftUp=false;
            leftDown=false;
        }
        else if (col == 6)
        {
            rightUp=false;
            rightDown=false;
        }
        else if (col ==7)
        {
            rightUp=false;
            rightDown=false;
            upRight=false;
            downRight=false;
        }

        int newRow = row;
        int newCol = col;
        //checking for available jumps
        if (upLeft == true)
        {
            newRow = row - 2;
            newCol = col - 1;
            if (isEmpty(board[row-2][col-1]))
            {
                board[row-2][col-1].addActionListener(secButtonListener);
                availableMove = true;
                board[row-2][col-1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in upper left jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in upper left jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (upRight == true)
        {
            newRow = row - 2;
            newCol = col + 1;
            if (isEmpty(board[row-2][col+1]))
            {
                board[row-2][col+1].addActionListener(secButtonListener);
                availableMove = true;
                board[row-2][col+1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in upper right jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in upper right jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (leftUp == true)
        {
            newRow = row - 1;
            newCol = col - 2;
            if (isEmpty(board[row-1][col-2]))
            {
                board[row-1][col-2].addActionListener(secButtonListener);
                availableMove = true;
                board[row-1][col-2].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in left up jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in left up jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (leftDown == true)
        {
            newRow = row + 1;
            newCol = col - 2;
            if (isEmpty(board[row+1][col-2]))
            {
                board[row+1][col-2].addActionListener(secButtonListener);
                availableMove = true;
                board[row+1][col-2].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in left down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in left down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (downLeft == true)
        {
            newRow = row + 2;
            newCol = col - 1;
            if (isEmpty(board[row+2][col-1]))
            {
                board[row+2][col-1].addActionListener(secButtonListener);
                availableMove = true;
                board[row+2][col-1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in lower left jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in lower left jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (downRight == true)
        {
            newRow = row + 2;
            newCol = col + 1;
            if (isEmpty(board[row+2][col+1]))
            {
                board[row+2][col+1].addActionListener(secButtonListener);
                availableMove = true;
                board[row+2][col+1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in lower right jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in lower right jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (rightUp == true)
        {
            newRow = row - 1;
            newCol = col + 2;
            if (isEmpty(board[row-1][col+2]))
            {
                board[row-1][col+2].addActionListener(secButtonListener);
                availableMove = true;
                board[row-1][col+2].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right up jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right up jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (rightDown == true)
        {
            newRow = row + 1;
            newCol = col + 2;
            if (isEmpty(board[row+1][col+2]))
            {
                board[row+1][col+2].addActionListener(secButtonListener);
                availableMove = true;
                board[row+1][col+2].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        return availableMove;
    }
    //find moves for kings
    public boolean kingMoves(int row, int col)
    {
        boolean up = true;
        boolean down = true;
        boolean left = true;
        boolean right = true;
        boolean topLeft = true;
        boolean bottomLeft = true;
        boolean topRight = true;
        boolean bottomRight = true;
        boolean availableMove = false;
        boolean white = true;

        //if its a black piece change white to false
        if (board[row][col].getText().compareTo(labels.get(6)) == 0 || board[row][col].getText().compareTo(labels.get(7)) == 0 ||
            board[row][col].getText().compareTo(labels.get(8)) == 0 || board[row][col].getText().compareTo(labels.get(9)) == 0 ||
            board[row][col].getText().compareTo(labels.get(10)) == 0 || board[row][col].getText().compareTo(labels.get(11)) == 0)
            white = false;

        //checking if checking for these will lead to out of index error
        if (row == 0)
        {
            up=false;
            topLeft=false;
            topRight=false;
        }
        else if (row == 7)
        {
            down=false;
            bottomLeft=false;
            bottomRight=false;
        }
        if (col == 0)
        {
            left=false;
            topLeft=false;
            bottomLeft=false;
        }
        else if (col == 7)
        {
            right=false;
            topRight=false;
            bottomRight=false;
        }

        int newRow, newCol;
        if (topLeft == true)
        {
            newRow = row - 1;
            newCol = col - 1;
            if (isEmpty(board[row-1][col-1]))
            {
                board[row-1][col-1].addActionListener(secButtonListener);
                availableMove = true;
                board[row-1][col-1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (up == true)
        {
            newRow = row - 1;
            newCol = col;
            if (isEmpty(board[row-1][col]))
            {
                board[row-1][col].addActionListener(secButtonListener);
                availableMove = true;
                board[row-1][col].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (topRight == true)
        {
            newRow = row - 1;
            newCol = col + 1;
            if (isEmpty(board[row-1][col+1]))
            {
                board[row-1][col+1].addActionListener(secButtonListener);
                availableMove = true;
                board[row-1][col+1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (left == true)
        {
            newRow = row;
            newCol = col - 1;
            if (isEmpty(board[row][col-1]))
            {
                board[row][col-1].addActionListener(secButtonListener);
                availableMove = true;
                board[row][col-1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (bottomLeft == true)
        {
            newRow = row + 1;
            newCol = col - 1;
            if (isEmpty(board[row+1][col-1]))
            {
                board[row+1][col-1].addActionListener(secButtonListener);
                availableMove = true;
                board[row+1][col-1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (down == true)
        {
            newRow = row + 1;
            newCol = col;
            if (isEmpty(board[row+1][col]))
            {
                board[row+1][col].addActionListener(secButtonListener);
                availableMove = true;
                board[row+1][col].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (bottomRight == true)
        {
            newRow = row + 1;
            newCol = col + 1;
            if (isEmpty(board[row+1][col+1]))
            {
                board[row+1][col+1].addActionListener(secButtonListener);
                availableMove = true;
                board[row+1][col+1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
        }
        if (right == true)
        {
            newRow = row;
            newCol = col + 1;
            if (isEmpty(board[row][col+1]))
            {
                board[row][col+1].addActionListener(secButtonListener);  
                availableMove = true;
                board[row][col+1].setBackground(optionsColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white == true && ((board[newRow][newCol].getText().compareTo(labels.get(6)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(7)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(8)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(9)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(10)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(11)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is white, and the piece in the top left is black
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(captureColor);
            }
            else if (isEmpty(board[newRow][newCol]) == false && white != true && ((board[newRow][newCol].getText().compareTo(labels.get(0)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(1)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(2)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(3)) == 0 ||
            board[newRow][newCol].getText().compareTo(labels.get(4)) == 0 || board[newRow][newCol].getText().compareTo(labels.get(5)) == 0))) 
            {
                //if statement checks if there is a piece in right down jump, if the selected piece is black, and the piece in the top left is white
                //this indicates a capture move
                availableMove=true;
                board[newRow][newCol].addActionListener(secButtonListener);
                board[newRow][newCol].setBackground(new Color(246, 153, 102));
            }
        }
        return availableMove;
    }
    //checks if a piece is in check
    public boolean isInCheck(boolean black)
    {
        //will find the button and set their row and column to this
        //these make it easier to do the math of where the pieces could capture the king from
        int row=0, col=0;

        //method will return this
        boolean inCheck = false;
        //boolean inBounds=true;
        int newRow, newCol;
        //first find where the black king is
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                if (black == true && board[i][j].getText().compareTo(labels.get(6)) == 0)//if black piece then looking for black king
                {
                    row=i;
                    col=j;
                }
                else if (black == false && board[i][j].getText().compareTo(labels.get(0)) == 0)//if white piece then looking for white king
                {
                    row=i;
                    col=j;
                }
            }
        }
        //check if there are white pawns that can attack black king
        if (row!=7 && black==true)
        {
            if (col!=0 && board[row+1][col-1].getText().compareTo(labels.get(5))==0)
            {
                inCheck = true;
                board[row+1][col-1].setBackground(Color.red);
            }
            if (col!=7 &&  board[row+1][col+1].getText().compareTo(labels.get(5))==0)
            {
                inCheck = true;
                board[row+1][col+1].setBackground(Color.red);
            }
        }
        //check if there are black pawns that can attack white king
        if (row!=0 && black==false)
        {
            if (col!=0 && board[row-1][col-1].getText().compareTo(labels.get(11))==0)
            {
                inCheck = true;
                board[row-1][col-1].setBackground(Color.red);
            }
            if (col!=7 &&  board[row-1][col+1].getText().compareTo(labels.get(11))==0)
            {
                inCheck = true;
                board[row-1][col+1].setBackground(Color.red);
            }
        }
        //check for rooks
        //check for rook or queen above 
        newRow = row-1;
        newCol = col;
        while (newRow != 0 && row != 0)
        {
            if (black == true)
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(2))==0 || board[newRow][newCol].getText().compareTo(labels.get(1))==0)
                {
                    inCheck = true;
                    board[newRow][newCol].setBackground(Color.red);
                    newRow=0;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a rook and not a space the exit the rook loop
                    newRow=0;
                else
                    newRow = newRow-1;
            }
            else
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(7))==0 || board[newRow][newCol].getText().compareTo(labels.get(8))==0)
                {
                    inCheck = true;             
                    board[newRow][newCol].setBackground(Color.red);
                    newRow=0;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a rook and not a space the exit the rook loop
                    newRow=0;
                else
                    newRow = newRow-1;
            }
        }
        newRow=row+1;
        newCol=col;
        //check below for a rook or queen
        while (newRow != 7 && row != 7)
        {
            if (black == true)
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(2))==0 || board[newRow][newCol].getText().compareTo(labels.get(1))==0)
                {
                    inCheck = true;  
                    board[newRow][newCol].setBackground(Color.red);
                    newRow=7;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a rook and not a space the exit the rook loop
                    newRow=7;
                else
                    newRow = newRow+1;
            }
            else
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(7))==0 || board[newRow][newCol].getText().compareTo(labels.get(8))==0)
                {
                    inCheck = true;                 
                    board[newRow][newCol].setBackground(Color.red);
                    newRow=7;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a rook and not a space the exit the rook loop
                    newRow=7;
                else
                    newRow = newRow+1;
            }
        }
        newRow=row;
        newCol=col-1;
        //check left for a rook or queen 
        while (newCol != 0 && col != 0)
        {
            //if it finds a rook then its in check
            if (black == true)
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(2))==0 || board[newRow][newCol].getText().compareTo(labels.get(1))==0)
                {
                    inCheck = true;  
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=0;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a rook and not a space the exit the rook loop
                    newCol=0;
                else
                    newCol = newCol-1;
            }
            else
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(7))==0 || board[newRow][newCol].getText().compareTo(labels.get(8))==0)
                {
                    inCheck = true;                   
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=0;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a rook and not a space the exit the rook loop
                    newCol=0;
                else
                    newCol = newCol-1;
            }
        }
        newRow=row;
        newCol=col+1;
        //check right for a rook or queen
        while (newCol != 7 && col != 7)
        {
            if (black == true)
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(2))==0 || board[newRow][newCol].getText().compareTo(labels.get(1))==0)
                {
                    inCheck = true;
                    
                    board[newRow][newCol].setBackground(Color.red);
                    newCol = 7;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a rook and not a space the exit the rook loop
                    newCol=7;
                else
                    newCol = newCol+1;
            }
            else
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(7))==0 || board[newRow][newCol].getText().compareTo(labels.get(8))==0)
                {
                    inCheck = true;
                    
                    board[newRow][newCol].setBackground(Color.red);
                    newCol = 7;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a rook and not a space the exit the rook loop
                    newCol=7;
                else
                    newCol = newCol+1;
            }
        }

        //check for bishops or queens
        //check for bishop or queen from top left
        newRow=row-1;
        newCol=col-1;
        while (newCol != 0 && newRow!=0 && col!=0 && row !=0)
        {
            if (black == true)
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(3))==0 || board[newRow][newCol].getText().compareTo(labels.get(1))==0)
                {
                    inCheck = true;
                    
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=0;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a bishop and not a space the exit the bishop loop
                    newCol=0;
                else
                {
                    newCol = newCol-1;
                    newRow = newRow-1;
                }
            }
            else
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(7))==0 || board[newRow][newCol].getText().compareTo(labels.get(9))==0)
                {
                    inCheck = true;                
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=0;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a bishop and not a space the exit the bishop loop
                    newCol=0;
                else
                {
                    newCol = newCol-1;
                    newRow = newRow-1;
                } 
            }
        }
        //check for bishop or queen from top right
        newRow=row-1;
        newCol=col+1;
        while (newCol != 7 && newRow!=0 && col!=7 && row !=0)
        {
            if (black == true)
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(3))==0 || board[newRow][newCol].getText().compareTo(labels.get(1))==0)
                {
                    inCheck = true;                  
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=7;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a bishop and not a space the exit the bishop loop
                    newCol=7;
                else
                {
                    newCol = newCol+1;
                    newRow = newRow-1;
                }
            }
            else
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(7))==0 || board[newRow][newCol].getText().compareTo(labels.get(9))==0)
                {
                    inCheck = true;                    
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=7;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a bishop and not a space the exit the bishop loop
                    newCol=7;
                else
                {
                    newCol = newCol+1;
                    newRow = newRow-1;
                }
            }
        }
        //check for bishop or queen from bottom right
        newRow=row+1;
        newCol=col+1;
        while (newCol != 8 && newRow!= 8 && col!=7 && row !=7)
        {
            if (black == true)
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(3))==0 || board[newRow][newCol].getText().compareTo(labels.get(1))==0)
                {
                    inCheck = true;                  
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=8;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a bishop and not a space the exit the bishop loop
                {
                    newCol=8;
                }
                else
                {
                    newCol = newCol+1;
                    newRow = newRow+1;
                }
            }
            else
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(7))==0 || board[newRow][newCol].getText().compareTo(labels.get(9))==0)
                {
                    inCheck = true;               
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=8;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a bishop and not a space the exit the bishop loop
                {
                    newCol=8;
                }
                else
                {
                    newCol = newCol+1;
                    newRow = newRow+1;
                } 
            }
        }
        //check for bishop or queen from bottom left
        newRow=row+1;
        newCol=col-1;
        while (newCol != 0 && newRow!=7 && col!=0 && row !=7)
        {
            if (black == true)
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(3))==0 || board[newRow][newCol].getText().compareTo(labels.get(1))==0)
                {
                    inCheck = true;
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=7;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a bishop and not a space the exit the bishop loop
                    newCol=0;
                else
                {
                    newCol = newCol-1;
                    newRow = newRow+1;
                }
            }
            else
            {
                if (board[newRow][newCol].getText().compareTo(labels.get(7))==0 || board[newRow][newCol].getText().compareTo(labels.get(9))==0)
                {
                    inCheck = true;
                    board[newRow][newCol].setBackground(Color.red);
                    newCol=7;
                }
                else if (board[newRow][newCol].getText().compareTo(" ")!=0) //if its not a bishop and not a space the exit the bishop loop
                    newCol=0;
                else
                {
                    newCol = newCol-1;
                    newRow = newRow+1;
                }
            }
        }

        //check for knight
        //check for knights above
        newRow=row;
        newCol=col;
        if (row>1) //row has to be greater than 1 to have a knight above it
        {
            if (black == true)
            {
                if (col!=7 && board[row-2][col+1].getText().compareTo(labels.get(4))==0)  //check up and right
                {
                    inCheck = true;
                    board[row-2][col+1].setBackground(Color.red);
                }
                else if (col!=0 && board[row-2][col-1].getText().compareTo(labels.get(4))==0) //check up and left
                {
                    inCheck=true;
                    board[row-2][col-1].setBackground(Color.red);
                }
            }
            else 
            {
                if (col!=7 && board[row-2][col+1].getText().compareTo(labels.get(10))==0)  //check up and right
                {
                    inCheck = true;
                    board[row-2][col+1].setBackground(Color.red);
                }
                else if (col!=0 && board[row-2][col-1].getText().compareTo(labels.get(10))==0) //check up and left
                {
                    inCheck=true;
                    board[row-2][col+1].setBackground(Color.red);
                }
            }
        }
        //checking left for knights
        if (col>1)  //need to be at least on column 2 to have a knight to the left
        {
            if (black == true)
            {
                if (row!=7 && board[row+1][col-2].getText().compareTo(labels.get(4))==0)  //check left and down
                {
                    inCheck = true;
                    board[row+1][col-2].setBackground(Color.red);
                }
                else if (row!=0 && board[row-1][col-2].getText().compareTo(labels.get(4))==0) //check left and up
                {
                    inCheck=true;
                    board[row-1][col-2].setBackground(Color.red);
                }
            }
            else
            {
                if (row!=7 && board[row+1][col-2].getText().compareTo(labels.get(10))==0)  //check left and down
                {
                    inCheck = true;
                    board[row+1][col-2].setBackground(Color.red);
                }
                else if (row!=0 && board[row-1][col-2].getText().compareTo(labels.get(10))==0) //check left and up
                {
                    inCheck=true;
                    board[row-1][col-2].setBackground(Color.red);
                }
            }
        }
        //checking down for knights
        if (row<6)  //need to be at below column 6 to have a knight below it
        {
            if (black == true)
            {
                if (col!=0 && board[row+2][col-1].getText().compareTo(labels.get(4))==0)  //check down and left
                {
                    inCheck = true;
                    board[row+2][col-1].setBackground(Color.red);
                }
                else if (col!=7 && board[row+2][col+1].getText().compareTo(labels.get(4))==0) //check down and right
                {
                    inCheck=true;
                    board[row+2][col+1].setBackground(Color.red);
                }
            }
            else
            {
                if (col!=0 && board[row+2][col-1].getText().compareTo(labels.get(10))==0)  //check down and left
                {
                    inCheck = true;
                    board[row+2][col-1].setBackground(Color.red);
                }
                else if (col!=7 && board[row+2][col+1].getText().compareTo(labels.get(10))==0) //check down and right
                {
                    inCheck=true;
                    board[row+2][col+1].setBackground(Color.red);
                }
            }
        }
        //checking right for knights
        if (col<6)  //need to be at below column 6 to have a knight to the right
        {
            if (black == true)
            {
                if (row!=7 && board[row+1][col+2].getText().compareTo(labels.get(4))==0)  //check right and down
                {
                    inCheck = true;
                    board[row+1][col+2].setBackground(Color.red);
                }
                else if (row!=0 && board[row-1][col+2].getText().compareTo(labels.get(4))==0) //check right and up
                {
                    inCheck=true;
                    board[row-1][col+2].setBackground(Color.red);
                }
            }
            else
            {
                if (row!=7 && board[row+1][col+2].getText().compareTo(labels.get(10))==0)  //check right and down
                {
                    inCheck = true;
                    board[row+1][col+2].setBackground(Color.red);
                }
                else if (row!=0 && board[row-1][col+2].getText().compareTo(labels.get(10))==0) //check right and up
                {
                    inCheck=true;
                    board[row-1][col+2].setBackground(Color.red);
                }
            }
        }
        return inCheck;
    }
    //looks for white king
    public boolean isWhiteKingAlive()
    {
        boolean alive = false;
        //search board for white king
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                if (board[i][j].getText().compareTo(labels.get(0))==0) //if you find it its alive
                    alive = true;
            }
        }
        return alive;
    }
    //looks for black king
    public boolean isBlackKingAlive()
    {
        boolean alive = false;
        //search board for black king
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                if (board[i][j].getText().compareTo(labels.get(6))==0) //if you find it its alive
                    alive = true;
            }
        }
        return alive;
    }
    //turns off all listeners so no more moves can be played
    public void endGame()
    {
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                board[i][j].removeActionListener(buttonlistener);
                board[i][j].removeActionListener(secButtonListener);
            }
        }
    }
    //looks for the piece clicked
    private class PositionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //search the board for the button 
            for (int i=0; i<8; i++)
            {
                for (int j=0; j<8; j++)
                {
                    if (e.getSource() == board [i][j])  //find the button that was clicked
                    {
                        availableMoves(board[i][j]);
                        board[i][j].setBackground(new Color (147, 147, 147));   //change its background color
                    }
                }
            }
        }
    }
    //looks for the position the piece wants to go and replaces the text
    private class SecondPosListener implements ActionListener
    {
        public void actionPerformed(ActionEvent f)
        {
            if (moveMade == true)
            {
                deleteText.setText(" ");
                for (int i=0; i<8; i++)
                {
                    for (int j=0; j<8; j++)
                    {
                        if (f.getSource() == board[i][j])
                        {
                            resetCheckered();
                            
                            //fixing labels because for some reason they didnt work without this?
                            for (int k=0; k<12; k++)
                                if (temp.toString().compareTo(labels.get(k)) == 0)
                                    board[i][j].setText(labels.get(k));

                            //check for pawn promotion (pawn at opposite end)
                            if (temp.toString().compareTo(labels.get(5))==0 && i == 0)  //if its a white pawn on row 0 it needs promotion
                            {
                                //find what piece the player chose to promote their pawn to and set it
                                for (int k=1; k<5; k++)
                                    if (whitePromotion.toString().compareTo(labels.get(k)) == 0)    
                                        board[i][j].setText(labels.get(k));
                                board[i][j].setForeground(whiteColor);
                            }//same but for black promotion
                            else if (temp.toString().compareTo(labels.get(11))==0 && i == 7)  //if its a white pawn on row 0 it needs promotion
                            {
                                for (int k=7; k<11; k++)
                                    if (blackPromotion.toString().compareTo(labels.get(k)) == 0)
                                        board[i][j].setText(labels.get(k));
                                board[i][j].setForeground(blackColor);
                            }

                            if (temp.toString().compareTo(labels.get(0)) == 0 || temp.toString().compareTo(labels.get(1)) == 0 ||
                                temp.toString().compareTo(labels.get(2)) == 0 || temp.toString().compareTo(labels.get(3)) == 0 ||
                                temp.toString().compareTo(labels.get(4)) == 0 || temp.toString().compareTo(labels.get(5)) == 0)
                            {
                                board[i][j].setForeground(whiteColor);

                                //run fuction to see if black is in check
                                blackCheck = isInCheck(true);

                                //if its in check let user know then make it a their turn
                                if (isBlackKingAlive() == false)
                                    chessErrors.youWin(1); //if black king is gone player 1 wins
                                else if (blackCheck == true)
                                    chessErrors.inCheck();
                                
                                blackTurn();
                                if (isBlackKingAlive()==false)
                                    endGame();
                                    
                            }
                            else if (temp.toString().compareTo(labels.get(6)) == 0 || temp.toString().compareTo(labels.get(7)) == 0 ||
                                     temp.toString().compareTo(labels.get(8)) == 0 || temp.toString().compareTo(labels.get(9)) == 0 ||
                                     temp.toString().compareTo(labels.get(10)) == 0 || temp.toString().compareTo(labels.get(11)) == 0)
                            {   //same but for black pieces
                                board[i][j].setForeground(blackColor);

                                whiteCheck = isInCheck(false);

                                //if its in check let user knw and make it their turn
                                if (isWhiteKingAlive()==false)
                                    chessErrors.youWin(2); //if white king is gone player 2 wins
                                else if (whiteCheck == true)
                                    chessErrors.inCheck();
                                
                                whiteTurn();

                                if (isWhiteKingAlive() == false)
                                    endGame();
                                    
                            }
                        }
                    }
                }
                temp.delete(0, temp.length());
            }
        }
    }

}