package by.ladz.gusakov.SocialMedialApp.service;

import by.ladz.gusakov.SocialMedialApp.model.Friendship;
import by.ladz.gusakov.SocialMedialApp.model.Person;
import by.ladz.gusakov.SocialMedialApp.model.Following;
import by.ladz.gusakov.SocialMedialApp.repository.FollowingRepository;
import by.ladz.gusakov.SocialMedialApp.repository.FriendshipRepository;
import by.ladz.gusakov.SocialMedialApp.exception.FollowingException;
import by.ladz.gusakov.SocialMedialApp.exception.PersonNotAuthenticatedException;
import by.ladz.gusakov.SocialMedialApp.util.FollowingValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {

    private final PeopleService peopleService;

    private final FriendshipRepository friendshipRepository;

    private final FollowingRepository followingRepository;

    private final FollowingValidator followingValidator;


    @Autowired
    public FriendRequestService(PeopleService peopleService, FriendshipRepository friendshipRepository,
                                FollowingRepository followingRepository, FollowingValidator followingValidator) {

        this.peopleService = peopleService;
        this.friendshipRepository = friendshipRepository;
        this.followingRepository = followingRepository;
        this.followingValidator = followingValidator;
    }

    @Transactional
    public void follow(Person followee) throws PersonNotAuthenticatedException, FollowingException {
        Person currentUser = peopleService.getCurrentUser();

        Following following = new Following(currentUser, followee, Following.Status.SENT);

        if (followingValidator.validate(following)) {
            if (establishMutualFriendship(currentUser, followee))
                following.setStatus(Following.Status.ACCEPTED);
            followingRepository.save(following);
        }
    }

    @Transactional
    public void acceptRequest(Person follower) throws PersonNotAuthenticatedException, FollowingException {
        Person currentUser = peopleService.getCurrentUser();
        Optional<Following> followingOptional = followingRepository.findByPersonAndFollowee(follower, currentUser);

        if (followingOptional.isEmpty()) {
            throw new FollowingException("Пользователь " + follower.getUsername()
                    + " не подписан на вас! Чтобы стать друзьями вы можете подписаться на него и подождать, "
                    + "пока он примет вашу заявку");
        }

        Following followerFollowing = followingOptional.get();
        followerFollowing.setStatus(Following.Status.ACCEPTED);

        Following currentUserFollowing = new Following(currentUser, follower, Following.Status.ACCEPTED);

        followingRepository.save(followerFollowing);
        followingRepository.save(currentUserFollowing);

        establishMutualFriendship(currentUser, follower);
    }

    @Transactional
    public void unfollow(Person followee) throws PersonNotAuthenticatedException, FollowingException {
        Person currentUser = peopleService.getCurrentUser();

        Optional<Following> currentUserFollowingOptional = followingRepository.findByPersonAndFollowee(currentUser, followee);
        if (currentUserFollowingOptional.isEmpty()) {
            throw new FollowingException("Вы не можете отписаться от пользователя " + followee.getUsername() +
                    ", потому что не подписаны на него");
        }

        List<Friendship> friendships = findFriendship(currentUser, followee);
        if(!friendships.isEmpty()){
            friendshipRepository.deleteAll(friendships);
            Optional<Following> followeeFollowingOpt = followingRepository.findByPersonAndFollowee(followee, currentUser);
            Following followeeFollowing = followeeFollowingOpt.get();
            followeeFollowing.setStatus(Following.Status.DECLINED);
            followingRepository.save(followeeFollowing);
        }

        followingRepository.delete(currentUserFollowingOptional.get());
    }

    @Transactional
    public void declineRequest(Person follower) throws PersonNotAuthenticatedException, FollowingException {
        Person currentUser = peopleService.getCurrentUser();

        Optional<Following> followingOptional = followingRepository.findByPersonAndFollowee(follower, currentUser);
        if (followingOptional.isEmpty()) {
            throw new FollowingException("Пользователь " + follower.getUsername()
                    + " не подписан на вас! Вы не можете отклонить заявку");
        }

        Following followerFollowing = followingOptional.get();
        followerFollowing.setStatus(Following.Status.DECLINED);

        followingRepository.save(followerFollowing);
    }

    public List<Friendship> findFriendship(Person person1, Person person2) {
        List<Friendship> friendshipList = new ArrayList<>();

        Optional<Friendship> firstFriendship = friendshipRepository.findByPersonAndFriend(person1, person2);
        Optional<Friendship> secondFriendship = friendshipRepository.findByPersonAndFriend(person2, person1);
        firstFriendship.ifPresent(friendshipList::add);
        secondFriendship.ifPresent(friendshipList::add);

        return friendshipList;
    }

    public boolean establishMutualFriendship(Person currentUser, Person followee) {
        Optional<Following> followingToCurrentUser = followingRepository.findByPersonAndFollowee(followee, currentUser);
        if (followingToCurrentUser.isPresent()) {
            Friendship friendship1 = new Friendship(currentUser, followee);
            Friendship friendship2 = new Friendship(followee, currentUser);
            friendshipRepository.save(friendship1);
            friendshipRepository.save(friendship2);
            return true;
        }
        return false;
    }
}
