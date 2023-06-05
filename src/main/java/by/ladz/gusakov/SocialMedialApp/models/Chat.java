package by.ladz.gusakov.SocialMedialApp.models;

import java.util.Date;
import java.util.Map;

public class Chat {

    private String firstChatMemberName;

    private String secondChatMemberName;

    private Map<Date, String> messages;

    public Chat() {
    }

    public Chat(String firstChatMemberName, String secondChatMemberName) {
        this.firstChatMemberName = firstChatMemberName;
        this.secondChatMemberName = secondChatMemberName;
    }

    public String getFirstChatMemberName() {
        return firstChatMemberName;
    }

    public void setFirstChatMemberName(String firstChatMemberName) {
        this.firstChatMemberName = firstChatMemberName;
    }

    public String getSecondChatMemberName() {
        return secondChatMemberName;
    }

    public void setSecondChatMemberName(String secondChatMemberName) {
        this.secondChatMemberName = secondChatMemberName;
    }

    public Map<Date, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<Date, String> messages) {
        this.messages = messages;
    }
}
