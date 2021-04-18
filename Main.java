import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

class Player {

    private static final List<Vector> checkpoints = new ArrayList<>();

    private static final List<Pod> myPods = Arrays.asList(new Pod(), new Pod());
    private static final List<Pod> enemyPods = Arrays.asList(new Pod(), new Pod());
    private static final List<Pod> allPods = Stream.of(myPods, enemyPods)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // Reading in laps and checkpoint numbers.
        int laps = in.nextInt();
        int checkpointCount = in.nextInt();

        // Reading in checkpoints
        for (int i = 0; i < checkpointCount; i++) {
            checkpoints.add(new Vector(in.nextInt(), in.nextInt()));
        }

        int bestBoostIndex = findLongestBoostDistance();

        // game loop
        while (true) {
            // Read in game data for all pods and insert them into pods.
            for (Pod pod : allPods) {
                pod.gameTick();
                pod.setPosition(in.nextInt(), in.nextInt());
                pod.setVelocity(in.nextInt(), in.nextInt());
                pod.setAngle(in.nextInt());
                pod.setCheckpoint(in.nextInt());
            }

            // Finding the leading enemy pod.
            Pod leadingEnemy = enemyPods.get(0).isAhead(enemyPods.get(1), checkpoints) ? enemyPods.get(0) : enemyPods.get(1);

            // Finding my pod, who is already in back.
            Pod myInterceptor = myPods.get(0).isAhead(myPods.get(1), checkpoints) ? myPods.get(1) : myPods.get(0);

            // Setting standard next checkpoint for my pods.
            myPods.forEach(pod -> {
                pod.setTarget(checkpoints.get(pod.getCheckpoint()));
                // If needed, use shield.
                if (pod != myInterceptor && pod.shieldUsage(myInterceptor)) pod.useShield();
                else if (pod.shieldUsage(enemyPods.get(0)) || pod.shieldUsage(enemyPods.get(1))) pod.useShield();
            });

            // Setting target for interception pod.
            myInterceptor.findAndSetInterceptionTarget(leadingEnemy, checkpoints);

            myPods.forEach(pod -> {
                pod.calculateThrust(bestBoostIndex);
                pod.printOutput();
            });
        }
    }

    /**
     * Method checks all possible checkpoint distances and returns the longest distance index.
     *
     * @return index of checkpoint b, in which case distance between checkpoints a and b is longest.
     */
    private static int findLongestBoostDistance() {
        int bestIndex = 0;
        double longestDistance = 0;

        for (int i = 0; i < checkpoints.size() - 1; i++) {

            // For n to 0 case.
            int j = (i + 1) % checkpoints.size();
            double nextDistance = Vector.distanceSqr(checkpoints.get(i), checkpoints.get(j));
            if (longestDistance < nextDistance) {
                bestIndex = j;
                longestDistance = nextDistance;
            }
        }
        return bestIndex;
    }
}


/**
 * This class is used to hold 2D Vector coordinates and static methods.
 */
class Vector {
    public final static int CHECKPOINT_RADIUS = 600;
    public final double x;
    public final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }


    // Computes the distance squared between vector a and vector b.
    public static double distanceSqr(Vector a, Vector b) {
        return Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2);
    }

    // Computes the distance between vector a and vector b.
    public static double distance(Vector a, Vector b) {
        return Math.sqrt(distanceSqr(a, b));
    }

    // Computes the dot product of vector a and vector b.
    public static double dot(Vector a, Vector b) {
        return a.x * b.x + a.y * b.y;
    }

    // Computes sum  of vector a and vector b.
    public static Vector add(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y);
    }

    // Computes minus of vector a and vector b.
    public static Vector minus(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y);
    }

    // Computes vector multiplication  of vector a and factor k.
    public static Vector multiply(double k, Vector a) {
        return new Vector(k * a.x, k * a.y);
    }

    // Normalizes this vector in place.
    public Vector normalize() {
        double norm = Math.sqrt(x * x + y * y);
        return new Vector(x / norm, y / norm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return Double.compare(vector.x, x) == 0 && Double.compare(vector.y, y) == 0;
    }
}


class Pod {
    private Vector position;
    private Vector velocity;
    private double angle;
    private int nextCheckPointId;
    private int progress;
    private final int MAX_THRUST = 100;

    private boolean hasBoost = true;
    private int shieldCooldown = 0;

    private boolean useBoost = false;
    private boolean useShield = false;

    private Vector target;
    private double thrust = 0;


    Pod() {
    }

    //------------------------------------------------------------------------
    //--------------------------------SETTERS---------------------------------
    //------------------------------------------------------------------------
    public void setPosition(int x, int y) {
        this.position = new Vector(x, y);
    }

    public void setVelocity(int vx, int vy) {
        this.velocity = new Vector(vx, vy);
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void setCheckpoint(int nextCheckPointId) {
        if (this.nextCheckPointId != nextCheckPointId) progress++;
        this.nextCheckPointId = nextCheckPointId;
    }

    public void setThrust(double thrust) {
        this.thrust = thrust;
    }

    public void setTarget(Vector target) {
        this.target = target;
    }


    //------------------------------------------------------------------------
    //--------------------------------GETTERS---------------------------------
    //------------------------------------------------------------------------

    public double getAngle() {
        return this.angle;
    }

    public int getCheckpoint() {
        return this.nextCheckPointId;
    }

    public int getProgress() {
        return this.progress;
    }

    public Vector getTarget() {
        return this.target;
    }

    public Vector getPosition() {
        return this.position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public double getThrust() {
        return this.thrust;
    }

    public boolean hasBoost() {
        return hasBoost;
    }

    public boolean hasShield() {
        return shieldCooldown == 0;
    }


    //------------------------------------------------------------------------
    //--------------------------------ACTIONS---------------------------------
    //------------------------------------------------------------------------

    public void gameTick() {
        useBoost = false;
        useShield = false;
        if (shieldCooldown > 0) shieldCooldown--;
    }

    public void useBoost() {
        //We can't change thrust on shield cooldown.
        if (hasBoost() && hasShield()) {
            useBoost = true;
            hasBoost = false;
        }
    }

    public void useShield() {
        if (hasShield()) {
            shieldCooldown = 3;
            useShield = true;
        }
    }


    /**
     * d - distance slow down factor
     * a - angle slow down factor
     *
     * @param bestBoostIndex
     */
    public void calculateThrust(int bestBoostIndex) {

        // Angle is good to go. Full thrust!!
        if (this.getAngle() < 5) {
            this.setThrust(MAX_THRUST);
            if (this.getCheckpoint() == bestBoostIndex) this.useBoost();
            return;
        }

        // Setting offset from target to counter current movement.
        this.setTarget(Vector.minus(this.getTarget(), Vector.multiply(3, this.getVelocity())));

        // Finding distance slowdown factor d.
        double checkpointDistance = Vector.distance(this.getTarget(), this.getPosition());
        double d = Math.max(Math.min(checkpointDistance / (1.7 * Vector.CHECKPOINT_RADIUS), 1), 0);

        // Finding angle slowdown factor a.
        Vector checkpointDirection = Vector.minus(this.getTarget(), this.getPosition()).normalize();
        double acos = Math.acos(checkpointDirection.x) * 180 / Math.PI;
        double checkpointAngle = checkpointDirection.y < 0 ? 360 - acos : acos;

        double angle = checkpointAngle - this.getAngle();
        double a = 1 - Math.max(Math.min(Math.abs(angle) / 90, 1), 0);

        this.setThrust(MAX_THRUST * d * a);
    }


    /**
     * Method prints pod output.
     */
    public void printOutput() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((int) Math.round(this.getTarget().x));
        stringBuilder.append(" ");
        stringBuilder.append((int) Math.round(this.getTarget().y));
        stringBuilder.append(" ");

        if (useBoost) stringBuilder.append("BOOST");
        else if (useShield) stringBuilder.append("SHIELD");
        else if (shieldCooldown > 0) stringBuilder.append("0");
        else stringBuilder.append((int) Math.round(this.getThrust()));
        System.out.println(stringBuilder);
    }


    /**
     * Mehtod checks, if current pod is ahead of other pod.
     * @param secondPod
     * @param checkpoints list of checkpoints, for comparing distances.
     * @return True, if this pod is ahead. False otherwise.
     */
    public boolean isAhead(Pod secondPod, List<Vector> checkpoints) {
        if (this.getProgress() != secondPod.getProgress())
            return this.getProgress() > secondPod.getProgress();

        Vector checkpoint1 = checkpoints.get(this.getCheckpoint());
        Vector checkpoint2 = checkpoints.get(secondPod.getCheckpoint());

        return Vector.distanceSqr(this.getPosition(), checkpoint1)
                < Vector.distanceSqr(secondPod.getPosition(), checkpoint2);
    }

    /**
     * Method uses vector calculatation to determin if shield should be used.
     * @param enemyPod Pod to check if collision is about to happen.
     * @return True if shield is really needed.
     */
    public boolean shieldUsage(Pod enemyPod) {
        // Checking if pods might collide on next turn.
        Vector myNextPos = Vector.add(this.getPosition(), this.getVelocity());
        Vector enemyNextPos = Vector.add(enemyPod.getPosition(), enemyPod.getVelocity());
        if (Vector.distance(myNextPos, enemyNextPos) >= (2 * 425)) return false;

        // Checking if pods direction are too similar.
        Vector directionTarget = Vector.minus(this.getTarget(), this.getPosition()).normalize();
        Vector directionEnemyPod = Vector.minus(enemyPod.getPosition(), this.getPosition()).normalize();
        return Vector.dot(directionTarget, directionEnemyPod) > 0.3;
    }

    /**
     * Method finds the best way to intercept enemy pod.
     * @param enemyPod
     * @param checkpoints
     */
    public void findAndSetInterceptionTarget(Pod enemyPod, List<Vector> checkpoints) {
        Vector nextEnemyCheckpoint = checkpoints.get(enemyPod.getCheckpoint());
        Vector enemyTrajectory = Vector.minus(nextEnemyCheckpoint, this.getPosition());

        Vector po = Vector.minus(enemyPod.getPosition(), this.getPosition());

        // Must be in opposite directions, otherwise it is pointless to follow him.
        if (!(Vector.dot(po, enemyTrajectory) > 0 ||
                Vector.distance(this.getPosition(), nextEnemyCheckpoint) >
                        Vector.distance(enemyPod.getPosition(), nextEnemyCheckpoint))) {

            this.setTarget(Vector.add(enemyPod.getPosition(), Vector.multiply(3, enemyPod.getVelocity())));
            return;
        }


        // Finding best checkpoint to go on and camp. =)
        double enemyDistance = 0;
        Vector enemyPodPosition = enemyPod.getPosition();
        int i = 0;

        while (i < checkpoints.size()) {
            int checkpointId = (enemyPod.getCheckpoint() + i) % (checkpoints.size());
            Vector nextCheckpoint = checkpoints.get(checkpointId);
            enemyDistance += Vector.distance(enemyPodPosition, nextCheckpoint);
            double myPodDistance = Vector.distance(this.getPosition(), nextCheckpoint);

            enemyPodPosition = nextCheckpoint;
            i++;

            if (myPodDistance < enemyDistance) break;
        }
        this.setTarget(enemyPodPosition);

    }

}
