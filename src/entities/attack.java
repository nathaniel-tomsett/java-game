package entities;

import engine.CommandHandler;
import engine.World;
import users.UserStream;

import java.util.List;
import java.util.Random;

/**
 * NPC = Non-Playable Character , PC = Playable Character
 * a class used to handle all attacks and attack effects (there's a lot to do here)
 * this deals with PCs attacking PCs or NPCs
 * also NPCs attacking PCs
 * however NPCs will never attack NPCs
 */
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
        world.dumpPlayersHash(userId);

        if (targetObj == null) {
            boolean npcFound = false;
/**
 * PC attacking NPC
 */
            new auditFile().writeLogLine("attacking NPC", userId);
            List<NPC> targetList = world.getNPCList();
            for (NPC n : targetList) {
                String TargetName = n.getName();

                if (TargetName.equalsIgnoreCase(Target)) {
                    npcFound = true;
                    NPC npc = n;
                    int TargetHP = npc.getHP();
                    /**
                     * attacking without weapon
                     */
                    if (getItemAtkString(input) == null) {
                        TargetHP -= 3;
                        npc.setHP(TargetHP);
                        userStream.printToUser("attack successful");
                        agrNPCAtk(world, world.getPlayer(userId), npc);
                    }
                    /**
                     * attacking with weapon
                     */

                    else {
                        Item item = getItemAtkString(input);
                        TargetHP -= itemDmg(input);
                        npc.setHP(TargetHP);
                        userStream.printToUser("attack successful");
                        agrNPCAtk(world, world.getPlayer(userId), npc);
                    }
/**
 * a check for npc death
 * if this happens all their items drop in the current room
 * then they are removed from the world
 */
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
            if (!npcFound) {
                userStream.printToUser("could not find a player of that name");
            }
        } else {
            new auditFile().writeLogLine("attacking player" + targetObj, userId);
/**
 * checks if you attacked god
 * if you did that's very bad. You die
 */
            boolean targetGod = targetObj.getgodemode();
            if (targetGod) {
                userStream.printToUser("your attack failed you hurt fred's feelings you shall now die");
                world.removePlayerFromGame(userId);
            }
            /**
             * PC attacking PC
             */
            int targetHp = targetObj.getHP();
            if (getItemAtkString(input) == null) {
                targetHp -= 3;
                userStream.printToUser("attack successful");
                targetObj.setHP(targetHp);
            }
            /**
             * attacking with weapon
             * also weapon effects bleed, paralysis and break
             * bleed - you take small damage at intervals until you die or find and use a medkit
             * paralysis - you cannot move for a set amount of time
             * break - destroys the item and causes bleed effect
             */
            else {
                itemDmg(input);
                targetHp -= itemDmg(input);
                Item item = getItemAtkString(input);
                userStream.printToUser("attack successful");
                targetObj.setHP(targetHp);
                if (item.getBleed()) {
                    bleed(targetObj);
                }
                if (item.getPar()) {
                    Paralysis(item, targetObj);
                }
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

    /**
     * removes player from game and checks if you killed yourself
     * then outputs a death message
     * @param targetPlayer
     */
    public void killUserOff(Player targetPlayer) {
        if (targetPlayer.getUserId().equals((userId))) {
            userStream.printToUser("you just killed yourself well done here's a gold sticker");
        }
        else {
            userStream.printToUser(targetPlayer.getUserId() + " has died");
            UserStream targetUserStream = targetPlayer.getUserStream();
            targetUserStream.printToUser("you have died at the hands of " + userId);
        }
        String targetUserId = targetPlayer.getUserId();
        world.removePlayerFromGame(targetUserId);
    }

    /**
     * a parser to get the target the user has inputted on its own in a variable
     * @param input the input a user has just written into the command line
     * @return the target that the user wishes to target
     */
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

    /**
     * a parser to get the item that a user wants to use on its own in a variable
     * @param input the input a user has just written into the command line
     * @return the item a user wishes to use
     */
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
        if (world.getPlayer(userId).getInventory().doesExistByName(subItemIndex)) {
            Item item = world.getPlayer(userId).getInventory().getItem(subItemIndex);
            return item;
        }else{
            return null;
        }
    }

    public int itemDmg(String input) {
        Item item = getItemAtkString(input);
        int dmgMod = item.getDmg();

        return dmgMod;
    }

    public void bleed(Player target) {
        new Thread() {
            public void run() {
                boolean bleeding = true;
                while(bleeding)
                {
                    Random rand = new Random();
                    int randEnd = rand.nextInt(10);
                    int hp = target.getHP();
                    hp -= 1;
                    target.setHP(hp);
                    if (hp <= 0) {
                        killUserOff(target);
                        bleeding = false;
                    } else {
                        target.getUserStream().printToUser("you are bleeding find a medKit");
                        if (randEnd == 1) {
                            bleeding = false;
                            UserStream tUserStream = target.getUserStream();
                            tUserStream.printToUser("you are no longer bleeding you have survived");
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
        int randBreak = rand.nextInt(1);
        if (randBreak == 0){
            String itemName = item.getName();
            String itemComp = "atk jim with "+ itemName;
            int targetHP = target.getHP();
            targetHP -= itemDmg(itemComp);
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

    /**
     * NPCs Attacking PCs
     * @param Target the PC being targetted by the attack
     * @param npc the NPC that is attacking
     */
    public void NPCAtk(World world, Player Target, NPC npc) {
        Random rand = new Random();
        int randInt = rand.nextInt(100);
        if (randInt <= npc.getAtkChance()) {
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

    /**
     * aggravating an NPC
     * this causes the NPC to increase in likelihood to attack
     * then immediately rolls for a chance to attack straight away
     * @param Target the PC being targetted by the attack
     * @param npc the NPC that is attacking
     */
    public void agrNPCAtk(World world, Player Target, NPC npc) {
        Random agrRand = new Random();
        int agrRandInt = agrRand.nextInt(100);
        double agrAtkChance = npc.getAtkChance() * npc.getAtkChanceMult();
        if (agrRandInt <= agrAtkChance) {
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

    /**
     * an NPC but this is used when the NPC has a weapon in their inventory
     * @param Target the PC being targetted by the attack
     * @param npc the NPC that is attacking
     * @return true or false to say whether the NPC still needs to atk or not
     */
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

