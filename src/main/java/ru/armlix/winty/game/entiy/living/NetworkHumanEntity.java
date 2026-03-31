package ru.armlix.winty.game.entiy.living;

import java.util.Objects;

import ru.armlix.winty.game.entiy.IDAllocator;
import ru.armlix.winty.network.netty.tcp.ChannelWrapper;
import ru.armlix.winty.network.netty.tcp.packet.game.player.data.Packet8SetHealth;
import ru.armlix.winty.network.netty.tcp.packet.game.world.Packet4WorldTime;

public class NetworkHumanEntity extends HumanEntity {

    private static final short CLIENT_MAX_HEALTH = 20;
    private static final short DAMAGE_FLASH_HEALTH = 19;

    private final ChannelWrapper channel;

    public NetworkHumanEntity(IDAllocator<Integer> alloc, ChannelWrapper channel) {
        super(alloc);
        this.channel = Objects.requireNonNull(channel, "channel");
    }

    @Override
    public void tick(long deltaTicks) {
        super.tick(deltaTicks);
        if(previousTickEntityTime != entityTime) sendTimeUpdate();
    }

    @Override
    public void kill() {
        super.kill();
        updateClientHealth(false);
    }

    @Override
    public void damage(double amount) {
        super.damage(amount);
        updateClientHealth(getCurrentHealth() > CLIENT_MAX_HEALTH);
    }

    @Override
    public void heal(double amount) {
        super.heal(amount);
        updateClientHealth(false);
    }

    @Override
    public void setHealth(double amount) {
        super.setHealth(amount);
        updateClientHealth(false);
    }

    private void updateClientHealth(boolean flashDamage) {
        if (flashDamage) {
            sendHealth(DAMAGE_FLASH_HEALTH);
        }

        sendHealth(clampClientHealth());
    }

    private short clampClientHealth() {
        long rounded = Math.round(getCurrentHealth());
        long clamped = Math.max(0, Math.min(CLIENT_MAX_HEALTH, rounded));
        return (short) clamped;
    }

    public void sendHealth(short health) {
        channel.write(new Packet8SetHealth(health));
    }

    public void sendTimeUpdate() {
        channel.write(new Packet4WorldTime(getEntityTime()));
    }

}