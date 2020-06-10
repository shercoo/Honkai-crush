package triple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static triple.Triple.Btn;

class GamePanel extends JPanel {
    Image backgroundPic;
    JButton pauseButton;
    MainPanel mainPanel;
    PausePanel pausePanel;
    GamePanel ptr=this;
    Triple triple;
    GamePanel(int w,int h,String bgPath,Triple triple){
        super();
        this.triple=triple;
        try {
            backgroundPic = new ImageIcon(bgPath).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLayout(null);
        setSize(w,h);

        mainPanel=new MainPanel(triple);
        mainPanel.setLayout(null);
        mainPanel.setSize(w-100, h-50);
        mainPanel.setLocation(50, 20);
        mainPanel.setOpaque(false);
        add(mainPanel);

        pausePanel=new PausePanel(w,h,triple);
        pausePanel.setLocation(0,0);
        pausePanel.setOpaque(false);
        pausePanel.setVisible(false);
        pausePanel.setEnabled(false);
        add(pausePanel,0);


        pauseButton=new JButton("ÔÝÍ£");
        pauseButton.setSize(75,50);
        pauseButton.setLocation(w-85,0);
        pauseButton.setVisible(true);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                triple.panelSwitcher.show(triple.getContentPane(),"pause");
                pausePanel.Activate();
                mainPanel.Pause();
                Btn.start();
            }
        });
        add(pauseButton);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundPic, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}


