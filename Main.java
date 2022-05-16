import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


interface Commons
{
    int WIDTH  = 700;
    int HEIGHT = 600;
    int DELAY  = 8;
}


class BrickGenerator
{
    byte brick[][];
    int brickWidth;
    int brickHeight;
    Image brickImg;

    public BrickGenerator( int row, int col )
    {
        brickImg = new ImageIcon( "img\\brick.png" ).getImage();
        brick = new byte[ row ][ col ];

        for ( int i = 0; i < brick.length; i++ ) {
            for ( int j = 0; j < brick[ 0 ].length; j++ ) {
                brick[ i ][ j ] = 1;
            }
        }

        brickWidth  = 100;
        brickHeight = 30;
    }


    public void draw( Graphics g )
    {
        for ( int i = 0; i < brick.length; i++ ) {
            for ( int j = 0; j < brick[ 0 ].length; j++ ) {
                if ( brick[ i ][ j ] > 0 ) {
                    g.drawImage( brickImg, i * brickWidth + 90, j * brickHeight + 50, null );
                }
            }
        }
    }

    public void setExist( boolean exits, int ro, int co )
    {
        if ( !exits ) {
            brick[ ro ][ co ] = 0;
        }
    }
}


class GamePanel extends JPanel implements ActionListener, KeyListener
{

    private boolean isPlay = false;
    private boolean isWin  = false;
    private boolean isLose = false;

    private Timer timer;
    private int totalBricks = 25;

    private Image background;
    private Image paddle;
    private Image ball;

    private BrickGenerator bg;

    // Paddle's value
    private int paddleX = Commons.WIDTH / 2;
    private int paddleY = 530;
    private int paddleSpeedX = 20;
    private int paddleWidth  = 120;
    private int paddleHeight = 10;


    // Ball's value
    private int ballX = Commons.WIDTH / 2;
    private int ballY = 200;
    private int ballSpeedX = -3;
    private int ballSpeedY = -3;
    private int ballSize  = 15;

        
    public GamePanel()
    {

        bg = new BrickGenerator( 5, 5 );
        
        background = new ImageIcon( "img\\background.png" ).getImage();
        paddle = new ImageIcon( "img\\paddle.png" ).getImage();
        ball = new ImageIcon( "img\\ball.png" ).getImage();
        

        
        timer = new Timer( Commons.DELAY, this );
        setFocusable( true );
        setFocusTraversalKeysEnabled( false );
        addKeyListener( this );
        timer.start();
    }


    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );

        // Background
        g.drawImage( background, 0, 0, null );

        // Ball
        g.drawImage( ball, ballX, ballY, null );

        // Paddle
        g.drawImage( paddle, paddleX, paddleY, null );

        // Brick
        bg.draw( g );

        // GAME CLEAR
        if ( isWin ) {
            g.setColor( Color.GREEN );
            g.setFont( new Font( "serif", Font.PLAIN, 40 ) );
            g.drawString( "GAME CLEAR", 20, 60 );
        }
    
    
        // GAME OVER
        if ( isLose ) {
            g.setColor( Color.RED );
            g.setFont( new Font( "serif", Font.PLAIN, 40 ) );
            g.drawString( "GAME OVER", 20, 60 );            
        }
        
    }
    
    @Override public void actionPerformed( ActionEvent e )
    {
        timer.start();
        repaint();


        if ( isPlay ) {
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            if ( ballX <= 0 ) {
                ballSpeedX *= -1;
            }

            if ( ballY <= 0 ) {
                ballSpeedY *= -1;
            }

            if ( ballX >= Commons.WIDTH - ballSize - 10 ) {
                ballSpeedX *= -1;
            }

            if ( ballY >= Commons.HEIGHT - ballSize - 10 ) {
                timer.stop();
                isLose = true;
            }

            if ( totalBricks == 0 ) {
                timer.stop();
                isWin = true;
            }


            Rectangle ballRect = new Rectangle( ballX, ballY, ballSize, ballSize );
            Rectangle paddleRect = new Rectangle( paddleX, paddleY, paddleWidth, paddleHeight );

            if ( ballRect.intersects( paddleRect ) ) {
                ballSpeedY *= -1;
            }


            for ( int i = 0; i < bg.brick.length; i++ ) {
                for ( int j = 0; j < bg.brick[ 0 ].length; j++ ) {
                    if ( bg.brick[ i ][ j ] > 0 ) {
                        int width  = bg.brickWidth;
                        int height = bg.brickHeight;
                        int xpos   = i * width  + 90;
                        int ypos   = j * height + 50;

                        Rectangle brickRect = new Rectangle( xpos, ypos, width, height );

                        if ( ballRect.intersects( brickRect ) ) {
                            ballSpeedY *= -1;
                            totalBricks--;
                            bg.setExist( false, i, j );
                        }
                    }
                }
            }
        }
    }

    @Override public void keyPressed( KeyEvent e )
    {
        if ( e.getKeyCode() == KeyEvent.VK_LEFT ) {
            isPlay = true;
            paddleX -= paddleSpeedX;
        }

        if ( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
            isPlay = true;
            paddleX += paddleSpeedX;
        }
    }

    @Override public void keyTyped( KeyEvent e )
    {}

    @Override public void keyReleased( KeyEvent e )
    {}

}

class Main extends JFrame
{

    public Main()
    {
        add( new GamePanel() );
        setSize( Commons.WIDTH, Commons.HEIGHT );
        setLocationRelativeTo( null );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setVisible( true );
        setResizable( false );
    }
    
    public static void main( String[] args )
    {
        new Main();
    }
}
