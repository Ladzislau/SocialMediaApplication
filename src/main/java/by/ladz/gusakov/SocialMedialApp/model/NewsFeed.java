package by.ladz.gusakov.SocialMedialApp.model;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsFeed {

    private Person owner;

    private List<Publication> publications;

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }
}
