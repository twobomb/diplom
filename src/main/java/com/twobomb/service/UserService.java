package com.twobomb.service;

        import com.twobomb.entity.Discipline;
        import com.twobomb.entity.Group;
        import com.twobomb.entity.Role;
        import com.twobomb.entity.User;
        import com.twobomb.repository.DiscinplineRepository;
        import com.twobomb.repository.UserRepository;
        import org.hibernate.Session;
        import org.hibernate.SessionFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Repository;
        import org.springframework.stereotype.Service;

        import javax.transaction.Transactional;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.function.Predicate;

@Service("UserService")
@Repository
@Transactional
public class UserService{
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }
    @Autowired
    DiscinplineRepository discinplineRepository;

    public List<User> getUserByRole(String role){
        List<User> users = userRepository.findAll();
        users.removeIf(x -> !x.getRole().getRole().equals(Role.TEACHER));
        return users;
    }

    public List<Discipline> getDisciplines(User user) {
        List<Discipline> res = new ArrayList<>();
//        Session session = sessionFactory.openSession();
        try {
            switch (user.getRole().getRole()) {
                case Role.ADMIN:
                    res = discinplineRepository.findAll();
                    break;
                case Role.STUDENT:
                    Group g = user.getPerson().getGroup();
//                    session.update(g);
                    res = g.getDisciplines();
                    break;
                case Role.TEACHER:
                    res = user.getPerson().getDisciplinesTeacher();
                    break;
                default:
                    try {
                        throw new Exception("Unknown user role");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
//            session.close();
        }
        return res;
    }
    public User getByLogin(String login) {
        User u = userRepository.findByLogin(login);
        return  u;
    }
    public List<User> getAll() {
        return userRepository.findAll();
    }


}
