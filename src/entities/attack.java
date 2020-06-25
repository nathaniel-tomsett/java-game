package entities;

import engine.World;
import users.UserStream;
import util.TextColours;

public class attack {
    private World world;
    UserStream userStream;
    String userId;

    public attack (World w, Player p) {
        this.world = w;
        this.userStream = p.getUserStream();
         this.userId = p.getUserId();
    }

    public void attack (String input) {

        String target = getTarget(input);

        Player targetObj = world.getPlayer(target);

        if (targetObj == null) {

        } else {
            int targetHp = targetObj.getHP();
            targetHp -= 3;
            if (targetHp <= 0) {
                if (targetObj.equals((userId))) {
                    userStream.printToUser("you just killed yourself well done heres a gold sticker");
                }
                userStream.printToUser(target + " has died");
                UserStream targetUserStream = targetObj.getUserStream();
                targetUserStream.printToUser("you have died at the hands of " + userId);
                String targetUserId= targetObj.getUserId();
                world.removePlayer(targetUserId);
                targetUserStream.killStream();
            } else {
                targetObj.setHP(targetHp);
                userStream.printToUser(target + " successful attack");
                UserStream targetUserStream = targetObj.getUserStream();
                targetUserStream.printToUser("you have been attacked by " + userId );
            }
        }
    }

    public String getTarget(String input) {

        int firstSpace = input.lastIndexOf(" ");
        String target = input.substring(firstSpace);
        return target.trim();
        }

}
