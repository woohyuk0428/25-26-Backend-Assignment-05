package com.gdg.todolist.dto;

import com.gdg.todolist.domain.Provider;
import com.gdg.todolist.domain.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocalUserInfoDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private Provider provider;
    private String accessToken;
    private String refreshToken;

    @Builder
    public LocalUserInfoDto(Long id, String name, String email, String password, Role role, Provider provider, String accessToken, String refreshToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static void from(LocalUserInfoDto localUserInfoDto) {


    }

}
