package com.project.mything.util;

import com.project.mything.auth.dto.AuthDto;
import com.project.mything.friend.dto.ApplyDto;
import com.project.mything.friend.dto.FriendDto;
import com.project.mything.image.dto.ImageDto;
import com.project.mything.page.ResponseMultiPageDto;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.headers.RequestHeadersSnippet;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

public class TestConstants {

    public final static Long ID1 = 1L;
    public final static Long ID2 = 2L;
    public final static String EMAIL = "test@naver.com";
    public final static String DIFF_EMAIL = "test1@naver.com";
    public static final String INVALID_EMAIL = "@를 이용한 이메일 형식을 지켜주세요";
    public final static String PASSWORD = "testPassword1!";
    public final static String DIFF_PASSWORD = "testPassword2@";
    public static final String INVALID_PASSWORD = "영문,숫자,특수문자 8~16 자리입니다.";
    public final static String NAME = "test";
    public final static String DIFF_NAME = "test1";
    public static final String INVALID_NAME = "한글 2자 이상 16자 이하입니다. No_English_please";
    public final static String PHONE = "01012345678";
    public final static String DIFF_PHONE = "01087654321";
    public final static String INVALID_PHONE = "1234567890123";
    public final static LocalDate BIRTHDAY = LocalDate.of(2023, 3, 20);
    public final static LocalDate DIFF_BIRTHDAY = LocalDate.of(2023, 3, 20);
    public final static String AUTH_NUMBER = "1234";
    public final static String DIFF_AUTH_NUMBER = "4321";
    public final static String ACCESS_KEY = "accessKey";
    public final static String INVALID_ACCESS_KEY = "invalidAccessKey";
    public final static String INFO_MESSAGE = "testInfoMessage";
    public final static String DIFF_INFO_MESSAGE = "testDifferentInfoMessage";
    public final static String REMOTE_PATH = "remotePath";
    public final static String JWT_HEADER = "Authorization";
    public final static String JWT_TOKEN = "AccessToken";
    public final static Integer ITEM_COUNT = 5;
    public final static String DIFF_REMOTE_PATH = "differentRemotePath";
    public final static Long NOT_FOUND_ID = 404L;
    public final static AuthDto.RequestJoin REQUEST_JOIN = AuthDto.RequestJoin.builder()
            .email(EMAIL)
            .name(NAME)
            .phone(PHONE)
            .password(PASSWORD)
            .birthday(BIRTHDAY)
            .authNumber(AUTH_NUMBER).build();

    public final static AuthDto.RequestAuthNumber REQUEST_AUTH_NUMBER = AuthDto.RequestAuthNumber.builder()
            .phone(PHONE)
            .build();

    public final static AuthDto.ResponseLogin RESPONSE_LOGIN = AuthDto.ResponseLogin.builder()
            .userId(ID1)
            .accessToken(ACCESS_KEY)
            .build();

    public final static User ORIGINAL_USER = User.builder()
            .id(ID1)
            .name(NAME)
            .phone(PHONE)
            .birthday(BIRTHDAY)
            .email(EMAIL)
            .password(PASSWORD)
            .build();

    public final static User REQUEST_USER = User.builder()
            .name(NAME)
            .phone(PHONE)
            .birthday(BIRTHDAY)
            .email(EMAIL)
            .password(PASSWORD)
            .build();
    public final static AuthDto.RequestLogin REQUEST_LOGIN = AuthDto.RequestLogin.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();

    public final static ImageDto.SimpleImageDto SIMPLE_IMAGE_DTO = ImageDto.SimpleImageDto.builder()
            .imageId(ID1)
            .remotePath(REMOTE_PATH)
            .build();

    public final static ImageDto.SimpleImageDto DIFF_SIMPLE_IMAGE_DTO = ImageDto.SimpleImageDto.builder()
            .imageId(ID2)
            .remotePath(DIFF_REMOTE_PATH)
            .build();

    public final static UserDto.ResponseDetailUser RESPONSE_DETAIL_USER = UserDto.ResponseDetailUser.builder()
            .userId(ID1)
            .name(NAME)
            .phone(PHONE)
            .birthday(BIRTHDAY)
            .infoMessage(INFO_MESSAGE)
            .avatar(SIMPLE_IMAGE_DTO)
            .build();

    public final static UserDto.ResponseDetailUser BIRTH_RESPONSE_DETAIL_USER = UserDto.ResponseDetailUser.builder()
            .userId(ID1)
            .name(NAME)
            .phone(PHONE)
            .birthday(LocalDate.now())
            .infoMessage(INFO_MESSAGE)
            .avatar(SIMPLE_IMAGE_DTO)
            .build();

    public final static UserDto.ResponseDetailUser DIFF_RESPONSE_DETAIL_USER = UserDto.ResponseDetailUser.builder()
            .userId(ID2)
            .name(DIFF_NAME)
            .phone(DIFF_PHONE)
            .birthday(DIFF_BIRTHDAY)
            .infoMessage(DIFF_INFO_MESSAGE)
            .avatar(DIFF_SIMPLE_IMAGE_DTO)
            .build();

    public final static UserDto.ResponseDetailUser BIRTH_DIFF_RESPONSE_DETAIL_USER = UserDto.ResponseDetailUser.builder()
            .userId(ID2)
            .name(DIFF_NAME)
            .phone(DIFF_PHONE)
            .birthday(LocalDate.now())
            .infoMessage(DIFF_INFO_MESSAGE)
            .avatar(DIFF_SIMPLE_IMAGE_DTO)
            .build();

    public final static UserDto.RequestEditProFile REQUEST_EDIT_PRO_FILE = UserDto.RequestEditProFile.builder()
            .name(NAME)
            .birthday(BIRTHDAY)
            .infoMessage(INFO_MESSAGE)
            .avatar(SIMPLE_IMAGE_DTO)
            .build();

    public final static UserDto.RequestEditProFile ERROR_EDIT_PRO_FILE = UserDto.RequestEditProFile.builder()
            .avatar(SIMPLE_IMAGE_DTO)
            .build();

    //파일 이미지
    public final static String FILE_NAME = "testFile.png";
    public final static String CONTENT_TYPE = "image/png";
    public final static String FILE_PATH = "src/main/resources/" + FILE_NAME;
    public final static FileInputStream FILE_INPUT_STREAM;
    public final static MockMultipartFile MOCK_MULTIPART_FILE;

    static {
        try {
            FILE_INPUT_STREAM = new FileInputStream(FILE_PATH);
            MOCK_MULTIPART_FILE = new MockMultipartFile("multipartFile", FILE_NAME, CONTENT_TYPE, FILE_INPUT_STREAM);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final static FriendDto.ResponseSimpleFriend RESPONSE_SIMPLE_FRIEND = FriendDto.ResponseSimpleFriend.builder()
            .user(RESPONSE_DETAIL_USER)
            .itemCount(ITEM_COUNT)
            .build();

    private static final FriendDto.ResponseSimpleFriend DIFF_RESPONSE_SIMPLE_FRIEND = FriendDto.ResponseSimpleFriend.builder()
            .user(DIFF_RESPONSE_DETAIL_USER)
            .itemCount(ITEM_COUNT)
            .build();

    public final static FriendDto.ResponseSimpleFriend BIRTH_RESPONSE_SIMPLE_FRIEND = FriendDto.ResponseSimpleFriend.builder()
            .user(BIRTH_RESPONSE_DETAIL_USER)
            .itemCount(ITEM_COUNT)
            .build();

    private static final FriendDto.ResponseSimpleFriend BIRTH_DIFF_RESPONSE_SIMPLE_FRIEND = FriendDto.ResponseSimpleFriend.builder()
            .user(BIRTH_DIFF_RESPONSE_DETAIL_USER)
            .itemCount(ITEM_COUNT)
            .build();
    public final static Pageable PAGE_REQUEST = PageRequest.of(0, 2);

    public final static List<FriendDto.ResponseSimpleFriend> RESPONSE_SIMPLE_FRIENDS
            = List.of(RESPONSE_SIMPLE_FRIEND, DIFF_RESPONSE_SIMPLE_FRIEND);
    public final static List<FriendDto.ResponseSimpleFriend> BIRTHDAY_RESPONSE_SIMPLE_FRIENDS
            = List.of(BIRTH_RESPONSE_SIMPLE_FRIEND, BIRTH_DIFF_RESPONSE_SIMPLE_FRIEND);

    public final static Page<FriendDto.ResponseSimpleFriend> PAGE = new PageImpl<>(RESPONSE_SIMPLE_FRIENDS, PAGE_REQUEST, RESPONSE_SIMPLE_FRIENDS.size());
    public final static Page<FriendDto.ResponseSimpleFriend> BIRTH_PAGE = new PageImpl<>(BIRTHDAY_RESPONSE_SIMPLE_FRIENDS, PAGE_REQUEST, BIRTHDAY_RESPONSE_SIMPLE_FRIENDS.size());

    public final static ResponseMultiPageDto<FriendDto.ResponseSimpleFriend> RESPONSE_SIMPLE_FRIEND_RESPONSE_MULTI_PAGE
            = new ResponseMultiPageDto<FriendDto.ResponseSimpleFriend>(RESPONSE_SIMPLE_FRIENDS, PAGE);

    public final static ResponseMultiPageDto<FriendDto.ResponseSimpleFriend> BIRTH_RESPONSE_SIMPLE_FRIEND_RESPONSE_MULTI_PAGE
            = new ResponseMultiPageDto<FriendDto.ResponseSimpleFriend>(BIRTHDAY_RESPONSE_SIMPLE_FRIENDS, BIRTH_PAGE);
    public final static ApplyDto.ResponseApplyId RESPONSE_APPLY_ID = ApplyDto.ResponseApplyId.builder()
            .applyId(ID1)
            .build();

    @NotNull
    public static RequestHeadersSnippet getRequestHeadersSnippet() {
        return requestHeaders(headerWithName(JWT_HEADER).description(JWT_TOKEN));
    }

    private final static UserDto.ResponseSimpleUser RESPONSE_SIMPLE_USER = UserDto.ResponseSimpleUser.builder()
            .userId(ID1)
            .name(NAME)
            .avatar(SIMPLE_IMAGE_DTO)
            .build();
    private final static UserDto.ResponseSimpleUser DIFF_RESPONSE_SIMPLE_USER = UserDto.ResponseSimpleUser.builder()
            .userId(ID2)
            .name(DIFF_NAME)
            .avatar(DIFF_SIMPLE_IMAGE_DTO)
            .build();
    private final static ApplyDto.ResponseSimpleApply RESPONSE_SIMPLE_APPLY = ApplyDto.ResponseSimpleApply.builder()
            .applyId(ID1)
            .user(RESPONSE_SIMPLE_USER)
            .build();
    private final static ApplyDto.ResponseSimpleApply DIFF_RESPONSE_SIMPLE_APPLY = ApplyDto.ResponseSimpleApply.builder()
            .applyId(ID2)
            .user(DIFF_RESPONSE_SIMPLE_USER)
            .build();

    public final static List<ApplyDto.ResponseSimpleApply> RESPONSE_SIMPLE_APPLY_LIST = List.of(RESPONSE_SIMPLE_APPLY, DIFF_RESPONSE_SIMPLE_APPLY);
}