import java.util.*;

public class Rocket extends Bullet {
    Rocket(double x, double y, double aimX, double aimY, int r, double speed, int team,
            int damage, int distance, boolean isRemovedOnHit, String picName, Entity shooter) {
        super(x, y, aimX, aimY, r, speed, team, damage, distance, isRemovedOnHit, picName, shooter);
    }

    @Override
    public void remove(ArrayList<Bullet> bullets) {
        bullets.add(new Bullet(getX(), getY(), getAimX(), getAimY(), 100, 0.1, this.getTeam(),
                100, 1, false, "explosion", getShooter()));
        bullets.get(bullets.size() - 1).setSound("audio/explosion.wav");
        bullets.remove(this);
    }

}
