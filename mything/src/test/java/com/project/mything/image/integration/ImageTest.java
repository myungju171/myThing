package com.project.mything.image.integration;

import com.project.mything.image.dto.ImageDto;
import com.project.mything.image.entity.Image;
import com.project.mything.image.service.ImageService;
import com.project.mything.security.jwt.service.JwtTokenProvider;
import com.project.mything.user.entity.User;
import com.project.mything.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mything.util.TestConstants.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class ImageTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ImageService imageService;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("이미지 업로드 테스트 성공")
    public void upload_suc() throws Exception {
        //given
        User dbUser = userRepository.save(ORIGINAL_USER);
        String token = jwtTokenProvider.createToken(dbUser);
        //when
        ResultActions perform = mockMvc.perform(
                multipart("/users/avatars")
                        .file(MOCK_MULTIPART_FILE)
                        .header(JWT_HEADER, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );
        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageId").exists())
                .andExpect(jsonPath("$.remotePath").exists());


    }

    @Test
    @DisplayName("이미지 삭제 테스트 성공")
    public void deleteAvatar_suc() throws Exception {
    //given
        User dbUser = userRepository.save(ORIGINAL_USER);
        String token = jwtTokenProvider.createToken(dbUser);
        ImageDto.SimpleImageDto simpleImageDto = imageService.uploadImage(MOCK_MULTIPART_FILE);
        Image image = imageService.findById(simpleImageDto.getImageId());
        dbUser.addImage(image);
        //when
        ResultActions perform = mockMvc.perform(
                delete("/users/avatars")
                        .header(JWT_HEADER, token)
        );
        //then
        perform.andExpect(status().isNoContent());
        Assertions.assertThat(dbUser.getImage()).isNull();
    }
}
