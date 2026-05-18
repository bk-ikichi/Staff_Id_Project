package org.example.ikichi_staffcard_project.auth.config;

import org.example.ikichi_staffcard_project.auth.UserAuthRepository;
import org.example.ikichi_staffcard_project.dto.User;
import org.example.ikichi_staffcard_project.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserAuthRepository userAuthRepository;

    public CustomUserDetailsService(UserRepository userRepository, UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String staffId) throws UsernameNotFoundException {
        User user = userAuthRepository.findByStaffId(staffId);
        if (user == null) {
            throw new UsernameNotFoundException(staffId +  " not found");
        }
        return new CustomUserDetails(user);
    }
}
