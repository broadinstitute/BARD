package bard.core.interfaces;

public interface Standardizable<T> {
    T standardized(); // return standardized value

    T original(); // return original value

    T preferred(); // return preferred value (e.g., for display purposes)
}
