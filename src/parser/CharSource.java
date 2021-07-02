package parser;

public interface CharSource {
    boolean hasNext();

    char next();

    int getPosition();
}