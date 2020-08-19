package entities;

import engine.CommandHandler;
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

    public void attack(Room room, String input) {
        String Target = getTarget(input);
        Player targetObj = world.getPlayer(Target);

        if (targetObj == null) {
            // Human attacking NPC
            List<NPC> targetList = world.getNPCList();
            for (NPC n : targetList) {
                String TargetName = n.getName();

                if (TargetName.equalsIgnoreCase(Target)) {
                    NPC npc = n;
                    int TargetHP = npc.getHP();
                    if (getItemAtkString(input) == null) {
                        TargetHP -= 3;
                        npc.setHP(TargetHP);
                        userStream.printToUser("attack successful");
                        agrNPCAtk(world, world.getPlayer(userId), npc);
                    } else {
                        TargetHP -= xtraDmg(input);
                        npc.setHP(TargetHP);
                        userStream.printToUser("attack successful");
                        agrNPCAtk(world, world.getPlayer(userId), npc);
                    }

                    if (TargetHP <= 0) {
                        userStream.printToUser(Target + " has died");
                        String TargetId = npc.getId();
                        List<Item> npcItems = npc.getItems();

                        for (Item i : npcItems){
                           List<Item> items =room.getItems();
                           items.add(i);
                        }
                        world.removeNpc(npc);


                    }
                    break;
                }

            }
        } else {
            boolean targetGod = targetObj.getgodemode();
            if (targetGod) {
                userStream.printToUser("your attack failed you hurt fred's feelings you shall now die");
                world.removePlayerFromGame(userId);
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
        world.removePlayerFromGame(targetUserId);
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

    public void NPCAtk(World world, Player Target, NPC npc) {
        Random rand = new Random();
        int randint = rand.nextInt(100);
        if (randint <= npc.getAtkChance()) {
            if( NPCHasItem (Target,npc)){
                return;
            }
            int targetHp = Target.getHP();
            targetHp -= npc.getAtkDmg();
            Target.setHP(targetHp);
            Target.getUserStream().printToUser("You were hit by " + npc.getName());
            if (targetHp <=0) {
                Target.getUserStream().printToUser("You have died, bad luck!");
                world.removePlayerFromGame(Target.getUserId());
            }
        }
    }

    public void agrNPCAtk(World world, Player Target, NPC npc) {
        Random agrrand = new Random();
        int agrrandint = agrrand.nextInt(100);
        double agrAtkChance = npc.getAtkChance() * npc.getAtkChanceMult();
        if (agrrandint <= agrAtkChance) {
            if( NPCHasItem (Target,npc)){
                return;
            }
            int targetHp = Target.getHP();
            targetHp -= npc.getAtkDmg();
            Target.setHP(targetHp);
            Target.getUserStream().printToUser("You were hit by " + npc.getName());
            if (targetHp <=0) {
                Target.getUserStream().printToUser("You have died, bad luck!");
                world.removePlayerFromGame(Target.getUserId());
            }
        }
    }
    public boolean NPCHasItem (Player Target,NPC npc){
        List<Item> itemsList = npc.getItems();
        if (itemsList != null && !itemsList.isEmpty()){
            Item atkItem = itemsList.get(0);
            int atkItemDmg  = atkItem.getDmg();
            int TargetHP = Target.getHP();
            TargetHP -= atkItemDmg;
            Target.setHP(TargetHP);
            Target.getUserStream().printToUser("You were hit by " + npc.getName() + " with the " + atkItem.getName());
            if (atkItem.getBleed()){
                bleed(Target);


            }
            if (atkItem.getPar()){
                Paralysis(atkItem,Target);
            }
            if (atkItem.getBreak()){
                Break(atkItem,Target);


            }
            return true;
        }else{
            return false;
        }
    }

}

