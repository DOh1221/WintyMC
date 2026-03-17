package ru.armlix.winty.game.entiy;

import lombok.Getter;

@Getter
public class LivingEntity extends Entity {

    private double currentHealth = 20.0;
    private double maxHealth = 20.0;
    private double healthRestore = 0.0; // количество здоровья, добавляемое при срабатывании
    private long ticksPerHealthRestoring = -1; // <=0 -> восстановление отключено
    private long ticksPassed = 0;
    private boolean dead = false;

    private Entity attacker = null;

    public LivingEntity(IDAllocator<Integer> alloc) {
        super(alloc);
    }

    @Override
    public void tick(long deltaTicks) {
        super.tick(deltaTicks);

        if (dead) {
            ticksPassed = 0;
            return;
        }

        if (ticksPerHealthRestoring > 0 && healthRestore > 0.0) {
            ticksPassed += 1;
            if (ticksPassed >= ticksPerHealthRestoring ) {
                double newHealth = currentHealth + healthRestore;
                currentHealth = Math.min(maxHealth, newHealth);
            }
        }
    }

    public void damage(double amount) {
        if (dead) return;
        currentHealth -= amount;
        if (currentHealth <= 0.0) {
            currentHealth = 0.0;
            dead = true;
            ticksPassed = 0;
            onDeath();
        }
    }

    public synchronized void heal(double amount) {
        if (dead) return;
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    protected void onDeath() {

    }

}