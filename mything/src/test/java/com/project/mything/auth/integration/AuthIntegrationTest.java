package com.project.mything.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mything.auth.dto.AuthDto;
import com.project.mything.auth.service.AuthNumSendService;
import com.project.mything.auth.service.PasswordService;
import com.project.mything.redis.repository.RedisRepository;
import com.project.mything.user.entity.Role;
import com.project.mything.user.entity.User;
import com.project.mything.user.entity.UserRole;
import com.project.mything.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mything.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(value = "jwt.secretKey=only_test_secret_Key_value_gn..rlfdlrkqnwhrgkekspdy")
@AutoConfigureMockMvc
@Transactional
public class AuthIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuthNumSendService authNumSendService;
    @MockBean
    RedisRepository redisRepository;
    @MockBean
    PasswordService passwordService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;

    private void addUserRole(User originalUser) {
        Role role = Role.builder().build();
        UserRole userRole = UserRole.builder().role(role).user(originalUser).build();
        role.getUserRoles().add(userRole);
        originalUser.getUserRoles().add(userRole);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void login_suc() throws Exception {
        //given
        User originalUser = REQUEST_USER;
        addUserRole(originalUser);
        userRepository.save(originalUser);

        given(passwordService.isNotEqualPassword(any(), any())).willReturn(false);

        String content = objectMapper.writeValueAsString(REQUEST_LOGIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("userId").value(originalUser.getId()))
                .andExpect(jsonPath("accessToken").exists());
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    public void login_fail() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(REQUEST_LOGIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void join() throws Exception {
        //given
        given(redisRepository.getData(anyString())).willReturn(Optional.of(AUTH_NUMBER));
        String content = objectMapper.writeValueAsString(REQUEST_JOIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("accessToken").exists());
    }

    @Test
    @DisplayName("회원가입 인증번호 받은 핸드폰과 다른 번호로 회원가입을 시도할 경우 실패 테스트")
    public void join_fail() throws Exception {
        //given
        given(redisRepository.getData(anyString())).willReturn(Optional.empty());
        String content = objectMapper.writeValueAsString(REQUEST_JOIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("회원가입 휴대폰 인증번호가 다를 경우 실패 테스트")
    public void join_fail2() throws Exception {
        //given
        given(redisRepository.getData(anyString())).willReturn(Optional.of(DIFF_AUTH_NUMBER));
        String content = objectMapper.writeValueAsString(REQUEST_JOIN);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));
        //then
        perform.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("인증번호 보내기 성공 테스트")
    public void sendAuthNum_suc() throws Exception {
        //given
        String content = objectMapper.writeValueAsString(REQUEST_AUTH_NUMBER);
        given(redisRepository.saveData(any(), any(), any())).willReturn(true);
        given(passwordService.getRandomCode()).willReturn(AUTH_NUMBER);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("true"));
        verify(authNumSendService, times(1)).send(anyString(), any());
    }

    @Test
    @DisplayName("인증번호 보내기 실패 테스트")
    public void sendAuthNum_fail() throws Exception {
        //given
        AuthDto.RequestAuthNumber REQUEST_AUTH_NUMBER = AuthDto.RequestAuthNumber.builder()
                .phone(INVALID_PHONE)
                .build();

        String content = objectMapper.writeValueAsString(REQUEST_AUTH_NUMBER);
        //when
        ResultActions perform = mockMvc.perform(
                post("/auth/number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        perform.andExpect(status().isBadRequest());
        verify(authNumSendService, times(0)).send(anyString(), any());
    }
}
