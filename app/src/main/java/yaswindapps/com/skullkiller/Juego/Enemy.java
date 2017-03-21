package yaswindapps.com.skullkiller.Juego;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.concurrent.atomic.AtomicIntegerArray;

import yaswindapps.com.skullkiller.MainActivity;
import yaswindapps.com.skullkiller.Motor.GameView;

/**
 * Created by Juanca on 04/02/2016.
 */
public class Enemy {

    public static final int APPEARING = 0;
    public static final int WALKING = 1;
    public static final int ATTACKING = 2;
    public static final int DIYING = 3;
    public static final int HURT = 4;

    private static final int BMP_ROWS = 5;
    private static final int BMP_COLUMNS = 10;


    private int currentFrame;
    private int frames[];
    private static int frameWidth;
    public static int frameHeight;

    private int hp=GameView.enemyHP;

    private GameView gameView;
    private Bitmap bmp;

    private int attackCD;

    private int x;
    private int y;
    private int status;

    private boolean muerto=false;

    private int speed;



    public Enemy(GameView gameView, Bitmap bmp, int carril) {
        frameWidth = bmp.getWidth() / BMP_COLUMNS;
        frameHeight = bmp.getHeight() / BMP_ROWS;

        frames = new int[5];
        frames[APPEARING] = 9;
        frames[WALKING] = 10;
        frames[ATTACKING] = 7;
        frames[DIYING] = 6;
        frames[HURT] = 2;

        status = APPEARING;
        speed=(int)((Math.random()*GameView.maxEnemySpeed-1));
        currentFrame = 9;
        this.gameView = gameView;
        this.bmp = bmp;
        x = gameView.getWidth()-frameWidth;
        y = carril*frameHeight;

        attackCD=0;
    }



    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }


    public void setStatus(int status) {
        if(this.status!=status){
            this.status = status;
            currentFrame = 9;
        }

    }

    public void onDraw(Canvas canvas) {

        int srcX = currentFrame * frameWidth;
        int srcY = status * frameHeight;

        Rect src = new Rect(srcX, srcY, srcX + frameWidth, srcY + frameHeight);
        Rect dst = new Rect(x, y, x + frameWidth, y + frameHeight);

        canvas.drawBitmap(bmp, src, dst, null);

        update();

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private void update(){
        currentFrame--;

        if(attackCD--<0)
            attackCD=0;

        if(status==WALKING){
            x-=speed+1;
        }

        if(status==APPEARING && currentFrame==10-frames[APPEARING]){
            setStatus(WALKING);
        }

        if(status==ATTACKING && currentFrame==10-frames[ATTACKING]){
            setStatus(WALKING);
        }

        if(isDead() && !muerto) {
            die();
        }

        if(status==DIYING && currentFrame==10-frames[DIYING]){
            currentFrame=frames[DIYING];
            muerto=true;
        }

        if(status==HURT && currentFrame==10-frames[HURT]){
            setStatus(WALKING);
        }

        if(currentFrame==10-frames[status]) {
            currentFrame = 9;
        }

        if(status!=ATTACKING && attackCD<=0){
            if (shouldAttack()){
                attack();
            }
        }

        if(x<0-frameWidth){
            gameView.reachBase(this);
        }

    }

    private void attack() {
      if(status!=ATTACKING && status!=DIYING && !muerto){
          setStatus(ATTACKING);
          attackCD=25;
          int r = (int)(Math.random()*100);
          if(r<GameView.probEnemyHit){
              gameView.heroe.receiveDmg();
          }
      }
    }


    public boolean isMuerto() {
        return muerto;
    }

    private boolean shouldAttack(){
        Heroe t=gameView.heroe;

        Rect bounds1 = new Rect(x+35, y+50, x+((frameWidth/3)*2), y+frameHeight-35);
        Rect bounds2 = new Rect(t.getX()+55, t.getY(), t.getX()+t.getFrameWidth()/2+35, t.getY()+t.getFrameHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            MainActivity.ma.sword();
            return true;
        }
        return false;
    }

    

    private void die(){
        if(status!=DIYING && status!=HURT) {
            setStatus(DIYING);
        }
    }

    public boolean isDead(){
        if (hp<=0) return true;
        return false;
    }


    public void receiveDmg() {
        x += frameWidth/2;
        hp--;
        if(!muerto) {
            if (status != HURT && status!=DIYING) {
                setStatus(HURT);
            }

        }
    }
}
