package ecs.ecs.entities;

/**
 * these states are used to flag entities or components
 * e.g. render-system and garbage-collection will only work
 * on entities which are buffered and flagged
 */
public enum State {
    UPDATE("flag update required"),
    STABLE("no action required"),
    DELETE("flag to delete");

    State(String description) {
    }
}
