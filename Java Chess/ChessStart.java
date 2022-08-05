import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ChessStart {
    
    //frame that the start screen will be on
    JFrame frame;

    //need labels for buttons that allow user to select what their pawn promotion should be
    protected ChessLabels labels = new ChessLabels();

    //need to have to start game (used in board constructor)
    protected StringBuilder whitePromotion = new StringBuilder(labels.get(1));
    protected StringBuilder blackPromotion = new StringBuilder(labels.get(7));
    protected Color p1Color = Color.white;
    protected Color p2Color = Color.black;
    
    ////for picking options and starting the game
    protected WhiteListener whiteListener = new WhiteListener();
    protected BlackListener blackListener = new BlackListener();
    protected StartGameListener startGameListener = new StartGameListener();

    //need for extra feature of letting player choose their color
    protected P1ColorListener p1ColorListener = new P1ColorListener();
    protected P2ColorListener p2ColorListener = new P2ColorListener();
    //Color[] p1ColorOps = new Color[4];
    Color[] p1ColorOps = {new Color(153, 136, 136), new Color(162, 147, 147), new Color(170, 157, 157), new Color(178, 166, 166)};
    Color[] p2ColorOps = {new Color(136, 162, 170), new Color(147, 170, 178), new Color(157, 178, 185), new Color(166, 185, 191)};

    public ChessStart()
    {
        //frame for starting screen
        frame = new JFrame("Welcome to Chess");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        //will house start game button and player options
        JPanel startingPanel = new JPanel();
        
        //panel for white pawn promotion
        JPanel whiteOptions = new JPanel();
        //adding instructions for pawn promotion selection
        JLabel player1Text = new JLabel("<html><div style='text-align: center;'>Player 1,<br>please select one for pawn promotion during the game.<br>Default is queen.<br>Default color is white.</div></html>");
        whiteOptions.add(player1Text);
        //adding JButtons with options for players future pawn promotion, adding aesthetics
        whiteOptions.setLayout(new GridLayout(5,1));
        JButton whiteButton;
        JButton whiteButton2;

        JPanel colorAndPromotionPanelW;
        for (int i=1; i<5; i++)
        {
            //panel that allows for two buttons side by side on the west side of border layout
            colorAndPromotionPanelW = new JPanel();
            colorAndPromotionPanelW.setLayout(new GridLayout(1,2));

            //making promotion button goes on oeft
            whiteButton = new JButton(labels.get(i));
            whiteButton.setBackground(new Color (47, 47, 47));
            whiteButton.setOpaque(true);
            whiteButton.setFont(new Font("Ariel Unicode MS", Font.BOLD, 45));
            whiteButton.setForeground(Color.white);
            whiteButton.setBorderPainted(true);
            whiteButton.setBorder(BorderFactory.createLineBorder(new Color(17, 17, 17), 5));
            whiteButton.addActionListener(whiteListener);
            colorAndPromotionPanelW.add(whiteButton);

            //making color button goes on right
            whiteButton2 = new JButton();
            whiteButton2.setBackground(p1ColorOps[i-1]);
            whiteButton2.setOpaque(true);
            whiteButton2.setBorderPainted(true);
            whiteButton2.setBorder(BorderFactory.createLineBorder(new Color(17, 17, 17), 5));
            whiteButton2.addActionListener(p1ColorListener);
            colorAndPromotionPanelW.add(whiteButton2);
            whiteOptions.add(colorAndPromotionPanelW);

        }

        //panel for black pawn promotion
        JPanel blackOptions = new JPanel();
        //adding instructions for pawn promotion selection
        JLabel player2Text = new JLabel("<html><div style='text-align: center;'>Player 2,<br>please select one for pawn promotion during the game.<br>Default is queen.<br>Default color is black.</div></html>");
        blackOptions.add(player2Text);
        //adding JButtons with options for players future pawn promotion
        blackOptions.setLayout(new GridLayout(5,1));
        JButton blackButton;
        JButton blackButton2;
  
        JPanel colorAndPromotionPanelB;
        for (int i=7; i<11; i++)
        {
            colorAndPromotionPanelB = new JPanel();
            colorAndPromotionPanelB.setLayout(new GridLayout(1,2));

            //making color button goes on left
            blackButton2 = new JButton();
            blackButton2.setBackground(p2ColorOps[i-7]);
            blackButton2.setOpaque(true);
            blackButton2.setBorderPainted(true);
            blackButton2.setBorder(BorderFactory.createLineBorder(new Color(17, 17, 17), 5));
            blackButton2.addActionListener(p2ColorListener);
            colorAndPromotionPanelB.add(blackButton2);

            //making promotion button goes on right
            blackButton = new JButton(labels.get(i));
            blackButton.setBackground(new Color (47, 47, 47));
            blackButton.setOpaque(true);
            blackButton.setFont(new Font("Ariel Unicode MS", Font.BOLD, 45));
            blackButton.setBorderPainted(true);
            blackButton.setBorder(BorderFactory.createLineBorder(new Color(17, 17, 17), 5));
            blackButton.addActionListener(blackListener);
            colorAndPromotionPanelB.add(blackButton);
            blackOptions.add(colorAndPromotionPanelB);
        }

        //adding the black and white options to panel 
        startingPanel.setLayout(new BorderLayout());
        startingPanel.add(whiteOptions, BorderLayout.WEST);
        startingPanel.add(blackOptions, BorderLayout.EAST);

        //adding welcome text
        JLabel welcomeText = new JLabel("WELCOME TO CHESS", SwingConstants.CENTER);
        startingPanel.add(welcomeText, BorderLayout.NORTH);

        //adding the start game button to panel
        JButton startGame = new JButton("START GAME");
        startGame.addActionListener(startGameListener);
        startingPanel.add(startGame, BorderLayout.CENTER);

        //for aesthetics changing font/background color, button sizes, and font of buttons and labels
        //startingPanel.setBackground(new Color(128, 101, 73));
        startingPanel.setBackground(new Color(17, 17, 17));
        player1Text.setFont(new Font("Monospaced", Font.PLAIN, 13));
        player2Text.setFont(new Font("Monospaced", Font.PLAIN, 13));
        player1Text.setForeground(Color.white);
        player2Text.setForeground(Color.white);
        player2Text.setBackground(new Color(17, 17, 17));
        welcomeText.setFont(new Font("Monospaced", Font.BOLD, 50));
        welcomeText.setForeground(new Color(255, 203, 116));
        welcomeText.setBackground(new Color(17, 17, 17));
        welcomeText.setOpaque(true);
        welcomeText.setVerticalAlignment(SwingConstants.CENTER);
        whiteOptions.setBackground(new Color(17, 17, 17));
        blackOptions.setBackground(new Color(17, 17, 17));
        startGame.setForeground(Color.white);
        startGame.setBackground(new Color(17, 17, 17));
        startGame.setOpaque(false);
        startGame.setBorderPainted(false);
        startGame.setFont(new Font("Monospaced", Font.ITALIC, 15));
        frame.add(startingPanel);
        frame.setSize(1000,800);
    }

    public void show()
    {
        frame.setVisible(true);
    }

    //determines what option white chose to promote its pawn to
    private class WhiteListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            for (int i=1; i<5; i++)
            {
                if (((AbstractButton) e.getSource()).getText().compareTo(labels.get(i))==0)
                {
                    whitePromotion.delete(0, whitePromotion.length());
                    whitePromotion.append(labels.get(i));
                }
            }
        }
    }

    //determines what option black chose to promote its pawn to 
    private class BlackListener implements ActionListener
    {
        public void actionPerformed(ActionEvent f)
        {
            for (int i=7; i<11; i++)
            {
                if (((AbstractButton) f.getSource()).getText().compareTo(labels.get(i))==0)
                {
                    blackPromotion.delete(0, blackPromotion.length());
                    blackPromotion.append(labels.get(i));
                }
            }
        }
    }

    private class P1ColorListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            p1Color = ((AbstractButton) e.getSource()).getBackground();
            
        }
    }

    private class P2ColorListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            p2Color = ((AbstractButton) e.getSource()).getBackground();
        }
    }
    //start game button listener when its clicked the frame closes and the board shows for the game to start
    private class StartGameListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            Board board = new Board(whitePromotion.toString(), blackPromotion.toString(), p1Color, p2Color);
            frame.dispose();
            board.show();
        }
    }
}
