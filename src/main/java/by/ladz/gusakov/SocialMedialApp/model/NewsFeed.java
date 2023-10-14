package by.ladz.gusakov.SocialMedialApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
@Getter
@Setter
public class NewsFeed {

    private Person owner;

    private List<Publication> publications;
}
