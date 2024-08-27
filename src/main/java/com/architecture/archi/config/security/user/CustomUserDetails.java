package com.architecture.archi.config.security.user;

import com.architecture.archi.db.entity.user.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public UserEntity getUser() {
        return user;
    }

    /**
     * 해당 유저의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_" + user.getRole().toString());

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPw();
    }

    /**
     * PK값
     */
    @Override
    public String getUsername() {
        return user.getId();
    }

    /**
     * 계정 만료 여부
     * true : 만료 안됨
     * false : 만료
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부
     * true : 잠기지 않음
     * false : 잠김
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     * true : 만료 안됨
     * false : 만료
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자 활성화 여부
     * ture : 활성화
     * false : 비활성화
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
