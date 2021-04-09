import java.util.*;
import java.io.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private static int laps;
    private static int checkpointCount;
    private static int[][] checkpoints;

    private static final Pod myPod1 = new Pod();
    private static final Pod myPod2 = new Pod();
    private static final Pod enemyPod1 = new Pod();
    private static final Pod enemyPod2 = new Pod();


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // Reading in 
        laps = in.nextInt();
        checkpointCount = in.nextInt();
        checkpoints = new int[checkpointCount][2];

        // Reading in checkpoints
        for (int i = 0; i < checkpoints.length; i++) {
            checkpoints[i][0] = in.nextInt();
            checkpoints[i][1] = in.nextInt();
        }

        // game loop
        while (true) {
            myPod1.setCoordinates(in.nextInt(), in.nextInt());
            myPod1.setVelocity(in.nextInt(), in.nextInt());
            myPod1.setAngle(in.nextInt());
            myPod1.setCheckpoint(in.nextInt());
            
            myPod2.setCoordinates(in.nextInt(), in.nextInt());
            myPod2.setVelocity(in.nextInt(), in.nextInt());
            myPod2.setAngle(in.nextInt());
            myPod2.setCheckpoint(in.nextInt());

            enemyPod1.setCoordinates(in.nextInt(), in.nextInt());
            enemyPod1.setVelocity(in.nextInt(), in.nextInt());
            enemyPod1.setAngle(in.nextInt());
            enemyPod1.setCheckpoint(in.nextInt());

            enemyPod2.setCoordinates(in.nextInt(), in.nextInt());
            enemyPod2.setVelocity(in.nextInt(), in.nextInt());
            enemyPod2.setAngle(in.nextInt());
            enemyPod2.setCheckpoint(in.nextInt());

            myPod1.move(checkpoints[myPod1.getCheckpoint()][0], checkpoints[myPod1.getCheckpoint()][1]);
            myPod2.move(checkpoints[myPod2.getCheckpoint()][0], checkpoints[myPod2.getCheckpoint()][1]);
            //if()
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            //System.err.println(nextCheckpointDist + " " + boostCount);

            // You have to output the target position
            // followed by the power (0 <= thrust <= 100)
            // i.e.: "x y thrust"
            /*
            int thrust = MAX_THRUST;
            if(boostCount>0 && nextCheckpointDist >= 5000 && newAngle <= 2){
                boostCount--;
                System.out.println((nextCheckpointX - 3 * (x-previousX)) + " " + (nextCheckpointY - 3 * (y-previousY)) + " BOOST");
                System.out.println((nextCheckpointX - 3 * (x-previousX)) + " " + (nextCheckpointY - 3 * (y-previousY)) + " BOOST");
            }
            else{
                if ( newAngle >= 3) thrust =  (int) Math.round(thrust * (1 - (newAngle/(double)90)));
                System.out.println((nextCheckpointX - 3 * (x-previousX)) + " " + (nextCheckpointY - 3 * (y-previousY)) + " " + thrust);
                System.out.println((nextCheckpointX - 3 * (x-previousX)) + " " + (nextCheckpointY - 3 * (y-previousY)) + " " + thrust);
            }*/
            //if (nextCheckpointDist <= 1200) 
            //    thrust = (int) Math.round(thrust * (nextCheckpointDist / 1200));
                
            
        }
    }
}

class Pod{
    private int x;
    private int y;
    private int vx;
    private int vy;
    private int angle;
    private int nextCheckPointId;
    private final int MAX_THRUST = 100;

    Pod(){
    }

    public void setCoordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setVelocity(int vx, int vy){
        this.vx = vx;
        this.vy = vy;
    }

    public void setAngle(int angle){
        this.angle = angle;
    }

    public void setCheckpoint(int nextCheckPointId){
        this.nextCheckPointId = nextCheckPointId;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int getVX(){
        return this.vx;
    }

    public int getVY(){
        return this.vy;
    }

    public int getAngle(){
        return this.angle;
    }

    public int getBearing(int x1, int y1, int x2, int y2){
        int bearing = (int) Math.round(Math.toDegrees(Math.atan2(y1 - y2, x1 - x2)));
        bearing -= 180;
        return (bearing + 360) % 360;

    }

    public int getCheckpoint(){
        return this.nextCheckPointId;
    }

    public void move(int checkpointX, int checkpointY){
        int thrust = MAX_THRUST;
        int checkpointDistance = checkpointDistance(checkpointX, checkpointY);


        if(checkpointDistance >= 5000 && getAngle() <= 2){
            System.out.println((checkpointX - 3 * vx) + " " + (checkpointY - 3 * vy) + " BOOST");
        }
        else{
            int wayAngle = distance(getAngle(), getBearing(getX(), getY(), checkpointX, checkpointY));
            wayAngle = wayAngle > 90 ? 90 : wayAngle;

            if (wayAngle >= 3) thrust =  (int) Math.round(thrust * (1 - (wayAngle/(double)90)));
            System.out.println((checkpointX - 3 * vx) + " " + (checkpointY - 3 * vy) + " " + thrust);
        }
    }

    private int checkpointDistance(int checkpointX, int checkpointY){
        return (int) Math.round(Math.sqrt(Math.pow(checkpointX - x, 2) + Math.pow(checkpointY - y, 2)));
    }

    public static int distance(int alpha, int beta) {
        int phi = Math.abs(beta - alpha) % 360;       // This is either the distance or 360 - distance
        int distance = phi > 180 ? 360 - phi : phi;
        return distance;
    }


}
