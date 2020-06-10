package triple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static triple.Triple.*;

class MainPanel extends JPanel {
    static final int N = 8, W = 600, H = 600;
    static final int width = W / N, height = H / N;
    static final int animateTime = 250;
    static final int frameRate = 40;
    static final int timeVal = 1000 / frameRate;
    static final int frameTot = (int) ((double) animateTime / 1000. * frameRate);
    static final int timeSlowSecs = 20;
    static final int multipleSecs = 20;
    static final int totStages=10;
    static ImageIcon[] lib = new ImageIcon[4];
    final int[] GOAL = {500, 1000, 2000, 3500, 5000,7000,9000,12000,15000,20000};
    final int[] LIMIT = {500, 600, 700, 800, 900,1000,1000,1000,1000,1000};
    Triple triple;
    double currentTime;
    double currentScore;
    double limitTime, goalScore;
    int timeSlowLastTime;
    int multipleLastTime;
    int transLastTimes;
    double timeSlowScale;
    double multipleScale;
    ClearList clearList;
    int stage;
    boolean[][] empty = new boolean[N][N];
    JLabel[] label = new JLabel[N * N];
    String[] text = new String[N * N];
    ImageIcon[] pic = new ImageIcon[N * N];
    int[][] id = new int[N][N];
    int[] X = new int[N * N], Y = new int[N * N];
    ConcurrentLinkedQueue<EventList<Element>> animationQueue = new ConcurrentLinkedQueue<>();
    AtomicInteger processing = new AtomicInteger(0);
    AtomicInteger animatingCnt = new AtomicInteger(0);
    static AtomicInteger pauseMark= new AtomicInteger(0);
    Random randomInt = new Random();
    Timer animater = new Timer();
    Timer timer = new Timer();



    StatePanel statePanel = new StatePanel();
    TextPanel textPanel;
    JButton nextButton = new JButton("下一关！");
    JButton showButton = new JButton("查看奖励");

    boolean clicked = false;
    MouseEvent last;
    Picture picture;
    int Pid = -1;

    Thread imageProcess;
    AtomicInteger waiting=new AtomicInteger(0);
    MainPanel ptr;

    MainPanel(Triple triple) {
        super();
        ptr=this;
        this.triple = triple;
        for (int i = 0; i < 4; i++)
            Skl[i] = new SkillMusic(i);

        for (int i = 0; i < N * N; i++)
            label[i] = new JLabel(lib[1]);
        for (int i = 0; i < N * N; i++) {
            add(label[i]);
            label[i].setSize(width, height);
            label[i].setLocation(i % N * width, i / N * height);
            label[i].addMouseListener(new MouseListener() {
                int pid = -1;

                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pid = -1;
                    if (pauseMark.get()==1 || triple.gameing.get() == 0 || processing.get() == 1 || animatingCnt.get() > 0) {
                        System.out.println(triple.gameing + " " + processing + " " + animatingCnt);
                        return;
                    }
                    if (animatingCnt.get() < 0)
                        animatingCnt.set(0);
                    for (int i = 0; i < N * N; i++)
                        if (e.getSource() == label[i]) {
                            pid = i;
                            Lab.start();
                            break;
                        }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (pauseMark.get()==1|| processing.getAndIncrement() == 1 || animatingCnt.get() > 0 || pid == -1 || isValidMove(pid,e) == 0) {
                        System.out.println(processing + " " + animatingCnt + " " + pid);
                        processing.decrementAndGet();
                        if(!clicked) {
                            Pid = -1;
                            if (pauseMark.get()==1 || triple.gameing.get() == 0 || processing.get() == 1 || animatingCnt.get() > 0) {
                                System.out.println(triple.gameing + " " + processing + " " + animatingCnt);
                                return;
                            }
                            if (animatingCnt.get() < 0)
                                animatingCnt.set(0);
                            for (int i = 0; i < N * N; i++)
                                if (e.getSource() == label[i]) {
                                    Pid = i;
                                    last = e;
                                    clicked = true;
                                    break;
                                }
                        }
                        else {
                            clicked = false;
                            if (pauseMark.get()==1|| processing.getAndIncrement() == 1 || animatingCnt.get() > 0 || Pid == -1 || isValidMove(e, last) == 0) {
                                System.out.println(processing + " " + animatingCnt + " " + pid);
                                processing.decrementAndGet();
                                return;
                            }
                            if (isValidMove(e, last) == -1) {
                                if (transLastTimes > 0) {
                                    transLastTimes--;
                                    statePanel.UpdateTrans(transLastTimes);
                                } else {
                                    processing.decrementAndGet();
                                    return;
                                }
                            }
                            System.out.println("begin");
                            int rid = Swap(Pid, e, last);
                            if (CheckState()) {
                                GenandFallDown(true);
                                while (CheckState())
                                    GenandFallDown(true);
                                animationQueue.add(new CheckList<>());
                            } else {
                                Swap(rid, e, last);
                            }
                            processing.decrementAndGet();
                        }

                        return;
                    }
                    if (isValidMove(pid,e) == -1) {
                        if (transLastTimes > 0) {
                            transLastTimes--;
                            statePanel.UpdateTrans(transLastTimes);
                        } else {
                            processing.decrementAndGet();
                            return;
                        }
                    }
                    System.out.println("begin");
                    int rid = Swap(pid, e);
                    if (CheckState()) {
                        GenandFallDown(true);
                        while (CheckState())
                            GenandFallDown(true);
                        animationQueue.add(new CheckList<>());
                    } else {
                        Swap(rid, e);
                    }
                    processing.decrementAndGet();
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

        }
        try {
            lib[0] = Scaled(new ImageIcon("pic/kiana.png"));
            lib[1] = Scaled(new ImageIcon("pic/mei2.png"));
            lib[2] = Scaled(new ImageIcon("pic/bronya.png"));
            lib[3] = Scaled(new ImageIcon("pic/seele.png"));
            /*
            lib[0] = Scaled(new ImageIcon(Triple.class.getResource("/pic/kiana.jpeg")));
            lib[1] = Scaled(new ImageIcon(Triple.class.getResource("/pic/kiana.jpeg")));
            lib[2] = Scaled(new ImageIcon(Triple.class.getResource("/pic/bronya.jpeg")));
            lib[3] = Scaled(new ImageIcon(Triple.class.getResource("/pic/seele.jpeg")));
*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*statePanel.add(startButton);*/
        nextButton.setSize(100, 50);
        nextButton.setLocation(W + 100, 500);
        nextButton.setVisible(false);
        nextButton.setEnabled(false);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stage++;
                Initialize();
                statePanel.repaint();
                pauseMark.decrementAndGet();
                nextButton.setVisible(false);
                nextButton.setEnabled(false);
                showButton.setVisible(false);
                showButton.setEnabled(false);
                if(triple.imagePrepared.get()>0)
                    triple.imagePrepared.decrementAndGet();
                DownloadPicture();
                Btn.start();
                Btn.start();
            }
        });
        add(nextButton);

        showButton.setSize(100, 50);
        showButton.setLocation(W + 210, 500);
        showButton.setVisible(false);
        showButton.setEnabled(false);
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ptr.setEnabled(false);
                for (Component c :
                        ptr.getComponents()) {
                    c.setEnabled(false);
                }
                ShowImagePanel showImagePanel=new ShowImagePanel(picture,ImageCrawler.imageBase,triple,triple.gamePanel);
                showImagePanel.setVisible(true);
                showImagePanel.setLocation(0,0);
                showImagePanel.setOpaque(false);
                triple.gamePanel.add(showImagePanel,0);
                triple.gamePanel.setEnabled(false);
                triple.gamePanel.pauseButton.setEnabled(false);
                triple.gamePanel.revalidate();
                showButton.setVisible(false);
                Btn.start();
            }
        });
        add(showButton);

        statePanel.setSize(380, 150);
        statePanel.setLocation(W+10, 130);
        statePanel.setOpaque(false);
        statePanel.setVisible(true);
        add(statePanel);

        textPanel=new TextPanel(380,150,"点下面按钮开始三消！\n");
        textPanel.setLocation(W+10, 300);
        add(textPanel,1);

    }

    static ImageIcon Scaled(ImageIcon origin) {
        Image img = origin.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon image2 = new ImageIcon(img);
        return image2;
    }

    int idx(int x, int y) {
        return id[x][y];
    }

    void Begin() {
        stage = 1;
        nextButton.setEnabled(false);
        nextButton.setVisible(false);
        triple.startTime = LocalDateTime.now();
        System.out.println(triple.startTime);
        Initialize();
        Game.loop();
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(pauseMark.get()==0)
                    addCurrentTime();
            }
        }, 0, 1000);
        if (triple.gameing.get() == 0)
            triple.gameing.incrementAndGet();
        textPanel.AppendText("----------------Stage " + stage + "---------------\n");
        DownloadPicture();
    }

    void Initialize() {
        currentTime = 0;
        currentScore = 0;
        limitTime = LIMIT[stage - 1];
        goalScore = GOAL[stage - 1];
        timeSlowScale = 1.;
        timeSlowLastTime = 0;
        multipleScale = 1.;
        multipleLastTime = 0;
        transLastTimes = 0;
        clearList = null;
        statePanel.Initialize();
        GenInitialState();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRoundRect(0, 0, 600, 600, 10, 10);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }

    public void paint(Graphics g) {
        super.paint(g);
        Color currentColor;
        /*g.drawRect(700,100,100,20);*/
        int end = (int) (256. * (1. - currentTime / limitTime));
        g.setColor(Color.black);
        g.drawString("Stage" + " " + stage + "/5", W + 10, 20);
        g.drawString("Time:", W + 10, 20 + 40 - 5);
        for (int i = 0; i < Math.min(256, end); i++) {
            currentColor = new Color(255, i, 0);
            g.setColor(currentColor);
            g.drawLine(W + 50 + i, 20 + 20, W + 50 + i, 20 + 40);
        }
        if (triple.gameing.get() > 0) {
            g.setColor(Color.black);
            g.drawRect(W + 50, 20 + 20, 256, 20);
        }
        if (clearList != null) {
            for (Object o : clearList) {
                Skill sk = (Skill) o;
                sk.drawString(this, g);
            }
/*
                for(int i=0;i<clearList.size();i++) {
                    Skill sk=(Skill)clearList.get(i);
                    g.drawString(Integer.toString(clearList.size()), 800, 300);
                    g.drawString(Integer.toString(sk.x)+" "+Integer.toString(sk.y), 800, 300+(i+1)*10);
                }
*/
        }
        g.setColor(Color.black);
        g.drawString("Score:", W + 10, 20 + 80 - 5);
        int end2 = (int) (256. * currentScore / goalScore);
        for (int i = 0; i < Math.min(256, end2); i++) {
            currentColor = new Color(255 - i, 255, 0);
            g.setColor(currentColor);
            g.drawLine(W + 50 + i, 20 + 60, W + 50 + i, 20 + 80);

        }
        if (triple.gameing.get() > 0) {
            g.setColor(Color.black);
            g.drawRect(W + 50, 20 + 60, 256, 20);
            g.drawString(Integer.toString((int) (currentScore + 0.001)) + "/" + Integer.toString((int) (goalScore + 0.001)), W + 50 + 260, 20 + 80 - 5);
        }
    }

    public void addCurrentTime() {
        currentTime += timeSlowLastTime > 0 ? timeSlowScale : 1.;
        if (--timeSlowLastTime == 0)
            timeSlowScale = 1.;
        if (--multipleLastTime == 0)
            multipleScale = 1.;
        this.repaint();
        if (currentTime > limitTime)
            timer.cancel();
    }

    public void addScore(ClearList clearList) {
        this.clearList = clearList;
        double totScore = 0.;
        for (Object o : clearList) {
            Skill sk = (Skill) o;
            sk.handleSkill(this);
        }
        for (Object o : clearList) {
            Skill sk = (Skill) o;
            totScore += (double) sk.cnt * 10. * multipleScale;
        }
        currentScore += totScore;
        this.repaint();
    }

    public void Clear() {
        this.clearList = null;
        this.repaint();
    }

    public void CheckStage() {
        if (currentScore > goalScore-0.001) {
            if (stage+1 <= totStages) {
                pauseMark.incrementAndGet();
                nextButton.setEnabled(true);
                nextButton.setVisible(true);
                if(triple.imagePrepared.get()==1)
                    showButton.setEnabled(true);
                else {
                    showButton.setText("下载中");
                    waiting.incrementAndGet();
                }
                showButton.setVisible(true);
                textPanel.AppendText("Stage " + (stage) + " Clear!\n");
            } else {
                stage--;
                textPanel.AppendText("通关了！就这？\n");
                triple.gameing.decrementAndGet();
            }
        } else if (currentTime > limitTime) {
            textPanel.AppendText("时间到！就这？\n");
            triple.gameing.decrementAndGet();
        }
    }

    int Swap(int pid, MouseEvent e) {

        int px = X[pid];
        int py = Y[pid];
        int dx = Math.floorDiv(e.getX(), width);
        int dy = Math.floorDiv(e.getY(), height);
        int rx = px + dx, ry = py + dy;
        int rid = id[rx][ry];

        MoveList<Element> moveList = new MoveList<>(pic);
        moveList.add(new LabelMove(pid, px * width, py * width, rx * width, ry * width));
        moveList.add(new LabelMove(rid, rx * width, ry * width, px * width, py * width));
        animatingCnt.incrementAndGet();
        animationQueue.add(moveList);

        X[pid] = rx;
        Y[pid] = ry;
        id[px][py] = rid;
        X[rid] = px;
        Y[rid] = py;
        id[rx][ry] = pid;

        return rid;

    }

    int Swap(int pid, MouseEvent e, MouseEvent las) {
        int px = X[pid];
        int py = Y[pid];

        int dx = Math.floorDiv(((JLabel)e.getSource()).getX() - ((JLabel)las.getSource()).getX(), width);
        int dy = Math.floorDiv(((JLabel)e.getSource()).getY() - ((JLabel)las.getSource()).getY(), height);
        int rx = px + dx, ry = py + dy;
        int rid = id[rx][ry];


        MoveList<Element> moveList = new MoveList<>(pic);
        moveList.add(new LabelMove(pid, px * width, py * width, rx * width, ry * width));
        moveList.add(new LabelMove(rid, rx * width, ry * width, px * width, py * width));
        animatingCnt.incrementAndGet();
        animationQueue.add(moveList);
        X[pid] = rx;
        Y[pid] = ry;
        id[px][py] = rid;
        X[rid] = px;
        Y[rid] = py;
        id[rx][ry] = pid;

        return rid;

    }

    boolean CheckState() {
/*
        for (int j = 0; j < N; j++) {
            String s = new String();
            for (int i = 0; i < N; i++)
                for (int k = 0; k < 4; k++)
                    if (pic[idx(i, j)] == lib[k])
                        s = s + " " + k;
            System.out.println(s);
        }
*/
        /*System.out.println("");*/
        int count = 0;
        clearList = new ClearList<>();
        for (int i = 0; i < N; i++)
            for (int j = 0, k = 0; j <= N - 3; j = k + 1, k++) {
                if (empty[i][j])
                    continue;
                while (k < N - 1 && pic[idx(i, j)] == pic[idx(i, k + 1)])
                    k++;
                int cnt = 0;
                if (k - j + 1 >= 3) {
                    cnt = k - j + 1;
                    for (int p = j; p <= k; p++) {
                        empty[i][p] = true;
                        int l = i, r = i;
                        while (l > 0 && pic[idx(l - 1, p)] == pic[idx(i, p)]) l--;
                        while (r < N - 1 && pic[idx(r + 1, p)] == pic[idx(i, p)]) r++;
                        if (r - l + 1 >= 3) {
                            cnt += r - l;
                            for (int q = l; q <= r; q++)
                                empty[q][p] = true;
                        }
                    }
                    count += cnt;

                    if (cnt >= 3) {
                        if (pic[idx(i, j)] == lib[0]) {
                            clearList.add(new TimeSlow(i, j, cnt));
                        } else if (pic[idx(i, j)] == lib[1]) {
                            clearList.add(new Multiple(i, j, cnt));
                        } else if (pic[idx(i, j)] == lib[2]) {
                            clearList.add(new Bomb(i, j, cnt));
                        } else if (pic[idx(i, j)] == lib[3]) {
                            clearList.add(new Trans(i, j, cnt));
                        } else {
                            System.out.println(i + " " + j + " " + "motherfucker");
                        }
                    }
                }
            }
        for (int j = 0; j < N; j++)
            for (int i = 0, k = 0; i <= N - 3; i = k + 1, k++) {
                if (empty[i][j])
                    continue;
                while (k < N - 1 && pic[idx(i, j)] == pic[idx(k + 1, j)])
                    k++;
                int cnt = 0;
                if (k - i + 1 >= 3) {
                    cnt = k - i + 1;
                    for (int p = i; p <= k; p++)
                        empty[p][j] = true;
                    count += cnt;

                    if (cnt >= 3) {
                        if (pic[idx(i, j)] == lib[0]) {
                            clearList.add(new TimeSlow(i, j, cnt));
                        } else if (pic[idx(i, j)] == lib[1]) {
                            clearList.add(new Multiple(i, j, cnt));
                        } else if (pic[idx(i, j)] == lib[2]) {
                            clearList.add(new Bomb(i, j, cnt));
                        } else if (pic[idx(i, j)] == lib[3]) {
                            clearList.add(new Trans(i, j, cnt));
                        } else {

                            System.out.println(i + " " + j + " " + "motherfucker");
                        }
                    }
                }
            }
        /*System.out.println(count);*/
        if (count > 0) {
            clearList.copyCurrentStates(pic, empty, id);
        }
        return count > 0;
    }

    void GenandFallDown(boolean show) {
/*
        for (int j = 0; j < N; j++) {
            String s = new String();
            for (int i = 0; i < N; i++)
                if (empty[i][j])
                    s = s + " 0";
                else
                    s = s + " 1";
            System.out.println(s);
        }
*/
        ArrayList<Integer> vanishList = new ArrayList<>();
        int cnt = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (empty[i][j]) {
                    vanishList.add(idx(i, j));
                    cnt++;
                }
            }
        }
        if (cnt > 0) {
            MoveList<Element> moveList = new MoveList<>();
            int cur = 0;
            for (int i = 0; i < N; i++) {
                for (int j = N - 1; j >= 0; j--) {
                    if (!empty[i][j]) {
                        int c = 0;
                        for (int k = j; k < N; k++)
                            if (!empty[i][k])
                                c++;
                        /*System.out.println("(" + i + " " + j + " " + (N - c) + ")");*/
                        X[idx(i, j)] = i;
                        Y[idx(i, j)] = N - c;
                        id[i][N - c] = idx(i, j);
                        if (show) {
                            moveList.add(new LabelMove(idx(i, j), i * width, j * height, i * width, (N - c) * height));
                        }
                    }
                }
                int c = 0;
                for (int j = 0; j < N; j++)
                    if (empty[i][j]) {
                        c++;
                        empty[i][j] = false;
                    }
                for (int k = 1; k <= c; k++) {
                    int nid = vanishList.get(cur++);
                    X[nid] = i;
                    Y[nid] = c - k;
                    id[i][c - k] = nid;
                    pic[nid] = lib[randomInt.nextInt(4)];
                    if (show)
                        moveList.add(new LabelMove(nid, i * width, -k * height, i * width, (c - k) * height));
                }
            }
            if (show) {
                animatingCnt.incrementAndGet();
                animationQueue.add(clearList);
                moveList.copyCurrentPics(pic);
                animatingCnt.incrementAndGet();
                animationQueue.add(moveList);
                /*Idle();*/
            }
        }
    }

    int isValidMove(int pid,MouseEvent e) {
        int px = X[pid];
        int py = Y[pid];
        int dx = Math.floorDiv(e.getX(), width);
        int dy = Math.floorDiv(e.getY(), height);
        int sum = Math.abs(dx) + Math.abs(dy);
        int rx = px + dx, ry = py + dy;
        if (sum == 0||rx<0||rx>=N||ry<0||ry>=N)
            return 0;
        else if (sum == 1)
            return 1;
        else return -1;
    }

    int isValidMove(MouseEvent e, MouseEvent las) {
        int dx = Math.floorDiv(((JLabel)e.getSource()).getX() - ((JLabel)las.getSource()).getX(), width);
        int dy = Math.floorDiv(((JLabel)e.getSource()).getY() - ((JLabel)las.getSource()).getY(), height);
        System.out.printf("%d %d\n", dx, dy);
        int sum = Math.abs(dx) + Math.abs(dy);
        if (sum == 0)
            return 0;
        else if (sum == 1)
            return 1;
        else return -1;
    }

    public void GenInitialState() {
        animater.cancel();
        animater.purge();
        animater = new Timer();
        animatingCnt.set(0);
        animationQueue.clear();
        animater.schedule(new TimerTask() {
            EventList<Element> eventList = null;
            int count = 0;

            @Override
            public void run() {
                /*System.out.println("animator# "+myid+": "+(++count));*/
                if (eventList == null && (!animationQueue.isEmpty())) {
                    eventList = animationQueue.poll();
                }
                if (eventList != null && pauseMark.get()==0) {
                    if (eventList.handleEvent()) {
                        eventList = null;
                        animatingCnt.decrementAndGet();
                    }
                }
            }
        }, 0, timeVal);
        for (int i = 0; i < N * N; i++) {
            label[i].setVisible(false);
            X[i] = i % N;
            Y[i] = i / N;
            id[i % N][i / N] = i;
            empty[i % N][i / N] = false;
            label[i].setSize(width, height);
            label[i].setLocation(i % N * width, i / N * height);
            pic[i] = lib[randomInt.nextInt(4)];
            label[i].setIcon(pic[i]);
        }
        while (CheckState())
            GenandFallDown(false);
        for (int i = 0; i < N * N; i++) {
            label[i].setIcon(pic[i]);
            label[i].setLocation(X[i] * width, Y[i] * height);
            label[i].setVisible(true);
        }

    }

    abstract class Element {
        abstract void handleElement(ArrayList list);
    }

    class LabelMove extends Element {
        int orix, oriy, destx, desty, pid;

        LabelMove(int pid, int orix, int oriy, int destx, int desty) {
            this.pid = pid;
            this.orix = orix;
            this.oriy = oriy;
            this.destx = destx;
            this.desty = desty;
        }

        void handleElement(ArrayList list) {
            MoveList moveList = (MoveList) list;
            label[pid].setIcon(moveList.pic[pid]);
            label[pid].setLocation(orix + (int) ((double) (destx - orix) * moveList.count / frameTot),
                    oriy + (int) ((double) (desty - oriy) * moveList.count / frameTot));
            label[pid].setVisible(true);
        }
    }

    class Skill extends Element {
        int x, y, cnt;

        Skill(int x, int y, int cnt) {
            this.x = x;
            this.y = y;
            this.cnt = cnt;
        }

        void handleElement(ArrayList list) {
        }

        void handleSkill(MainPanel p) {
        }

        void drawString(MainPanel p, Graphics g) {
        }
    }

    class TimeSlow extends Skill {
        double scale;

        TimeSlow(int x, int y, int count) {
            super(x, y, count);
            this.scale = scale;
            if (count <= 3)
                scale = 1.;
            else if (count == 4)
                scale = 0.8;
            else if (count == 5)
                scale = 0.5;
            else
                scale = 0.5 - (double) Math.min(5, count - 5) / 10.;
        }

        void handleSkill(MainPanel p) {
            if (cnt > 3) {
                p.timeSlowLastTime = timeSlowSecs;
                p.timeSlowScale = Math.min(p.timeSlowScale, scale);
                statePanel.UpdateTimeSlow(p.timeSlowScale);
                Skl[0].start();
                textPanel.AppendText("时空断裂！时间减速" + (int) ((1 - p.timeSlowScale + 0.001) * 100.) + "%\n");
            }
        }

        @Override
        void drawString(MainPanel p, Graphics g) {
            g.setColor(new Color(179, 0, 255));
            g.drawString("+" + (cnt * 10), x * width + width / 2, y * height + height / 2);
            if (cnt > 3) {
                g.drawString("时空断裂！时间减速" + (int) ((1 - p.timeSlowScale + 0.001) * 100.) + "%", x * width + width / 2 + 50, y * height + height / 2);
            }
        }
    }

    class Multiple extends Skill {
        double scale;

        Multiple(int x, int y, int count) {
            super(x, y, count);
            if (count <= 3)
                scale = 1.;
            else if (count == 4)
                scale = 1.2;
            else if (count == 5)
                scale = 1.5;
            else
                scale = 1.5 + (double) Math.min(5, count - 5) / 10.;
        }

        public void handleSkill(MainPanel p) {
            if (cnt > 3) {
                p.multipleLastTime = multipleSecs;
                p.multipleScale = Math.max(p.multipleScale, scale);
                statePanel.UpdateMultiple(p.multipleScale);
                Skl[1].start();
                textPanel.AppendText("影分身！得分x" + p.multipleScale + "\n");
            }
        }

        @Override
        void drawString(MainPanel p, Graphics g) {
            g.setColor(new Color(0, 179, 255));
            g.drawString("+" + (cnt * 10), x * width + width / 2, y * height + height / 2);
            if (cnt > 3) {
                g.drawString("影分身！得分x" + p.multipleScale, x * width + width / 2 + 50, y * height + height / 2);
            }
        }
    }

    class Bomb extends Skill {
        double score;

        Bomb(int x, int y, int count) {
            super(x, y, count);
            if (count <= 3)
                score = 0.;
            else if (count == 4)
                score = 50.;
            else if (count == 5)
                score = 100.;
            else
                score = 100. + Math.min(5, count - 5) * 20.;
        }

        public void handleSkill(MainPanel p) {
            p.currentScore += score;
            if (cnt > 3) {
                Skl[2].start();
                textPanel.AppendText("一拳超鸭！得分+" + (int) (score + 0.001) + "\n");
            }
        }

        @Override
        void drawString(MainPanel p, Graphics g) {
            g.setColor(new Color(255, 255, 0));
            g.drawString("+" + (cnt * 10), x * width + width / 2, y * height + height / 2);
            if (cnt > 3) {
                g.drawString("一拳超鸭！得分+" + (int) (score + 0.001), x * width + width / 2 + 50, y * height + height / 2);
            }
        }
    }

    class Trans extends Skill {
        int times;

        Trans(int x, int y, int count) {
            super(x, y, count);
            if (count <= 4)
                times = 0;
            else if (count == 5)
                times = 1;
            else
                times = 1 + Math.min(5, count - 5) / 2;
        }

        public void handleSkill(MainPanel p) {
            p.transLastTimes += times;
            statePanel.UpdateTrans(p.transLastTimes);
            if (cnt > 4) {
                Skl[3].start();
                textPanel.AppendText("量子之海！可随意拖动次数+" + times + "\n");
            }
        }

        @Override
        void drawString(MainPanel p, Graphics g) {
            g.setColor(new Color(255, 255, 255));
            g.drawString("+" + (cnt * 10), x * width + width / 2, y * height + height / 2);
            if (cnt > 4) {
                g.drawString("量子之海！可随意拖动次数+" + times, x * width + width / 2 + 50, y * height + height / 2);
            }
        }
    }

    abstract class EventList<E> extends ArrayList<E> {
        abstract boolean handleEvent();
    }

    class MoveList<E> extends EventList<E> {
        ImageIcon[] pic = new ImageIcon[N * N];
        int count;

        MoveList() {
            count = 0;
        }

        MoveList(ImageIcon[] pic) {
            count = 0;
            for (int i = 0; i < N * N; i++)
                this.pic[i] = pic[i];
        }

        void copyCurrentPics(ImageIcon[] pic) {
            for (int i = 0; i < N * N; i++)
                this.pic[i] = pic[i];
        }

        boolean handleEvent() {
            count++;
            for (E o : this) {
                Element l = (Element) o;
                l.handleElement(this);
            }
            if (count == frameTot) {
                count = 0;
                return true;
            } else
                return false;
        }
    }

    class ClearList<E> extends EventList<E> {
        int count;
        ImageIcon[] pic = new ImageIcon[N * N];
        boolean[][] empty = new boolean[N][N];
        int[][] id = new int[N][N];

        ClearList() {
            count = 0;
        }

        void copyCurrentStates(ImageIcon[] pic, boolean[][] empty, int[][] id) {
            for (int i = 0; i < N * N; i++)
                this.pic[i] = pic[i];
            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++)
                    this.empty[i][j] = empty[i][j];
            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++)
                    this.id[i][j] = id[i][j];
/*
            System.out.println("copying");
            for (int j = 0; j < N; j++) {
                String s = new String();
                for (int i = 0; i < N; i++)
                    if (empty[i][j])
                        s = s + " 0";
                    else
                        s = s + " 1";
                System.out.println(s);
            }
*/

        }

        boolean handleEvent() {
            if(count == 1)
                Tri.start();
            count++;
            if (count <= frameTot) {
                for (int i = 0; i < N; i++)
                    for (int j = 0; j < N; j++)
                        if (empty[i][j]) {
                            if (((count - 1) / 4) % 2 == 0)
                                label[id[i][j]].setVisible(false);
                            else
                                label[id[i][j]].setVisible(true);
                        }
            } else if (count == frameTot + 1) {
                addScore(this);
            }
            if (count == frameTot * 2) {
                Clear();
                count = 0;
                return true;
            } else
                return false;
        }
    }

    class CheckList<E> extends EventList<E> {
        int count;

        boolean handleEvent() {
            count++;
            if (count == 5) {
                CheckStage();
                return true;
            } else
                return false;
        }
    }

    void Pause(){
        setEnabled(false);
        for (Component c:this.getComponents()
             ) {
            c.setEnabled(false);
        }
        triple.gamePanel.pauseButton.setEnabled(false);
        pauseMark.incrementAndGet();
    }

    void Continue(){
        setEnabled(true);
        for (Component c:this.getComponents()
                ) {
            c.setEnabled(true);
        }
        triple.gamePanel.pauseButton.setEnabled(true);
        if(pauseMark.get()>0)
            pauseMark.decrementAndGet();
    }

    void Print(){
        for (Component c:this.getComponents()
                ) {
            System.out.println(c.toString());
        }
    }

    public RecordData GetNowData(){
        RecordData recordData=new RecordData();
        recordData.name=triple.name;
        recordData.dateTime=LocalDateTime.now();
        recordData.score=(int)currentScore;
        recordData.stage=stage;
        recordData.situation=this.toString();
        return recordData;
    }

    public String toString(){
        StringBuffer strbuf=new StringBuffer("");
        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                for(int k=0;k<4;k++)
                    if(pic[idx(i,j)]==lib[k])
                        strbuf.append(k);
        strbuf.append(" ");
/*
        currentTime = 0;
        currentScore = 0;
        limitTime = LIMIT[stage - 1];
        goalScore = GOAL[stage - 1];
        timeSlowScale = 1.;
        timeSlowLastTime = 0;
        multipleScale = 1.;
        multipleLastTime = 0;
        transLastTimes = 0;
*/

        strbuf.append(currentTime);
        strbuf.append(" ");
        strbuf.append(currentScore);
        strbuf.append(" ");
        strbuf.append(timeSlowScale);
        strbuf.append(" ");
        strbuf.append(timeSlowLastTime);
        strbuf.append(" ");
        strbuf.append(multipleScale);
        strbuf.append(" ");
        strbuf.append(multipleLastTime);
        strbuf.append(" ");
        strbuf.append(transLastTimes);
        strbuf.append(" ");
        return strbuf.toString();
    }

    public void LoadRecordData(RecordData recordData){

        nextButton.setEnabled(false);
        nextButton.setVisible(false);
        showButton.setEnabled(false);
        showButton.setVisible(false);

        if (triple.gameing.get() == 0) {
            triple.gameing.incrementAndGet();
            for (int i = 0; i < N * N; i++) {
                X[i] = i % N;
                Y[i] = i / N;
                id[i % N][i / N] = i;
                empty[i % N][i / N] = false;
                label[i].setSize(width, height);
                label[i].setLocation(i % N * width, i / N * height);
                label[i].setVisible(true);
            }
        }
        currentScore=recordData.score;
        stage=recordData.stage;
        String[] datas=recordData.situation.split(" ");

        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                pic[idx(i, j)] = lib[(int) (datas[0].charAt(i * N + j)) - (int) '0'];

        for(int i=0;i<N*N;i++)
            label[i].setIcon(pic[i]);

        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                empty[i][j]=false;

        currentTime=Double.parseDouble(datas[1]);
        currentScore=Double.parseDouble(datas[2]);
        limitTime=LIMIT[stage-1];
        goalScore=GOAL[stage-1];
        timeSlowScale=Double.parseDouble(datas[3]);
        timeSlowLastTime=Integer.parseInt(datas[4]);
        multipleScale=Double.parseDouble(datas[5]);
        multipleLastTime=Integer.parseInt(datas[6]);
        transLastTimes=Integer.parseInt(datas[7]);

        statePanel.Initialize();
        if(timeSlowScale>1.001)
            statePanel.UpdateTimeSlow(timeSlowScale);
        if(multipleScale>1.001)
            statePanel.UpdateMultiple(multipleScale);
        statePanel.UpdateTrans(transLastTimes);

        textPanel.AppendText("----------------Stage " + stage + "---------------\n");

        animater.cancel();
        animater.purge();
        animater = new Timer();
        animatingCnt.set(0);
        animationQueue.clear();
        animater.schedule(new TimerTask() {
            EventList<Element> eventList = null;

            @Override
            public void run() {
                if (eventList == null && (!animationQueue.isEmpty())) {
                    eventList = animationQueue.poll();
                }
                if (eventList != null && pauseMark.get()==0) {
                    if (eventList.handleEvent()) {
                        eventList = null;
                        animatingCnt.decrementAndGet();
                    }
                }
            }
        }, 0, timeVal);
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(pauseMark.get()==0)
                    addCurrentTime();
            }
        }, 0, 1000);

        DownloadPicture();
        CheckStage();
        Game.loop();

    }

    public void EndGame(){
        timer.cancel();
        animater.cancel();
        animatingCnt.set(0);
        animationQueue.clear();
        pauseMark.set(0);
        triple.gameing.set(0);
        statePanel.EndGame();
        Game.stop();
    }

    void DownloadPicture(){

        if(ImageCrawler.imageDownloading.get()==0
                &&triple.imagePrepared.get()==0){

            imageProcess=new Thread(()->{
                try {
                    picture = new ImageCrawler().work();
                    if(picture.image!=null)
                        triple.imagePrepared.incrementAndGet();
                    ptr.PictureReady();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            imageProcess.start();
        }
    }

    void PictureReady(){
        if(waiting.get()==1) {
            showButton.setText("查看奖励");
            showButton.setEnabled(true);
            waiting.decrementAndGet();
        }
    }

}

