package cn.vhyme.ballcraft.ui;

import android.content.Context;
import java.util.List;
import java.util.Vector;


public class NPCBall extends PlayerBall {

    private List<Ball> balls;

    public static final int WALL_FORCE = 60;

    public NPCBall(Context context, float x, float y, float radius) {
        super(context, x, y, radius);
    }

    public void updateBallList(Vector<Ball> balls){
        this.balls = new Vector<>(balls);
    }

    @Override
    public void move(int worldW, int worldH) {
        if (balls.size() > 0) {

            // 对NPC的运动算法进行重构
            // 使用模拟力学的方案
            float vx = 0, vy = 0, totalModule = 0;
            for (Ball ball : balls) {
                // 发现大小相同的球，不做处理
                if (ball.radius == radius) continue;

                if (ball.radius < radius) {
                    // 发现食物，产生引力
                    float distanceSquare = (ball.x - x) * (ball.x - x) + (ball.y - y) * (ball.y - y);
                    if(distanceSquare == 0) continue;
                    float module = ball.radius * ball.radius / distanceSquare;
                    float x1 = (ball.x - x);
                    float y1 = (ball.y - y);
                    float module2 = (float) Math.sqrt(x1 * x1 + y1 * y1);
                    vx += module * x1 / module2;
                    vy += module * y1 / module2;
                    totalModule += module;
                } else {
                    // 发现敌人，产生斥力
                    float distanceSquare = (ball.x - x) * (ball.x - x) + (ball.y - y) * (ball.y - y);
                    if(distanceSquare == 0) continue;
                    float module = ball.radius * ball.radius / distanceSquare;

                    // 为了提高难度，令NPC对NPC放松警惕
                    if(ball instanceof NPCBall) module *= 0.7f;
                    float x1 = (ball.x - x);
                    float y1 = (ball.y - y);
                    float module2 = (float) Math.sqrt(x1 * x1 + y1 * y1);
                    vx -= module * x1 / module2;
                    vy -= module * y1 / module2;
                    totalModule -= module;
                }
            }

            // 若被追杀，产生对墙的斥力
            if(totalModule < 0) {
                if (x > 0) {
                    float module = WALL_FORCE / (x * x);
                    vx += module * x;
                }
                if (worldW - x > 0) {
                    float module = WALL_FORCE / ((worldW - x) * (worldW - x));
                    vx += module * (x - worldW);
                }
                if (y > 0) {
                    float module = WALL_FORCE / (y * y);
                    vy += module * y;
                }
                if (worldH - y > 0) {
                    float module = WALL_FORCE / ((worldH - y) * (worldH - y));
                    vy += module * (y - worldH);
                }
            }

            speedX += vx;
            speedY += vy;
            float module = (float) Math.sqrt(speedX * speedX + speedY * speedY);
            if (module != 0) {
                speedX = speedX / module * getSpeedFactor();
                speedY = speedY / module * getSpeedFactor();
            }
        }
        super.move(worldW, worldH);
    }
}