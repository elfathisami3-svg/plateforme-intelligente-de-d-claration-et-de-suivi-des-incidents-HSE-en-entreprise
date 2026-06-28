package hse.model;

public enum Gravite {
    FAIBLE(1),
    MOYENNE(2),
    ELEVEE(3),
    CRITIQUE(5);

    private final int score;

    Gravite(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}

