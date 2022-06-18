import java.util.*;

public class Slash extends Bullet {
    Sound deflectSound = new Sound("audio/defect.wav");

    Slash(double x, double y, double aimX, double aimY, int r, double speed, int team,
            int damage, int distance, boolean isRemovedOnHit, String picName, Entity shooter) {
        super(x, y, aimX, aimY, r, speed, team, damage, distance, isRemovedOnHit, picName, shooter);
        super.setSound("audio/slash.wav");
        super.setDeflectable(false);
    }

    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        for (int i = 0; i < bullets.size(); i++) {
            if (super.circCircDetect(bullets.get(i), this) && this != bullets.get(i)
                    && super.getTeam() != bullets.get(i).getTeam()
                    && bullets.get(i).getDeflectable()) {
                bullets.get(i).setAim(super.getAimX(), super.getAimY());
                bullets.get(i).setTeam(super.getTeam());
                bullets.get(i).setShooter(super.getShooter());
                deflectSound();
            }
        }
        super.update(entities, bullets, slowmoTracker);
    }

    public void deflectSound() {
        if (deflectSound.isRunning()) {
            deflectSound.stop();
            deflectSound.flush();
            deflectSound.setFramePosition(0);
            deflectSound.start();
        } else {
            deflectSound.start();
        }
    }
}
