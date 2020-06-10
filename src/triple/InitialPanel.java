package triple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static triple.Triple.Btn;
import static triple.Triple.Wait;

public class InitialPanel extends JPanel {
    JButton startButton;
    JButton loadButton;
    Triple triple;
    InitialPanel ptr;
    InitialPanel(int w,int h,Triple triple){
        super();
        ptr=this;
        this.triple=triple;
        setLayout(null);
        setSize(w,h);
        startButton=new JButton("开始游戏");
        startButton.setSize(100,50);
        startButton.setLocation((w-100)/2,(h-50)/2);
        startButton.setVisible(true);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                triple.panelSwitcher.show(triple.getContentPane(),"game");
                triple.gamePanel.mainPanel.Begin();
                triple.gamePanel.mainPanel.Continue();
                Btn.start();
                Wait.stop();
            }
        });
        this.add(startButton);

        loadButton=new JButton("载入存档");
        loadButton.setSize(100,50);
        loadButton.setLocation(w/2-50,h/2+25);
        loadButton.setVisible(true);
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RecordPanel recordPanel=new RecordPanel(700,500,triple,ptr);
                recordPanel.setLocation((w-700)/2,(h-500)/2);
                recordPanel.scrollPane.requestFocus();
                recordPanel.saveButton.setEnabled(false);
                ptr.Disable();
                ptr.revalidate();
                ptr.add(recordPanel,0);
                Btn.start();
                /*triple.gamePanel.mainPanel.Print();*/
            }
        });
        add(loadButton);

    }

    void Disable(){
        this.setEnabled(false);
        for (Component c:
                this.getComponents()) {
            c.setVisible(false);
            c.setEnabled(false);
        }
    }

    void Enable(){
        this.setEnabled(true);
        for (Component c:
                this.getComponents()) {
            c.setVisible(true);
            c.setEnabled(true);
        }

    }

}
