package triple;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static triple.Triple.Btn;
import static triple.Triple.Wait;

public class RecordPanel extends JPanel {
    JButton returnButton;
    JButton saveButton;
    JButton loadButton;
    JScrollPane scrollPane;
    DefaultListModel model;
    JList recordList;
    JPanel father;
    RecordPanel ptr=this;
    Triple triple;

    RecordPanel(int w,int h,Triple triple,JPanel father){
        super();
        this.triple=triple;
        this.father=father;
        setSize(w,h);
        setLayout(null);
        setOpaque(true);
        setBorder(null);

        model=new DefaultListModel<>();
        ArrayList<RecordData> records=triple.gameRecord.QueryAll();
        if(records.size()>0) {
            for (RecordData record :
                    records) {
                model.addElement(record);
            }
        }
        model.addElement(new RecordData());
        recordList=new JList(model);
        recordList.setCellRenderer(new ListCell());
        recordList.setOpaque(true);
        recordList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

            }
        });

        scrollPane=new JScrollPane(recordList);
        scrollPane.setSize(w,h-100);
        scrollPane.setVisible(true);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBorder(null);
        add(scrollPane);

        returnButton=new JButton("返回");
        returnButton.setSize(100,50);
        returnButton.setLocation(w/2-150,h-75);
        returnButton.setVisible(true);
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                triple.gamePanel.mainPanel.showButton.setVisible(true);
                father.remove(ptr);
                father.setEnabled(true);
                for (Component c:
                        father.getComponents()) {
                    c.setEnabled(true);
                    c.setVisible(true);
                }
                father.repaint();
                father.revalidate();
                Btn.start();
            }
        });
        add(returnButton);

        saveButton=new JButton("保存进度");
        saveButton.setSize(100,50);
        saveButton.setLocation(w/2-50,h-75);
        saveButton.setVisible(true);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(recordList.isSelectionEmpty())
                    return;
                int index=recordList.getSelectedIndex();
                System.out.println(index);

                RecordData recordData=triple.gamePanel.mainPanel.GetNowData();

                if(index==model.size()-1) {
                    triple.gameRecord.Insert(recordData);
                    recordData.id = triple.gameRecord.currentMaxId;
                    model.set(index, recordData);
                    model.addElement(new RecordData());
                    recordList.setModel(model);
                }else {
                    triple.gameRecord.Update(((RecordData)model.get(index)).id,recordData);
                    recordData.id=triple.gameRecord.currentMaxId;
                    model.set(index,recordData);
                }
                Btn.start();
            }
        });
        add(saveButton);

        loadButton=new JButton("载入存档");
        loadButton.setSize(100,50);
        loadButton.setLocation(w/2+50,h-75);
        loadButton.setVisible(true);
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index=recordList.getSelectedIndex();
                System.out.println(index);

                if(index<model.size()-1) {
                    triple.panelSwitcher.show(triple.getContentPane(),"game");
                    triple.gamePanel.mainPanel.Continue();
                    triple.gamePanel.pausePanel.DeActivate();
                    father.remove(ptr);
                    for (Component c :
                            father.getComponents()) {
                        c.setEnabled(true);
                    }
                    father.revalidate();
                    triple.gamePanel.mainPanel.EndGame();
                    triple.gamePanel.mainPanel.LoadRecordData((RecordData)model.getElementAt(index));
                    Wait.stop();
                }
                Btn.start();
            }
        });
        add(loadButton);
        setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
    }

}
