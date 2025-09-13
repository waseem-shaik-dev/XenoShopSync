package com.example.XenoShopSync.authService;

import com.example.XenoShopSync.auth.UserPrincipal;
import com.example.XenoShopSync.entity.User;
import com.example.XenoShopSync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Prefix with "ROLE_" is handled by SimpleGrantedAuthority convention
        return new UserPrincipal(user);
    }


}
