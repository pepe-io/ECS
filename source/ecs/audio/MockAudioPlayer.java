package ecs.audio;

import ecs.event.GameEventObserver;
import ecs.event.GameEvent;
import ecs.settings.Settings;

/**
 * mock audio player is an example of an event-observer
 */
public class MockAudioPlayer implements GameEventObserver {

    // settings
    private boolean playSounds = Settings.getPlaySounds();

    public MockAudioPlayer() {

    }


    /**
     * play sounds on game-events
     *
     * @param event
     *      event
     */
    @Override
    public void getNotification(GameEvent event) {
        if(playSounds) System.out.println("MockAudioPlayer: " + event.getEventType());
    }
}
