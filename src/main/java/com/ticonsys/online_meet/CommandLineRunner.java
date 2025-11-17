package com.ticonsys.online_meet;

import com.ticonsys.online_meet.model.user.User;
import com.ticonsys.online_meet.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {

        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            System.out.println("Testing User Data Save");
            User user = new User();
            user.setProfileName("Rashedul Islam");
            user.setGender("Male");
            user.setPhone("01571797518");
            user.setUsername("rashedul");
            user.setPassword(encoder.encode("123456"));
            user.setIsActive(true);
            userRepository.save(user);
        }
        else {
            System.out.println("Data already Save");

        }
    }

}
