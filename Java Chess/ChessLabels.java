public class ChessLabels{

    private String[] labels = new String[12];

    public ChessLabels()
    {
        labels[0] = ("\u2654");
        labels[1] = ("\u2655");
        labels[2] = ("\u2656");
        labels[3] = ("\u2657");
        labels[4] = ("\u2658");
        labels[5] = ("\u2659");
        labels[6] = ("\u265A");
        labels[7] = ("\u265B");
        labels[8] = ("\u265C");
        labels[9] = ("\u265D");
        labels[10] = ("\u265E");
        labels[11] = ("\u265F");
    }

    public String get(int i)
    {
        return labels[i];
    }
}