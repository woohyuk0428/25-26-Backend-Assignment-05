package com.gdg.todolist.service;

import com.gdg.todolist.domain.Provider;
import com.gdg.todolist.domain.Role;
import com.gdg.todolist.domain.User;
import com.gdg.todolist.dto.LocalUserInfoDto;
import com.gdg.todolist.dto.TokenDto;
import com.gdg.todolist.dto.LocalUserSignUpDto;
import com.gdg.todolist.exception.UserNotFoundException;
import com.gdg.todolist.jwt.TokenProvider;
import com.gdg.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class LocalAuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenDto adminSignUp(LocalUserSignUpDto localUserSignUpDto) {
        User user = userRepository.save(User.builder()
                .name(localUserSignUpDto.getName())
                .email(localUserSignUpDto.getEmail())
                .password(passwordEncoder.encode(localUserSignUpDto.getPassword()))
                .role(Role.ROLE_ADMIN)
                .provider(Provider.LOCAL)
                .build()
        );

        String accessToken = tokenProvider.createAccessToken(user);
        String refreshToken = tokenProvider.createRefreshToken(user);

        user.saveAccessToken(accessToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public TokenDto userSignUp(LocalUserSignUpDto localUserSignUpDto) {
        User user = userRepository.save(User.builder()
                .name(localUserSignUpDto.getName())
                .email(localUserSignUpDto.getEmail())
                .password(passwordEncoder.encode(localUserSignUpDto.getPassword()))
                .role(Role.ROLE_USER)
                .provider(Provider.LOCAL)
                .build()
        );

        String accessToken = tokenProvider.createAccessToken(user);
        String refreshToken = tokenProvider.createRefreshToken(user);

        user.saveAccessToken(accessToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public TokenDto refresh(Long id){
        User user = entityUserId(id);

        String refreshToken = tokenProvider.createRefreshToken(user);

        user.saveAccessToken(refreshToken);

        return TokenDto.builder()
                .accessToken(user.getAccessToken())
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public LocalUserInfoDto getMyInfo(Principal principal){
        User user = entityUserId(Long.parseLong(principal.getName()));

        return LocalUserInfoDto.from(user);
    }

    @Transactional(readOnly = true)
    public LocalUserInfoDto getUserInfo(Long id){
        User user = entityUserId(id);

        return LocalUserInfoDto.from(user);
    }

    @Transactional
    public LocalUserInfoDto updateUserInfo(Principal principal, LocalUserSignUpDto localUserSignUpDto){
        User user = entityUserId(Long.parseLong(principal.getName()));
        user.updateInfo(
                localUserSignUpDto.getName() == null ? user.getName() : localUserSignUpDto.getName(),
                localUserSignUpDto.getEmail() == null ? user.getEmail() : localUserSignUpDto.getEmail(),
                localUserSignUpDto.getPassword() == null ? user.getPassword() : passwordEncoder.encode(localUserSignUpDto.getPassword())
        );

        return LocalUserInfoDto.from(user);
    }

    @Transactional
    public void deleteUser(Principal principal){
        userRepository.deleteById(Long.parseLong(principal.getName()));
    }

    private User entityUserId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자가 없습니다."));
    }
}
