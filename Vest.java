public class Vest extends Item {
    Vest(int x, int y, int length, int width, int cost, String description) {
        super("Vest", x, y, length, width, cost, description);
    }

    @Override
    public void activateItem(Creature user) {
        user.setMaxHp(user.getMaxHp() + 40);
        user.setHp(user.getHp() + 40);
    }
}
