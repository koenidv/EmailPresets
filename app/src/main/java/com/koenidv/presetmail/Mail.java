package com.koenidv.presetmail;

//  Created by koenidv on 28.04.2020.
public class Mail {

    private String[] recipients;
    private String subject, body, name;

    Mail() {
    }

    Mail(String[] mRecipients, String mSubject, String mName, String mBody) {
        recipients = mRecipients;
        subject = mSubject;
        body = mBody;
        name = mName;
    }

    String[] getRecipients() {
        return recipients;
    }

    void setRecipients(String[] mRecipients) {
        recipients = mRecipients;
    }

    String getSubject() {
        return subject;
    }

    void setSubject(String mSubject) {
        subject = mSubject;
    }

    String getBody() {
        return body;
    }

    void setBody(String mBody) {
        body = mBody;
    }

    String getName() {
        if (name == null || name.isEmpty())
            return subject;
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }
}
