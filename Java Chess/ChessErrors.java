import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ChessErrors {
    
    JFrame frame = new JFrame("Attention Player");
    ChessLabels labels = new ChessLabels();
    
    //needed for inCheck popup
    JPanel inCheckPanel;
    boolean checkPanelOn = false;

    //needed for noMoves popup
    JPanel noMovesPanel;
    boolean noMovesPanelOn = false;


    public ChessErrors()
    {
        //frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    //sees if there is a panel on the frame is yes remove it
    public void clearFrame()
    {
        if (checkPanelOn == true)
        {
            frame.remove(inCheckPanel);
            checkPanelOn=false;
        }
        if (noMovesPanelOn == true)
        {
            frame.remove(noMovesPanel);
            noMovesPanelOn=false;
        }
    }
    public void noMoves()
    {
        clearFrame();
        noMovesPanel = new JPanel();
        JLabel noMovesText = new JLabel("This piece has no available moves. Please select another.");
        
        noMovesText.setFont(new Font("Monospaced", Font.PLAIN, 15));
        noMovesText.setBackground(Color.gray);
        //noMovesText.setForeground(new Color());
        noMovesPanel.add(noMovesText);
        noMovesPanelOn=true;
        frame.add(noMovesPanel);
        frame.setSize(600, 100);
        frame.setVisible(true);
    }

    public void inCheck()
    {
        clearFrame();
        inCheckPanel = new JPanel();
        JLabel inCheckText = new JLabel("You are in check. Protect your King.");

        inCheckText.setFont(new Font("Monospaced", Font.PLAIN, 15));
        inCheckText.setBackground(Color.gray);
        //noMovesText.setForeground(new Color());
        inCheckPanel.add(inCheckText);
        checkPanelOn=true;
        frame.add(inCheckPanel);
        frame.setSize(600, 100);
        frame.setVisible(true);
    }

    public void youWin(int player)
    {
        clearFrame();
        String winText = new String("Congratulations player "+player+"! You win!");
        JLabel winLabel = new JLabel(winText);
        JPanel winPanel = new JPanel();

        winLabel.setFont(new Font("Monospaced", Font.PLAIN, 15));
        winLabel.setBackground(Color.gray);
        winPanel.add(winLabel);

        frame.add(winPanel);
        frame.setSize(600, 100);
        frame.setVisible(true);
    }
}
