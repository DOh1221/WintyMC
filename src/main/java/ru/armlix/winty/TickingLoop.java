package ru.armlix.winty;

import lombok.AllArgsConstructor;
import ru.armlix.winty.game.world.World;

@AllArgsConstructor
public class TickingLoop extends Thread {

    World world;

    @Override
    public void run() {
        while (true) {
            world.tick();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
