package ru.armlix.winty.game.entiy.living;

import ru.armlix.winty.game.entiy.IDAllocator;
import ru.armlix.winty.network.netty.tcp.ChannelWrapper;
import ru.armlix.winty.network.netty.tcp.packet.game.player.data.Packet8SetHealth;


public class NetworkHumanEntity extends HumanEntity {

    private ChannelWrapper channel;

    public NetworkHumanEntity(IDAllocator<Integer> alloc, ChannelWrapper channel) {
        super(alloc);
        this.channel = channel;
    }

    @Override
    public void kill() {
        super.kill();
        updateClientHealth();
    }

    public void damage(double amount) {
        super.damage(amount);
        updateClientHealth();
    }

    public void heal(double amount) {
        super.heal(amount);
        updateClientHealth();
    }

    @Override
    public void setHealth(double amount) {
        super.setHealth(amount);
        updateClientHealth();
    }

    private void updateClientHealth() {
        channel.write(new Packet8SetHealth(((short) getCurrentHealth())));
    }

}
