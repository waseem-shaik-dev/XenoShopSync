package com.example.XenoShopSync.authService;

import com.example.XenoShopSync.dto.OtpVerificationDto;
import com.example.XenoShopSync.dto.UserLoginDto;
import com.example.XenoShopSync.dto.UserRegistrationDto;
import com.example.XenoShopSync.dto.TenantRequestDto;
import com.example.XenoShopSync.entity.RegistrationToken;
import com.example.XenoShopSync.entity.Tenant;
import com.example.XenoShopSync.entity.User;
import com.example.XenoShopSync.enums.Role;
import com.example.XenoShopSync.enums.UserStatus;
import com.example.XenoShopSync.repository.RegistrationTokenRepository;
import com.example.XenoShopSync.repository.TenantRepository;
import com.example.XenoShopSync.repository.UserRepository;
import com.example.XenoShopSync.utility.EmailService;
import com.example.XenoShopSync.utility.OtpUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RegistrationTokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${app.jwt.access-minutes}")
    private long accessMinutes;

    /**
     * Register a tenant user along with a tenant entity
     */
    public String registerTenant(UserRegistrationDto dto) throws MessagingException {

        if(userRepository.existsByEmail(dto.email())){
            throw new RuntimeException("user with email "+dto.email()+" already exists!");
        }

        TenantRequestDto t = dto.tenant();
        if (t == null) {
            throw new IllegalArgumentException("Tenant details are required for TENANT registration");
        }


        // 1️⃣ Check tenantId uniqueness
        boolean tenantExists = tenantRepository.findByTenantId(dto.tenant().tenantId()).isPresent();
        if (tenantExists) {
            throw new RuntimeException("Tenant ID already exists. Please choose a different tenant ID.");
        }

        boolean baseUrlExists = tenantRepository.existsByShopifyBaseUrl(dto.tenant().shopifyBaseUrl());
        if(baseUrlExists){
            throw new RuntimeException("Tenant with base url:"+dto.tenant().shopifyBaseUrl()+" already exists. Please choose a different Shopify Base URL.");
        }

        // 2️⃣ Validate access token
        boolean tokenValid = validateShopifyAccessToken(dto.tenant().shopifyBaseUrl(), dto.tenant().accessToken());
        if (!tokenValid) {
            throw new RuntimeException("Access token OR  Shopify store URL is invalid ");
        }




        User user = User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(Role.TENANT)
                .status(UserStatus.PENDING)
                .build();

        Tenant tenant = Tenant.builder()
                .tenantId(t.tenantId())
                .shopifyBaseUrl(t.shopifyBaseUrl())
                .accessToken(t.accessToken())
                .shopName(t.shopName())
                .user(user)
                .build();

         tenantRepository.save(tenant);



         userRepository.save(user);

        sendOtp(user);

         return "Registration started. OTP sent to your email.";
    }


    public boolean validateShopifyAccessToken(String shopifyBaseUrl, String accessToken) {
        try {
            WebClient client = WebClient.builder()
                    .baseUrl(shopifyBaseUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader("X-Shopify-Access-Token", accessToken)
                    .build();

            String response = client.get()
                    .uri("/shop.json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // blocking for simplicity

            return response != null && !response.isEmpty();
        } catch (Exception e) {
            return false; // token invalid or base URL incorrect
        }
    }


    // Step 2: Verify OTP
    public void verifyOtp(OtpVerificationDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RegistrationToken token = tokenRepository.findByUserAndOtp(user, dto.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        tokenRepository.delete(token); // cleanup
    }


    public void resendOtp(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != UserStatus.PENDING) {
            throw new RuntimeException("User already verified");
        }

        sendOtp(user);
    }



    private void sendOtp(User user) throws MessagingException {
        String otp = OtpUtil.generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        RegistrationToken token = tokenRepository.findByUser(user)
                .orElse(new RegistrationToken());
        token.setUser(user);
        token.setOtp(otp);
        token.setExpiry(expiry);

        tokenRepository.save(token);

        emailService.sendRegistrationOtpEmail(user.getEmail(), otp);
    }


    /**
     * Register an admin (no tenant)
     */
    public User registerAdmin(UserRegistrationDto dto) {

        if(userRepository.existsByEmail(dto.email())){
            throw new RuntimeException("user with email "+dto.email()+" already exists!");
        }


        User user = User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    /**
     * Login user → authenticate + generate JWT + set cookie
     */
    public LoginResponse login(UserLoginDto dto, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtService.generateAccessToken(user.getEmail(), user.getRole());

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) // change to true in prod (HTTPS)
                .path("/auth/refresh")
                .maxAge(accessMinutes * 60)
                .sameSite("None")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());



        String tenantId = tenantRepository.findById(user.getId())
                .map(Tenant::getTenantId)
                .orElse(null);



        String role = "is" + user.getRole().name().substring(0, 1).toUpperCase()
                + user.getRole().name().substring(1).toLowerCase();

        return new LoginResponse(
                user.getId(),
                tenantId,
                token,
                "Bearer",
                user.getEmail(),
                role,
                accessMinutes * 60
        );
    }

    /**
     * Inner DTO for Login response
     */
    public record LoginResponse(
            Long userId,
            String tenantId,
            String accessToken,
            String tokenType,
            String email,
            String role,
            long expiresIn
    ) {}
}
