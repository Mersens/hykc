package com.tuoying.hykc.entity;

public class QuestionEntity {
    private String question;
    private String answer;
    private boolean isShowed=false;
    public QuestionEntity(String question,String answer,boolean isShowed){
        this.question=question;
        this.answer=answer;
        this.isShowed=isShowed;

    }

    public boolean isShowed() {
        return isShowed;
    }

    public void setShowed(boolean showed) {
        isShowed = showed;
    }



    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
