package ecs.event;

/**
 * event data types
 */
public enum EventData {
    CollisionUUID("UUID of collision"),
    ColliderUUID("UUID of collider"),
    VELOCITY("Point3D");

    EventData(String description) {

    }
}
