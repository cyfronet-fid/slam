package pl.cyfronet.indigo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pl.cyfronet.indigo.bean.User;
import java.util.List;

/**
 * @author bwilk
 *
 */
@RepositoryRestResource
//@PreAuthorize("hasRole('ADMIN')")
public interface UserRepository extends CrudRepository<User, Long> {
    
// TODO bring back security 
//    @Override
//    @PreAuthorize("checkPolicy(@activities.get('LIST_USERS'))")
//    public Iterable<User> findAll();
//
//    @Override
//    @PreAuthorize("checkPolicyUser(#user, @activities.get('SAVE_USER'))")
//    public <S extends User> S save(S user);
//
//    @Override
//    @PreAuthorize("permitAll")
//    @PostAuthorize("checkPolicyUser(returnObject, @activities.get('VIEW_USER'))")
//    public User findOne(Long id);

    User findByEmail(@Param("email") String email);
    List<User> findByOrganisationName(@Param("organisationName") String organisationName);
}