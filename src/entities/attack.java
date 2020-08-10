package entities;

import engine.World;
import users.UserStream;

import java.util.List;
import java.util.Random;

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
            if (targetGod) {
                userStream.printToUser("your attack failed you hurt fred's feelings you shall now die");
                userStream.killStream();
                world.removePlayer(userId);
            }
            int targetHp = targetObj.getHP();
            if (getItemAtkString(input) == null) {
                targetHp -= 3;
                userStream.printToUser("attack successful");
                targetObj.setHP(targetHp);
            } else if (getItemAtkString(input).getBleed()){
                xtraDmg(input);
                targetHp -= xtraDmg(input);
                Item item = getItemAtkString(input);
                userStream.printToUser("attack successful");
                targetObj.setHP(targetHp);
                if (item.getBleed()) {
                    bleed(targetObj);
                }
            }else if (getItemAtkString(input).getPar()){
                xtraDmg(input);
                targetHp -= xtraDmg(input);
                Item item = getItemAtkString(input);
                userStream.printToUser("attack successful");
                targetObj.setHP(targetHp);
                Paralysis(item,targetObj);

            }
                else {
                    xtraDmg(input);
                    targetHp -= xtraDmg(input);
                    Item item = getItemAtkString(input);
                    userStream.printToUser("attack successful");
                    targetObj.setHP(targetHp);
                    if (item.getBreak()) {
                        Break(item, targetObj);
                    }
                }
            if (targetHp <= 0) {
                killUserOff(targetObj);
            } else {
                UserStream targetUserStream = targetObj.getUserStream();
                targetUserStream.printToUser("you have been attacked by " + userId);
            }

        }

    }

    public void killUserOff(Player targetPlayer) {
        if (targetPlayer.getUserId().equals((userId))) {
            userStream.printToUser("you just killed yourself well done here's a gold sticker");
        }
        userStream.printToUser(targetPlayer.getUserId() + " has died");
        UserStream targetUserStream = targetPlayer.getUserStream();
        targetUserStream.printToUser("you have died at the hands of " + userId);
        String targetUserId = targetPlayer.getUserId();
        world.removePlayer(targetUserId);
        targetUserStream.killStream();
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

    public void bleed(Player target) {
        new Thread() {
            public void run() {
                boolean jimbob = true;
                while(jimbob)
                {
                    Random rand = new Random();
                    int randend = rand.nextInt(10);
                    int hp = target.getHP();
                    hp -= 1;
                    target.setHP(hp);
                    if (hp <= 0) {
                        killUserOff(target);
                        jimbob = false;
                    } else {
                        target.getUserStream().printToUser("you are bleeding find a medkit");
                        if (randend == 1) {
                            jimbob = false;
                            UserStream targuserstream = target.getUserStream();
                            targuserstream.printToUser("you are no longer bleeding you have survived");
                        }
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }.start();
    }
    public void Break(Item item, Player target){
        Random rand = new Random();
        int randbreak = rand.nextInt(1);
        if (randbreak == 0){
            String itemname = item.getName();
            String itemcomp = "atk jim with "+ itemname;
            int targetHP = target.getHP();
            targetHP -= xtraDmg(itemcomp);
            target.setHP(targetHP);
            Inventory playerInv = world.getPlayer(userId).getInventory();
            playerInv.removeItem(item);
            bleed(target);
            userStream.printToUser("the glass shatters over "+ target.getUserId()+"'s head");
        }
    }

    public void Paralysis(Item item, Player Target){
        boolean canPar = item.getPar();
        if (canPar){
            Target.getUserStream().printToUser("you have been paralysed you can no longer move or do anything enjoy");
            Target.setIsPar(canPar);
        }
    }
}

