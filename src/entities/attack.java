package entities;

import engine.World;
import users.UserStream;
import util.TextColours;

public class attack {
    private World world;
    UserStream userStream;


    public attack (World w, Player p) {
        this.world = w;
        this.userStream = p.getUserStream();
    }

    public void attack (String input) {

        String target = getTarget(input);
        Player targetObj = world.getPlayer(target);
        if (targetObj == null) {

        } else {
            int targetHp = targetObj.getHP();
            targetHp -= 3;
            if (targetHp <= 0) {
                userStream.printToUser(target + "has died");
                UserStream targetUserStream = targetObj.getUserStream();
                targetUserStream.printToUser("you have died");
                String targetUserId= targetObj.getUserId();
                world.removePlayer(targetUserId);
                targetUserStream.killStream();
            } else {
                targetObj.setHP(targetHp);
                userStream.printToUser(target + "successful attack, new health = " + targetHp);
                UserStream targetUserStream = targetObj.getUserStream();
                targetUserStream.printToUser("you have been attacked");
            }
        }
    }

    public String getTarget(String input) {

        int firstSpace = input.lastIndexOf(" ");
        String target = input.substring(firstSpace);
        return target.trim();
        }

}
