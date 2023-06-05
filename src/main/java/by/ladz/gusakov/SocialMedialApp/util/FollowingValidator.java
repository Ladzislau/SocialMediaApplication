package by.ladz.gusakov.SocialMedialApp.util;

import by.ladz.gusakov.SocialMedialApp.exceptions.FollowingException;
import by.ladz.gusakov.SocialMedialApp.models.Following;
import by.ladz.gusakov.SocialMedialApp.repositories.FollowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FollowingValidator{

    private final FollowingRepository followingRepository;

    @Autowired
    public FollowingValidator(FollowingRepository followingRepository) {
        this.followingRepository = followingRepository;
    }

    public boolean validate(Following following) throws FollowingException {
        if(following.getPerson().equals(following.getFollowee())){
            throw new FollowingException("Ошибка! Нельзя подписаться на самого себя");
        }
        if(followingRepository.findByPersonAndFollowee(following.getPerson(), following.getFollowee()).isPresent()){
            throw new FollowingException("Ошибка! Вы уже являетесь подписчиком пользователя "
                    + following.getFollowee().getUsername());
        }
        return true;
    }
}
