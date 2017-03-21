package yaswindapps.com.skullkiller.Juego;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import yaswindapps.com.skullkiller.MainActivity;
import yaswindapps.com.skullkiller.Motor.GameView;

/**
 * Created by Juanca on 25/01/2016.
 */
public class Heroe {

    public static final int IDLE = 0;
    public static final int WALKING = 1;
    public static final int ATTACKING = 2;
    public static final int DIYING = 3;
    public static final int HURT = 4;

    private static final int BMP_ROWS = 5;
    private static final int BMP_COLUMNS = 9;

    private int frames[];
    private static int frameWidth;
    private static int frameHeight;
    private int currentFrame;

    private GameView gameView;
    private Bitmap bmp;

    private boolean muerto;

    private int x;
    private int y;
    private int status;
    private int hp;
    private int attackCD;
    private int walkingCD;

    private int xSpeed;
    private int ySpeed;
    private boolean attacking=false;

    public Heroe(GameView gameView, Bitmap bmp, int carril) {
        frameWidth = bmp.getWidth() / BMP_COLUMNS;
        frameHeight = bmp.getHeight() / BMP_ROWS;

        muerto=false;
        walkingCD=15;

        frames = new int[5];
        frames[IDLE] = 3;
        frames[WALKING] = 8;
        frames[ATTACKING] = 6;
        frames[DIYING] = 9;
        frames[HURT] = 2;

        attackCD=4;
        status = IDLE;
        currentFrame = 0;

        this.gameView = gameView;
        this.bmp = bmp;
        x = 0;
        y = frameHeight*carril;

        hp=GameView.heroeHP; //Cargar from preferencias
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void setStatus(int status) {
        if(status==WALKING && this.status!=HURT && this.status!=DIYING && this.status!=ATTACKING){
            if(this.status!=status)
                this.status=status;
        }

        if(status==IDLE && this.status!=HURT && this.status!=DIYING && this.status!=ATTACKING){
            if(this.status!=status)
                this.status=status;
        }

        if(this.status!=status)
            this.status=status;

    }

    public void onDraw(Canvas canvas) {


        int srcX = currentFrame * frameWidth;

        int srcY = status * frameHeight;

        Rect src = new Rect(srcX, srcY, srcX + frameWidth, srcY + frameHeight);
        Rect dst = new Rect(x, y, x + frameWidth, y + frameHeight);

        canvas.drawBitmap(bmp, src, dst, null);

        update();
        logica();


    }


    public void attack(ArrayList<Enemy> enemys){
        if(attackCD<=0){
            if(status!=ATTACKING) {
                setStatus(ATTACKING);
                attacking=true;
            }
            Rect bounds1 = new Rect(x+35, y+50, x+((frameWidth/3)*2), y+frameHeight-35);

            for(int i=0;i<enemys.size();i++){
                Enemy t = enemys.get(i);
                Rect bounds2 = new Rect(t.getX(), t.getY(), t.getX()+t.getFrameWidth(), t.getY()+t.getFrameHeight());
                if (Rect.intersects(bounds1, bounds2)) {
                    t.receiveDmg();
                    MainActivity.ma.latigoHit();
                    break;
                }else{
                    MainActivity.ma.latigo();
                }
            }
        }
        else attackCD=4;
    }


    private void update(){
        currentFrame++;
        attackCD--;
        walkingCD--;


        if(status==HURT && currentFrame==frames[HURT]){
            setStatus(IDLE);
        }

        if(status==IDLE && currentFrame==frames[IDLE]){
            currentFrame=0;
        }

        if(status==WALKING && currentFrame==frames[WALKING]){
            setStatus(IDLE);
        }

        if(status==ATTACKING && currentFrame==frames[ATTACKING]){
            attacking=false;
            setStatus(IDLE);
        }

        if(isDead() && !muerto) {
            die();
        }

        if(status==DIYING && currentFrame==frames[DIYING]){
            muerto=true;
        }

        if (muerto){
            currentFrame=frames[DIYING];
            gameView.endGame();
        }

        if(currentFrame>=frames[status]) {
            currentFrame = 0;
        }

    }

    private void logica(){
        if (x >= gameView.getWidth() - frameWidth - xSpeed || x + xSpeed <= 0) {
            xSpeed = 0;
        }
        x = x + xSpeed;
        if (y >= gameView.getHeight() - frameHeight- ySpeed || y + ySpeed <= 0) {
            ySpeed = 0;
        }
        y = y + ySpeed;



        if(xSpeed>0 || xSpeed<0 || ySpeed>0 || ySpeed<0) {
            if(!attacking&&walkingCD<0) {
                setStatus(WALKING);
                walkingCD = 5;
            }
        }else{
            if(!attacking&&walkingCD<0) {
                setStatus(IDLE);
                walkingCD = 5;
            }
        }
    }


    private void die(){
       setStatus(DIYING);
    }

    public boolean isDead(){
        if (hp<=0)
            return true;

        return false;
    }


    public void receiveDmg() {
       if(!muerto) {
           MainActivity.ma.vibrar(500);
           MainActivity.ma.ouch();
           if (status != HURT) {
               setStatus(HURT);
           }
           hp--;
       }
    }


    public void setxSpeed(int x2){
       xSpeed=x2;
    }

    public void setySpeed(int y2){
        ySpeed=y2;
    }

}
