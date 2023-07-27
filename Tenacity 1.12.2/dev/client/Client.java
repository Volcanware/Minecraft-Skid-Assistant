package dev.client;

import dev.client.ClientType;
import dev.client.tenacity.Tenacity;
import dev.event.Event;

public class Client {
    public static ClientType client;

    public static void dispatchEvent(Event event) {
        if (client == null) {
            return;
        }
        switch (client) {
            case TENACITY: {
                Tenacity.INSTANCE.getEventProtocol().dispatch(event);
                break;
            }
        }
    }
}