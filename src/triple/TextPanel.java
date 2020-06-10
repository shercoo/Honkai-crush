package triple;

import javax.swing.*;
import java.awt.*;

public class TextPanel extends JPanel {
    JTextArea textArea;
    JScrollPane scrollPane;
    TextPanel(int w,int h){
        super();
        setLayout(null);
        setSize(w,h);
        setVisible(true);
        setOpaque(false);

        textArea=new JTextArea();
        textArea.setEditable(false);
        textArea.setOpaque(false);

        scrollPane=new JScrollPane(textArea);
        scrollPane.setSize(w,h);
        scrollPane.setLocation(0,0);

        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVisible(true);

        scrollPane.setBorder(null);
        add(scrollPane);
    }

    TextPanel(int w,int h,String s){
        this(w,h);
        AppendText(s);
    }

    void AppendText(String s) {
        textArea.append(s);
        textArea.setCaretPosition(textArea.getText().length());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.85f));
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
}
