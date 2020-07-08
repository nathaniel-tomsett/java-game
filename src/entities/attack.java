package entities;

import com.sun.xml.internal.bind.v2.model.core.ID;
import engine.World;
import users.UserStream;
import util.TextColours;

import java.util.List;

public class attack {
    private World world;
    UserStream userStream;
    String userId;

    public attack(World w, Player p) {
        this.world = w;
        this.userStream = p.getUserStream();
        this.userId = p.getUserId();
    }

    public void attack(String input) {
        String Target = getTarget(input);
        Player targetObj = world.getPlayer(Target);

        if (targetObj == null) {
            List<NPC> targetList = world.getNPCList();
            for (NPC n : targetList) {
                String TargetName = n.getName();

                if (TargetName.equalsIgnoreCase(Target)) {
                    NPC TargetObj = n;
                    int TargetHP = TargetObj.getHP();
                    if (getItemAtkString(input) == null) {
                        TargetHP -= 3;
                        TargetObj.setHP(TargetHP);
                        userStream.printToUser("attack successful");
                    } else {
                        TargetHP -= xtraDmg(input);
                        TargetObj.setHP(TargetHP);
                        userStream.printToUser("attack successful");
                    }

                    if (TargetHP <= 0) {
                        userStream.printToUser(Target + " has died");
                        String TargetId = TargetObj.getId();
                        world.removeNpc(TargetObj);

                    }
                    break;
                }

            }
        } else {
            boolean targetGod = targetObj.getgodemode();
            if (targetGod = true) {
                userStream.printToUser("your attack failed you hurt fred's feelings you shall now die");
                userStream.killStream();
                world.removePlayer(userId);
            }
            int targetHp = targetObj.getHP();
            if (getItemAtkString(input).equals(null)) {
                targetHp -= 3;
                userStream.printToUser("attack successful");
                targetObj.setHP(targetHp);
            } else {
                xtraDmg(input);
                targetHp -=  xtraDmg(input);
                userStream.printToUser("attack successful");
                targetObj.setHP(targetHp);
            }
            if (targetHp <= 0) {
                if (targetObj.equals((userId))) {
                    userStream.printToUser("you just killed yourself well done heres a gold sticker");
                }
                userStream.printToUser(Target + " has died");
                UserStream targetUserStream = targetObj.getUserStream();
                targetUserStream.printToUser("you have died at the hands of " + userId);
                String targetUserId = targetObj.getUserId();
                world.removePlayer(targetUserId);
                targetUserStream.killStream();
            } else {
                targetObj.setHP(targetHp);
                userStream.printToUser(Target + " successful attack");
                UserStream targetUserStream = targetObj.getUserStream();
                targetUserStream.printToUser("you have been attacked by " + userId);
            }

        }

    }



        public String getTarget(String input){

            int firstSpace = input.indexOf(" ");
            int secondSpace = input.indexOf(" ", firstSpace+1);
            if (secondSpace == -1) {
                String target = input.substring(firstSpace);
                return target.trim();
            } else {
                String target = input.substring(firstSpace, secondSpace);
                return target.trim();
            }
        }


        public Item getItemAtkString(String input) {
            // turn input into two strings
            int withIndex = input.indexOf("with");
            if (withIndex == -1) {
                return null;
            }
            String itemIndex = input.substring(withIndex);
            int SpaceIndex = itemIndex.indexOf(" ");
            if (SpaceIndex == -1) {
                return null;
            }

            String subItemIndex = itemIndex.substring(SpaceIndex).trim();
            if (world.getPlayer(userId).getInventory().doesexistByName(subItemIndex)) {
                Item item = world.getPlayer(userId).getInventory().getItem(subItemIndex);
                return item;
            }else{
                return null;
            }
        }

    public int xtraDmg(String input) {
        Item item = getItemAtkString(input);
        int dmgMod = item.getDmg();

        return dmgMod;
    }
}

