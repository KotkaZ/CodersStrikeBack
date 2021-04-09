
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

    private static final Pod myPod1 = new Pod(0);
    private static final Pod myPod2 = new Pod(1);
    private static final Pod enemyPod1 = new Pod(2);
    private static final Pod enemyPod2 = new Pod(2);
    private static final List<Pod> allPods = Arrays.asList(myPod1, myPod2, enemyPod1, enemyPod2);

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
            // Readin in game data for all pods. 
            for(Pod pod : allPods){
                pod.setCoordinates(in.nextInt(), in.nextInt());
                pod.setVelocity(in.nextInt(), in.nextInt());
                pod.setAngle(in.nextInt());
                pod.setCheckpoint(in.nextInt());
            }
            
            // Finding the leading enemy pod.
            Pod bestEnemyPod = enemyPod1.getProgress() > enemyPod2.getProgress() ? enemyPod1 : enemyPod2;

            myPod1.move(checkpoints[myPod1.getCheckpoint()][0], checkpoints[myPod1.getCheckpoint()][1]);
            myPod2.move(bestEnemyPod.getX() - 3* bestEnemyPod.getVX(), bestEnemyPod.getY() - 3* bestEnemyPod.getVY());
                            
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
    private int progress;
    private final int MAX_THRUST = 100;
    private Type type;

    public static enum Type {
        RACER,
        INTERCEPTOR,
        ENEMY
    }

    Pod(int type){
        if(type == 0) this.type = Type.RACER;
        else if(type == 1) this.type = Type.INTERCEPTOR;
        else  this.type = Type.ENEMY;
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
        if(this.nextCheckPointId != nextCheckPointId) progress++;
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

    public int getProgress(){
        return this.progress;
    }

    public void move(int checkpointX, int checkpointY){
        int thrust = MAX_THRUST;
        int checkpointDistance = checkpointDistance(checkpointX, checkpointY);

        if(type == Type.INTERCEPTOR && checkpointDistance<500 && (Math.abs(vx) + Math.abs(vy))>=200){
            System.out.println((checkpointX - 1 * vx) + " " + (checkpointY - 1 * vy) + " SHIELD");
        }
        else if(type == Type.INTERCEPTOR && checkpointDistance >= 3000 && getAngle() <= 4){
            System.out.println((checkpointX - 3 * vx) + " " + (checkpointY - 3 * vy) + " BOOST");
        }
        else if(type == Type.RACER && checkpointDistance >= 4000 && getAngle() <= 2){
            System.out.println((checkpointX - 3 * vx) + " " + (checkpointY - 3 * vy) + " BOOST");
        }
        else{
            int wayAngle = distance(getAngle(), getBearing(getX(), getY(), checkpointX, checkpointY));
            wayAngle = wayAngle > 90 ? 90 : wayAngle;

            if (wayAngle >= 4) thrust =  (int) Math.round(thrust * (1 - (wayAngle/(double)95)));
            System.out.println((checkpointX - 3 * vx) + " " + (checkpointY - 3 * vy) + " " + thrust);
        }
    }

    // in 2d distance between two points.
    private int checkpointDistance(int checkpointX, int checkpointY){
        return (int) Math.round(Math.sqrt(Math.pow(checkpointX - x, 2) + Math.pow(checkpointY - y, 2)));
    }

    // Angle between two lines.
    public int distance(int alpha, int beta) {
        int phi = Math.abs(beta - alpha) % 360;  
        int distance = phi > 180 ? 360 - phi : phi;
        return distance;
    }


}
