package triple;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static triple.Triple.Btn;

public class ShowImagePanel extends JPanel {
    static double contentRatio=0.7;
    JButton returnButton;
    JButton saveButton;
    JButton loadButton;
    JPanel father;
    Picture picture;
    ShowImagePanel ptr=this;
    ImageBase imageBase;
    Triple triple;

    ShowImagePanel(Picture picture,ImageBase imageBase,Triple triple,JPanel father) {
        super();
        this.picture=picture;
        this.imageBase=imageBase;
        this.triple = triple;
        this.father = father;
        int w=father.getWidth(),h=father.getHeight();
/*
        System.out.println(w);
        System.out.println(h);
*/

        setSize(w,h);
        setLayout(null);
        setOpaque(false);

        returnButton=new JButton("返回");
        returnButton.setSize(100,50);
        returnButton.setLocation(w/2-100,(int)((1.-(1.-contentRatio)/2.)*h)+5);
        returnButton.setVisible(true);
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                father.remove(ptr);
                for (Component c :
                        triple.gamePanel.mainPanel.getComponents()) {
                    c.setEnabled(true);
                    c.setVisible(true);
                }
                triple.gamePanel.pauseButton.setEnabled(true);
                triple.gamePanel.mainPanel.setEnabled(true);
                father.setEnabled(true);
                father.repaint();
                father.revalidate();
                Btn.start();
            }
        });
        add(returnButton);

        saveButton=new JButton("保存到本地");
        saveButton.setSize(100,50);
        saveButton.setLocation(w/2,(int)((1.-(1.-contentRatio)/2.)*h)+5);
        saveButton.setVisible(true);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] temp=picture.filename.split("\\.");
                    String formatName=temp[temp.length-1];
                    ImageIO.write(picture.image,formatName,new File("./pic/"+picture.filename));
                    imageBase.Insert(picture.filename);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                saveButton.setText("已保存");
                saveButton.setEnabled(false);
                Btn.start();
            }
        });
        add(saveButton);


    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage image=picture.image;
        int w=getWidth(),h=getHeight(),iw=image.getWidth(),ih=image.getHeight(),dw,dh;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.85f));
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRoundRect(0, 0, w, h, 10, 10);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        double r=((double)w/h)/((double)iw/ih);
        if(r>1){
            dh=(int)(contentRatio*h);
            dw=(int)((double)iw/ih*dh);
        }
        else {
            dw=(int)(contentRatio*w);
            dh=(int)((double)ih/iw*dw);
        }
        Image scaledImage = image.getScaledInstance(dw, dh, Image.SCALE_SMOOTH);
        g.drawImage(scaledImage,(w-dw)/2,(h-dh)/2,dw,dh,this);

    }

}
