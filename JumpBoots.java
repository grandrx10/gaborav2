public class JumpBoots extends Item {
    JumpBoots(int x, int y, int length, int width, int cost, String description) {
        super("JumpBoots", x, y, length, width, cost, description);
    }

    @Override
    public void activateItem(Creature user) {
        user.setMaxJumps(user.getMaxJumps() + 1);
    }
}
