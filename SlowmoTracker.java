import java.security.Timestamp;
import java.awt.*;

public class SlowmoTracker {
    private double activeSlowAmount = 1;
    private SlowmoTrackerSound sound = new SlowmoTrackerSound();
    private double gameTime;
    private Color backgroundColor = Color.black;

    SlowmoTracker() {
        this.gameTime = 0;
    }

    public void activateSlow(double slowAmount) {
        if (activeSlowAmount == 1) {
            activeSlowAmount = slowAmount;
            sound.slowSound();
            backgroundColor = new Color(54, 11, 8);
        } else {
            activeSlowAmount = 1;
            sound.resumeTimeSound();
            backgroundColor = Color.black;
        }
    }

    public void setSlow(double slowAmount) {
        activeSlowAmount = slowAmount;
    }

    public void drawBackground(Graphics g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, Const.WIDTH, Const.HEIGHT);
    }

    public void drawForeground(Graphics g) {
        if (this.activeSlowAmount != 1) {
            for (int i = 0; i < randint(5, 12); i++) {
                g.setColor(new Color(255, 89, 77, 40));
                g.fillRect(randint(0, Const.WIDTH - 250), randint(0, Const.HEIGHT - 20), randint(100, 400), 20);
            }
        }
    }

    public int randint(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    // getters
    public double getActiveSlowAmount() {
        return activeSlowAmount;
    }

    public double getGameTime() {
        return this.gameTime;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    // Setters
    public void increaseGameTime(double amount) {
        gameTime += amount;
    }
}
