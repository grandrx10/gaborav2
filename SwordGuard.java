import java.util.*;

public class SwordGuard extends Enemy {
    SwordGuard(int x, int y, int length, int width, String picName) {
        super(x, y, length, width, picName);
        super.setGravity(0.2);
        super.setDetectRange(500);
        super.setEngageRange(80);
        super.setRunAccel(0.8);
        super.setAttackCooldown(40);
        super.setJumpSpeed(7);
        super.setTeam(1);
        super.setMaxHp(30);
    }

    public void attack(ArrayList<Entity> entities, ArrayList<Bullet> bullets) {
        bullets.add(new Slash(super.getX() + super.getLength() / 2, super.getY() + super.getWidth() / 2,
                super.getDestinationX(), super.getDestinationY(),
                100, 5, super.getTeam(), 2, 80, false,
                "slash", this));
    }

    @Override
    public void update(ArrayList<Entity> entities, ArrayList<Bullet> bullets, SlowmoTracker slowmoTracker) {
        super.updateDestination(entities);
        super.update(entities, bullets, slowmoTracker);
        super.search(entities, bullets, slowmoTracker);
    }
}
