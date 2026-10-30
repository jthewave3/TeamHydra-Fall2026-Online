package Model;

/** What an item does when used. WEAPON and KEY are not consumed in combat; the rest are potions. */
public enum ItemEffect {
    WEAPON,
    KEY,
    /** Restores value% of the player's max HP. */
    HEAL_PERCENT,
    /** Removes value% of the monster's HP (100 = the monster disappears). */
    MONSTER_DAMAGE_PERCENT,
    /** Reduces incoming monster damage by value% for duration turns. */
    SHIELD_PERCENT,
    /** Increases the player's damage by value% for duration turns. */
    DAMAGE_BOOST_PERCENT,
    /** The monster cannot attack for duration turns. */
    STUN
}
