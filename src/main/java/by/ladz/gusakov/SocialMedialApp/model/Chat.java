package by.ladz.gusakov.SocialMedialApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class Chat {

    private String firstChatMemberName;

    private String secondChatMemberName;

    private Map<Date, String> messages;

    public Chat(String firstChatMemberName, String secondChatMemberName) {
        this.firstChatMemberName = firstChatMemberName;
        this.secondChatMemberName = secondChatMemberName;
    }
}
