package entities;

import engine.World;

public class attack {
    private World world;

    public attack(World w) {
        world = w;
    }

    public void attack (String input) {

        String target = getTarget(input);
        Player targetObj = world.getPlayer(target);
        if (targetObj == null) {

        } else {
            int targetHp = targetObj.getHP();
            targetHp -= 3;
            targetObj.setHP(targetHp);
        }
    }

    public String getTarget(String input) {

        int firstSpace = input.lastIndexOf(" ");
        String target = input.substring(firstSpace);
        return target.trim();
        }

}
