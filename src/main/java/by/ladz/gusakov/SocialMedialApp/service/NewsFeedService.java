package by.ladz.gusakov.SocialMedialApp.service;

import by.ladz.gusakov.SocialMedialApp.exception.IncorrectPageParametersException;
import by.ladz.gusakov.SocialMedialApp.model.Following;
import by.ladz.gusakov.SocialMedialApp.model.NewsFeed;
import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.model.Publication;
import by.ladz.gusakov.SocialMedialApp.exception.PersonNotAuthenticatedException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class NewsFeedService {

    private final EntityManager entityManager;

    private final NewsFeed newsFeed;

    private final PeopleService peopleService;

    @Autowired
    public NewsFeedService(EntityManager entityManager, NewsFeed newsFeed, PeopleService peopleService) {
        this.entityManager = entityManager;
        this.newsFeed = newsFeed;
        this.peopleService = peopleService;
    }

    public List<Publication> generateNewsFeedForCurrentUser(Integer page, Integer publicationsPerPage, Boolean sortByCreationTime)
            throws PersonNotAuthenticatedException, IncorrectPageParametersException {

        Person owner = peopleService.getCurrentUser();
        List<Person> following = getPersonFollowings(owner);

        List<Publication> allPublications = generatePublicationList(following);
        newsFeed.setPublications(allPublications);

        if (sortByCreationTime != null && sortByCreationTime)
            Collections.sort(allPublications);
        else
            Collections.shuffle(allPublications);

        if((page != null && publicationsPerPage != null)) {

            return splitNewsByPages(page, publicationsPerPage);
        }

        return allPublications;
    }

    private List<Publication> splitNewsByPages(int page, int publicationsPerPage) throws IncorrectPageParametersException {
        if(page < 1 || publicationsPerPage < 1)
            throw new IncorrectPageParametersException("Неправильно заданы параметры отображения ленты активностей!" +
                    " Проверьте правильность параметров page и publications_per_page");

        List<Publication> publications = newsFeed.getPublications();

        if(publications.isEmpty())
            return Collections.emptyList();

        double lastPage = (double) publications.size() / publicationsPerPage;

        if ((lastPage % 1) != 0) {
            lastPage++;
        }

        if(page > lastPage)
            throw new IncorrectPageParametersException("Неправильно заданы параметры отображения ленты активностей!" +
                    " Проверьте правильность параметра page");

        int startPosition = (publicationsPerPage * page) - publicationsPerPage;
        int endPosition = startPosition + publicationsPerPage;

        if(endPosition > publications.size())
            endPosition = publications.size();

        List<Publication> publicationsForPage = new ArrayList<>();
        for (int i = startPosition; i < endPosition; i++) {
            Publication publication = publications.get(i);
            publicationsForPage.add(publication);
        }

        return publicationsForPage;
    }

    private List<Person> getPersonFollowings(Person person){
        Session session = entityManager.unwrap(Session.class);
        TypedQuery<Following> query = session.createQuery(
                        "SELECT f " +
                                "FROM Following f " +
                                "JOIN FETCH f.followee " +
                                "WHERE f.person = :person", Following.class)
                .setParameter("person", person);
        List<Following> followingRequests = query.getResultList();
        List<Person> followingPeople = new ArrayList<>();
        for (Following following :
                followingRequests) {
            followingPeople.add(following.getFollowee());
        }
        return followingPeople;
    }

    private List<Publication> generatePublicationList(List<Person> following){
        Session session = entityManager.unwrap(Session.class);
        TypedQuery<Person> query = session.createQuery(
                        "SELECT DISTINCT p " +
                                "FROM Person p " +
                                "LEFT JOIN FETCH p.publications pub " +
                                "WHERE p IN :following", Person.class)
                .setParameter("following", following);
        List<Person> followingWithPublications = query.getResultList();

        List<Publication> publicationList = new ArrayList<>();
        for (Person followee :
        followingWithPublications) {
            List<Publication> followeePublications = followee.getPublications();
            publicationList.addAll(followeePublications);
        }
        return publicationList;
    }

}
