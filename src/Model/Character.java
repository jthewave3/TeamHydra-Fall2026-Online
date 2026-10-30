package Model;

/** Shared by the player and monsters: a name and a health pool. */
public abstract class Character {
    private final String name;
    private final int maxHealth;
    private int health;

    protected Character(String name, int maxHealth) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isDead() { return health <= 0; }
    public void takeDamage(int amount) { health = Math.max(0, health - amount); }
    public void heal(int amount) { health = Math.min(maxHealth, health + amount); }
}
