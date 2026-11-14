package Model; // all the game data classes live in this package

import java.util.ArrayList; // list of messages
import java.util.Random; // random numbers

public class Combat { // one fight between the player and a monster
    private Player player; // the player
    private Monster monster; // the monster
    private Random rand; // random numbers for the fight
    private int shieldTurns; // monster attacks the shield still blocks
    private int cloneTurns; // attacks that still get doubled
    private int stunTurns; // monster turns that still get skipped
    private boolean over; // true when the fight ended
    private boolean won; // true if the monster is dead
    private boolean fled; // true if the player ran away

    public Combat(Player player, Monster monster, Random rand) { // constructor
        this.player = player; // save the player
        this.monster = monster; // save the monster
        this.rand = rand; // save the random object
    }

    public ArrayList<String> attack() { // player attacks with the equipped weapon
        ArrayList<String> log = new ArrayList<String>(); // messages to show
        Weapon weapon = player.getEquipped(); // weapon being used
        if (weapon == null) { // nothing equipped
            log.add("You need a weapon to attack."); // tell the player
            monsterTurn(log); // monster still attacks
            return log; // done
        }
        if (weapon.isSlow() && weapon.isResting()) { // fire axe is still recovering
            weapon.setResting(false); // ready next turn
            log.add("You are still recovering from your last swing."); // tell the player
            monsterTurn(log); // monster attacks
            return log; // done
        }
        if (!weapon.canAttack()) { // bow with no arrows
            log.add("Your " + weapon.getName() + " is out of arrows. Equip something else."); // tell the player
            monsterTurn(log); // monster attacks
            return log; // done
        }
        int damage = weapon.rollDamage(rand); // roll damage
        if (cloneTurns > 0) { // clone potion is active
            damage = damage * 2; // double damage
            cloneTurns--; // one less turn of clone
            log.add("Your clone attacks with you!"); // tell the player
        }
        weapon.useOnce(); // use up a hit or arrow
        if (weapon.isSlow()) weapon.setResting(true); // axe needs to rest next turn
        monster.takeDamage(damage); // hurt the monster
        log.add("You hit " + monster.getName() + " for " + damage + " damage."); // tell the player
        log.add(weapon.getName() + ": " + weapon.getStatus()); // show durability or arrows
        if (weapon.isBroken()) { // weapon wore out
            log.add("Your " + weapon.getName() + " breaks. You'll need to find another weapon."); // tell the player
            player.breakEquipped(); // remove it
        }
        if (!monster.isAlive()) { // monster died
            over = true; // fight is over
            won = true; // player won
            log.add(monster.getWinText()); // show the win text
            return log; // no monster turn
        }
        monsterTurn(log); // monster attacks back
        return log; // done
    }

    public ArrayList<String> useItem(Consumable item) { // player uses a potion or the flash bomb
        ArrayList<String> log = new ArrayList<String>(); // messages to show
        String effect = item.getEffect(); // what the item does
        if (effect.equals("vanish")) { // vanishing potion
            monster.takeDamage(monster.getHp()); // monster loses all its hp
            over = true; // fight is over
            won = true; // player won
            log.add(monster.getName() + " vanishes into thin air."); // tell the player
            return log; // no monster turn
        }
        if (effect.equals("shrink")) { // shrinking potion
            monster.takeDamage(monster.getHp() / 2); // take away half of its hp
            log.add(monster.getName() + " shrinks and loses half of its health."); // tell the player
        }
        if (effect.equals("heal")) { // healing potion
            player.heal(player.getMaxHp() / 4); // heal 25%
            log.add("You drink the potion and heal 25%. Your HP: " + player.getHp()); // tell the player
        }
        if (effect.equals("shield")) { // shield potion
            shieldTurns = 2; // lasts 2 attacks
            log.add("A shield surrounds you for 2 turns."); // tell the player
        }
        if (effect.equals("clone")) { // clone potion
            cloneTurns = 2; // lasts 2 attacks
            log.add("A clone appears. Your damage is doubled for 2 turns."); // tell the player
        }
        if (effect.equals("stun")) { // flash bomb
            stunTurns = 2; // monster is stunned for 2 turns
            log.add(monster.getName() + " is stunned for 2 turns."); // tell the player
        }
        if (!monster.isAlive()) { // shrinking can't kill but just in case
            over = true; // fight is over
            won = true; // player won
            log.add(monster.getWinText()); // show the win text
            return log; // no monster turn
        }
        monsterTurn(log); // monster attacks
        return log; // done
    }

    public ArrayList<String> flee() { // player tries to run away
        ArrayList<String> log = new ArrayList<String>(); // messages to show
        if (monster.isBoss()) { // can't run from the boss
            log.add(monster.getFleeText()); // "There is no leaving this room."
            return log; // doesn't use up the turn
        }
        if (rand.nextInt(100) < 50) { // 50% chance to get away
            over = true; // fight is over
            fled = true; // player ran
            log.add("Attempting to flee! Flee successful."); // tell the player
            log.add(monster.getFleeText()); // show the flee text
            return log; // done
        }
        log.add("You try to flee but the monster blocks your path!"); // failed
        monsterTurn(log); // monster attacks
        return log; // done
    }

    private void monsterTurn(ArrayList<String> log) { // monster attacks the player
        if (stunTurns > 0) { // monster is stunned
            stunTurns--; // one less turn of stun
            log.add(monster.getName() + " is stunned and can't attack!"); // tell the player
            return; // skip the attack
        }
        int damage = monster.rollDamage(rand); // roll damage
        if (shieldTurns > 0) { // shield is up
            damage = damage * 3 / 4; // 25% less damage
            shieldTurns--; // one less turn of shield
        }
        player.takeDamage(damage); // hurt the player
        log.add(monster.getName() + " hits you for " + damage + " damage."); // tell the player
        if (!player.isAlive()) { // player died
            over = true; // fight is over
        }
    }

    public Monster getMonster() { // gets the monster
        return monster; // send it back
    }

    public boolean isOver() { // gets over flag
        return over; // send it back
    }

    public boolean isWon() { // gets won flag
        return won; // send it back
    }

    public boolean hasFled() { // gets fled flag
        return fled; // send it back
    }
}
