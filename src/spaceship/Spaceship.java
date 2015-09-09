
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    Image rocketAnim;
    int rocketXPos;
    int rocketYPos;
    int rocketXSpeed;
    int rocketYSpeed;
    int rocketDir;
    int rocketHealth;
 
//variables for missiles
//    int currentCannonBallIndex;
//    int numCannonBalls = 10;
//    int cannonBallXPos[]=new int[numCannonBalls];
//    int cannonBallYPos[]=new int[numCannonBalls];
//    int cannonBallDir[]=new int[numCannonBalls];
//    boolean cannonBallVisible[]=new boolean[numCannonBalls];
//    boolean cannonBallFired[]=new boolean[numCannonBalls];
   boolean canFire;
   int cannonCharge;
   
   boolean forceField;
   int ffRadius;
   int ffTime;
   
    Missle missles[] = new Missle[Missle.numMissle];
    
//variables for star
    
    int numStar=1000000;
    int starXPos[]= new int [numStar];
    int starYPos[]= new int [numStar];
    int starDir;
    boolean rocketHit[]= new boolean[numStar];
    
//variables for score
    int score;
    int highScore;
    boolean gameOver;
    int scoreTime;
    boolean gameStart;
    
//game
    boolean gm1;//normal
    boolean gm2;

    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {if (!gameStart&&!gameOver)rocketYSpeed++;
                } else if (e.VK_DOWN == e.getKeyCode()) {if (!gameStart&&!gameOver)rocketYSpeed--;
                } else if (e.VK_LEFT == e.getKeyCode()) { if(gm1&&!gameStart&&!gameOver){starDir=2; if(rocketXSpeed>-20)rocketXSpeed--;}
                } else if (e.VK_RIGHT == e.getKeyCode()) {if (!gameStart&&!gameOver){starDir=1;if(rocketXSpeed<20)rocketXSpeed++;}
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    zsound = new sound("ouch.wav");                    
                }
                else if (e.VK_SPACE == e.getKeyCode() && canFire && gm1) {
                    missles[Missle.currentMissleIndex].visible = true;
                    missles[Missle.currentMissleIndex].xPos = rocketXPos;
                    missles[Missle.currentMissleIndex].yPos = rocketYPos;
                    Missle.currentMissleIndex++; 
                    if (Missle.currentMissleIndex >= Missle.numMissle)
                    Missle.currentMissleIndex = 0;
                      canFire=false;
                   
                }
                else if (e.VK_F == e.getKeyCode()) {forceField=true;
                }
                else if (e.VK_1 == e.getKeyCode()&& gameStart) {gm1=true; gameStart=false;
                }
                else if (e.VK_2 == e.getKeyCode() && gameStart) {gm2=true; gameStart=false; rocketXSpeed=1;
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        
        g.setColor(Color.red);
        g.fillRect(getWidth2()/6,getYNormal(-3),58*5,7);
        g.setColor(Color.green);
        g.fillRect(getWidth2()/6,getYNormal(-3),cannonCharge*5,7);
        
        if(forceField)
            drawForceField(getX(rocketXPos),getYNormal(rocketYPos),ffRadius);
        
        if(rocketDir==1 && rocketXSpeed==0){
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        }
        else if (rocketDir==2 && rocketXSpeed==0){
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
        }
        else if (rocketDir==1 && rocketXSpeed!=0){
        drawRocketAnim(rocketAnim,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        }
        else if (rocketDir==2 && rocketXSpeed!=0){
        drawRocketAnim(rocketAnim,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
        }
        for (int index=0;index<numStar;index++)
        {
            g.setColor(Color.yellow);
        drawCircle(getX(starXPos[index]),getYNormal(starYPos[index]),1.0,1,1);
        }
        //draw Cannonball
        for (int index=0;index<Missle.numMissle;index++)
        {
            if (missles[index].visible)
            {
                g.setColor(Color.red);
                drawCircle(getX(missles[index].xPos),getYNormal(missles[index].yPos),0,.5,.5);
            }
        }
        //top of screen info
        g.setColor(Color.black);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("Score: " + score, 10, 44);
        g.setColor(Color.black);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("Rocket Health: " + rocketHealth, 150, 44);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("HighScore: " + highScore, 310, 44);
        
        if (gameOver)
        {
            g.setColor(Color.white);
            g.setFont(new Font("Impact",Font.BOLD,60));
            g.drawString("GAME OVER", getWidth2()/6, getHeight2()/3);

        }
        if (gameStart)
        {
            g.setColor(Color.red);
          
            g.setColor(Color.white);
            g.setFont(new Font("Impact",Font.BOLD,60));
            g.drawString("Start Game", getWidth2()/6, getHeight2()/3);
            g.setFont(new Font("Impact",Font.BOLD,30));
            g.drawString("1 for Normal", getWidth2()/6, getHeight2()/2);
            
            g.drawString("2 for Runner", getWidth2()/6, (getHeight2()/4)*3);

        }
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    ///////////////////////////////////////////////////////////////////////////////
    public void drawForceField(int xpos,int ypos, int rad)
    {
        g.translate(xpos,ypos);

        g.fillOval(-rad,-rad,rad*2,rad*2);

        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocketAnim(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketAnim.getWidth(this);
        int height = rocketAnim.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
/////////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        
        
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        rocketXSpeed = 0;
        rocketYSpeed = 0;
        rocketDir=1;
        
        ffRadius=60;
        ffTime=0;
      
        
        for (int index=0;index<numStar;index++)
        {
            
        starXPos[index] =(int) (Math.random()*getWidth2());
        starYPos[index] =(int) (Math.random()*getHeight2());
        rocketHit[index]=false;
        while (rocketXPos-60 < starXPos[index] && 
            rocketXPos+60 > starXPos[index] &&
            rocketYPos-60 < starYPos[index] &&
            rocketYPos+60 > starYPos[index] )
                {
                    starXPos[index] =(int) (Math.random()*getWidth2());
                    starYPos[index] =(int) (Math.random()*getHeight2());
                }
        }
        starDir=1;
        score=0;
        scoreTime=0;
        gameOver=false;
        rocketHealth=3;
        forceField=false;
        
        gameStart=true;
        gm1=false;
        gm2=false;
        
        Missle.currentMissleIndex=0;
        canFire=true;
        cannonCharge=58;
        for (int index=0;index<missles.length;index++)
        {
            missles[index]= new Missle();
        }
        

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            rocketAnim = Toolkit.getDefaultToolkit().getImage("./animRocket.GIF");
            reset();
            readFile();
            bgSound = new sound("starwars.wav");
        }
        
        if(bgSound.donePlaying){
            bgSound = new sound("starwars.wav");
        }
    if(!gameOver && !gameStart) {  
    //star/rocket movement
        if(rocketXSpeed>=1){
        rocketDir=1;
        }
        else if(rocketXSpeed<=-1){
        rocketDir=2;
        }
        rocketYPos+=rocketYSpeed;
        
        for (int index=0;index<numStar;index++)
        {
            starXPos[index]-=rocketXSpeed;
            if(starXPos[index]<=0 &&rocketDir==1){
                starXPos[index]=getWidth2()+15;
                starYPos[index]=(int) (Math.random() * getHeight2());
            }
            if(starXPos[index]>=getWidth2() && rocketDir==2){
                starXPos[index]=-15;
                starYPos[index]=(int) (Math.random() * getHeight2());
            }
        }
/////////////////////////////////F O R C E   F I E L D////////////////////////////////////////
           
        if (forceField){
            
            ffTime++;
             if (ffTime>=20){
                forceField=false;
                ffTime=0;
            }       
        }
        if (forceField){
            
            int dist;
            int sqr;

            for (int index=0;index<numStar;index++)
            {
                dist= ((starXPos[index]-rocketXPos)*(starXPos[index]-rocketXPos))+((starYPos[index]-rocketYPos+ffRadius/4)*(starYPos[index]-rocketYPos+ffRadius/4));
                sqr=(starXPos[index]-rocketXPos)+(starYPos[index]-rocketYPos+ffRadius/4);
                if(sqr<0)
                    sqr*=-1;
                if(sqr!=0)
                    dist/=sqr;
                if(dist<ffRadius && forceField && sqr!=0){
                    if(rocketDir==1){
                       starXPos[index]=getWidth2()+15;
                       starYPos[index]=(int) (Math.random() * getHeight2());
                       score+=5;
                    }
                    if(rocketDir==2){
                       starXPos[index]=-15;
                       starYPos[index]=(int) (Math.random() * getHeight2());
                       score+=5;
                    }
                }
            }
        }
//////////////////////////////////////////////////////////////////////////////////////////////       

//score
        if (rocketHealth<=0)
            gameOver=true;
        if(rocketXSpeed>0)
        scoreTime++;
        else if (rocketXSpeed<0)
        scoreTime++;    
        if (scoreTime+rocketXSpeed>25){
            
            
              score++;
            
            scoreTime=0;
        }
        
        if (!canFire)
        {
            cannonCharge-=2;
            if(cannonCharge<=0)
            {
                canFire=true;
                cannonCharge=58;
            }
        }
        //detecting hits on spaceship
        
        for (int index=0;index<numStar;index++)
        {
            if (rocketXPos-15 < starXPos[index] && 
                rocketXPos+15 > starXPos[index] &&
                rocketYPos-15 < starYPos[index] &&
                rocketYPos+15 > starYPos[index] )
                {
                    if(!rocketHit[index]){
                    zsound=new sound ("ouch.wav");
                    rocketHealth--;
                    }
                    rocketHit[index]=true;
                }
            else
                rocketHit[index]=false;
            
        }
        //detecting hits cannon vs star
        for (int count=0;count<Missle.numMissle;count++)
        {
            for (int index=0;index<numStar;index++)
            {
                if (missles[count].visible)
                    {
                        if (starXPos[index]-20 < missles[count].xPos && 
                            starXPos[index]+20 > missles[count].xPos &&
                            starYPos[index]-20 < missles[count].yPos &&
                            starYPos[index]+20 > missles[count].yPos){
                            missles[count].visible=false;
                            if(rocketDir==1){
                                starXPos[index]=getWidth2()+15;
                                starYPos[index]=(int) (Math.random() * getHeight2());
                                if (rocketXSpeed!=0)
                                score+=5;
                            }
                            if(rocketDir==2){
                                starXPos[index]=-15;
                                starYPos[index]=(int) (Math.random() * getHeight2());
                                if (rocketXSpeed!=0)
                                score+=5;
                            }
                        }
                            
                    }
            } 
        }
        
        //cannon ball
        for (int count=0;count<Missle.numMissle;count++)
        {
            if (missles[count].visible)
            {
                if(rocketDir==1 && !missles[count].fired){
                    missles[count].dir=1;
                    missles[count].fired=true;
                    
                }
                if(rocketDir==2 && !missles[count].fired){
                    missles[count].dir=2;
                    missles[count].fired=true;
                    
                }
                if(missles[count].dir==1){
                    missles[count].xPos+=4;
                    if (missles[count].xPos >= getWidth2())
                    missles[count].visible = false;
                    
                }
                
                if(missles[count].dir==2){
                    missles[count].xPos-=4;
                    if (missles[count].xPos <= 0)
                        missles[count].visible = false;
                    
                }
            }
        }
        
        
        if(rocketYPos>=getHeight2()){
        rocketYSpeed=0;
        rocketYPos=getHeight2();
        }
        else if(rocketYPos<=0){
        rocketYSpeed=0;
        rocketYPos=0;
        }
        
    }
    if(score>highScore && gameOver==true)
                highScore=score;
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////   
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStar = Integer.parseInt(numStarsString.trim());
                }
                if (newLine.startsWith("highscore"))
                {
                    String highscoreString = newLine.substring(10);
                    highScore = Integer.parseInt(highscoreString.trim());
                }
                line = in.readLine();
                
            }
            in.close();
        } catch (IOException ioe) {
        }
    }
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}

class Missle
{
    public static int currentMissleIndex=0;
    public  final static int numMissle = 10;
    
    public int xPos;
    public int yPos;
    public int dir;
    public boolean visible;
    public boolean fired;

    
    Missle()
    {
        visible=false;
        fired=false;
    }
    
}