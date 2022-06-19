package br.com.quarkus.social.domain.repository;

import br.com.quarkus.social.domain.model.Follower;
import br.com.quarkus.social.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        var params = Parameters.with("follower", follower)
                .and("user", user)
                .map();

        var query = find("followerId = :follower and user = :user", params);

        return query.firstResultOptional().isPresent();
    }
}
