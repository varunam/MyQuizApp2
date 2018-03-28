package app.myquizapp.com.myquizapp.model;

/**
 * Created by vaam on 3/27/2018.
 */

public class Question {

    private String question;
    private String[] options = new String[4];
    private int solution;

    public Question(String question, String[] options, int solution)
    {
        this.question = question;
        this.options = options;
        this.solution = solution;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getSolution() {
        return solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }
}
