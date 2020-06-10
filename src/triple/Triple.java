package triple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.apple.eio.FileManager.getResource;


class Triple extends JFrame {

    static final int N = 8, W = 600, H = 600;
    AtomicInteger gameing = new AtomicInteger(0);
    GamePanel gamePanel= new GamePanel(W+500,H+80,"pic/background.png",this);
    InitialPanel initialPanel=new InitialPanel(W+500,H+80,this);
    File record;
    FileReader reader;
    FileWriter writer;
    LocalDateTime startTime;
    CardLayout panelSwitcher=new CardLayout();
    DataBase gameRecord=new DataBase("record");
    String name="sherco";
    AtomicInteger imagePrepared=new AtomicInteger(0);

    static WaitMusic Wait = new WaitMusic();
    static GameMusic Game = new GameMusic();
    static ButtonMusic Btn = new ButtonMusic();
    static TripleMusic Tri = new TripleMusic();
    static LabelMusic Lab = new LabelMusic();
    static SkillMusic[] Skl = new SkillMusic[4];


    public Triple() {
        LocalDateTime a=LocalDateTime.now();
        String s=(a.toString().replace("T"," ").split("\\."))[0];
        System.out.println(s);

        setResizable(false);


        this.setLayout(panelSwitcher);

        setSize(W + 500, H + 80);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(gamePanel,"game");
        add(initialPanel,"initial");

        Wait.loop();
        panelSwitcher.show(this.getContentPane(),"initial");

    }

    public static void main(String[] args) {
        new Triple().setVisible(true);
    }


}
