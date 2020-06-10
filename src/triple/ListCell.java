package triple;

import javax.swing.*;
import java.awt.*;

public class ListCell extends JPanel implements ListCellRenderer {
    RecordData recordData;
    boolean isSelected;
    boolean cellhadFocus;
    ListCell(){
        setOpaque(false);
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        recordData=(RecordData)value;
        this.isSelected=isSelected;
        this.cellhadFocus=cellHasFocus;
        return this;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        if(isSelected)
            g2d.setColor(new Color(255, 255, 220));
        else
            g2d.setColor(new Color(255, 255, 240));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        if (recordData.id==0){
            g.setColor(Color.gray);
            g.fillRoundRect(getWidth()/2-20,getHeight()/2-5,40,10,5,5);
            g.fillRoundRect(getWidth()/2-5,getHeight()/2-20,10,40,5,5);
        }else {
            g.setColor(Color.black);
            g.drawString("´æµµ"+Integer.toString(recordData.id)+
                    " "+RecordData.TimeToStr( recordData.dateTime) +
                    " "+recordData.name+
                    " µÚ"+recordData.stage+"¹Ø", 20, 20);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(60,60);
    }
}
