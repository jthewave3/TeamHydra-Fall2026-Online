package Model;

public class Puzzle {
    private final String question;
    private final String answers;
    private final String[] accepted;
    private boolean solved;

    // answers: accepted answers separated by "/"
    public Puzzle(String question, String answers) {
        this.question = question;
        this.answers = answers;
        this.accepted = answers.toLowerCase().split("/");
    }

    /** A fresh, unsolved copy. */
    public Puzzle copy() { return new Puzzle(question, answers); }

    public String getQuestion() { return question; }
    public boolean isSolved() { return solved; }

    public boolean attempt(String guess) {
        String g = guess.trim().toLowerCase();
        for (String a : accepted) {
            if (g.equals(a)) {
                solved = true;
                break;
            }
        }
        return solved;
    }
}
