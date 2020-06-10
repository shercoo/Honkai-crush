package triple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static triple.Triple.Btn;
import static triple.Triple.Wait;

public class PausePanel extends JPanel {
    JButton returnButton;
    JButton continueButton;
    JButton saveButton;
    PausePanel ptr=this;
    Triple triple;

    PausePanel(int w,int h,Triple triple){
        super();
        this.triple=triple;
        setSize(w,h);
        setLayout(null);
        continueButton=new JButton("继续");
        continueButton.setSize(100,50);
        continueButton.setLocation(w/2-50,h/2-25);
        continueButton.setVisible(true);
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                triple.gamePanel.mainPanel.Continue();
                ptr.DeActivate();
                Btn.start();
            }
        });
        add(continueButton);

        saveButton=new JButton("保存进度");
        saveButton.setSize(100,50);
        saveButton.setLocation(w/2-50,h/2+25);
        saveButton.setVisible(true);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RecordPanel recordPanel=new RecordPanel(700,500,triple,ptr);
                recordPanel.setLocation((w-700)/2,(h-500)/2);
                recordPanel.scrollPane.requestFocus();
                ptr.Disable();
                ptr.add(recordPanel,0);
                ptr.revalidate();
                Btn.start();
                /*triple.gamePanel.mainPanel.Print();*/
            }
        });
        add(saveButton);

        returnButton=new JButton("返回主界面");
        returnButton.setSize(100,50);
        returnButton.setLocation(w/2-50,h/2+75);
        returnButton.setVisible(true);
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                triple.initialPanel.Enable();
                triple.panelSwitcher.show(triple.getContentPane(),"initial");
                triple.gamePanel.mainPanel.EndGame();
                ptr.DeActivate();
                Btn.start();
                Wait.loop();
            }
        });
        add(returnButton);

    }

    void Activate(){
        setEnabled(true);
        setVisible(true);
        for (Component c:
                this.getComponents()) {
            c.setEnabled(true);
            c.setVisible(true);
        }
    }

    void DeActivate(){
        setEnabled(false);
        setVisible(false);
        for (Component c:
                this.getComponents()) {
            c.setEnabled(false);
            c.setVisible(false);
        }
    }

    void Disable(){
        this.setEnabled(false);
        for (Component c:
                this.getComponents()) {
            /*System.out.println(c.toString());*/
            c.setEnabled(false);
            c.setVisible(false);
        }
    }

    void Enable(){
        for (Component c:
                this.getComponents()) {
            c.setEnabled(true);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        /*System.out.println("fuck");*/
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.85f));
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
    }

}
