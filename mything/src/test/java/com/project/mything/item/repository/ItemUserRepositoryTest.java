package com.project.mything.item.repository;

import com.project.mything.item.entity.Item;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.item.entity.enums.ItemStatus;
import com.project.mything.user.entity.User;
import com.project.mything.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemUserRepositoryTest {

    @Autowired
    ItemUserRepository itemUserRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @BeforeEach
    public void init() {
        User user = User.builder().name("홍길동").build();
        User dbUser = userRepository.save(user);
        Item item = Item.builder().productId(12345L).title("테스트 타이틀").price(1000).build();
        Item dbItem = itemRepository.save(item);
        ItemUser itemUser = ItemUser.builder().user(user).item(item).build().addItemUser();
        ItemUser dbItemUser = itemUserRepository.save(itemUser);
    }


    @Test
    @DisplayName("저장한 itemUser의 addItemUser메소드를 통해 정상적으로 연관관계 매핑하는지 성공 테스트")
    public void caseCade_create_test() {
        //given
        User user = User.builder().name("홍길동").build();
        User dbUser = userRepository.save(user);
        Item item = Item.builder().productId(12345L).title("테스트 타이틀").price(1000).build();
        Item dbItem = itemRepository.save(item);
        //when
        ItemUser itemUser = ItemUser.builder().user(user).item(item).build().addItemUser();
        ItemUser dbItemUser = itemUserRepository.save(itemUser);
        //then

        assertThat(dbItemUser.getUser().getId()).isEqualTo(dbUser.getId());
        assertThat(dbItemUser.getItem().getId()).isEqualTo(dbItem.getId());
        assertThat(dbUser.getItemUserList().size()).isSameAs(1);
        assertThat(dbUser.getItemUserList().size()).isSameAs(1);
    }

    @Test
    @DisplayName("User의 caseCade_remove 통해 정상적으로 연관관계 삭제하는지 성공 테스트")
    public void caseCade_delete_test() {
        //given
        User user = User.builder().name("홍길동").build();
        User dbUser = userRepository.save(user);
        Item item = Item.builder().productId(12345L).title("테스트 타이틀").price(1000).build();
        Item dbItem = itemRepository.save(item);
        ItemUser itemUser = ItemUser.builder().user(user).item(item).build().addItemUser();
        ItemUser dbItemUser = itemUserRepository.save(itemUser);

        //when
        userRepository.delete(dbUser);

        boolean itemUserResult = itemUserRepository.findById(dbItemUser.getId()).isPresent();
        boolean itemResult = itemRepository.findById(dbItem.getId()).isPresent();
        boolean userResult = userRepository.findById(dbUser.getId()).isPresent();

        //then
        assertThat(itemUserResult).isFalse();
        assertThat(userResult).isFalse();
        assertThat(itemResult).isFalse();
    }

    @Test
    @DisplayName("itemUser 객체 userId와 productId로 찾아내는 메소드 성공 테스트")
    public void findItemUserByUserIdAndProductId_suc() {
        //given
        User user = User.builder().name("홍길동").build();
        User dbUser = userRepository.save(user);
        Item item = Item.builder().productId(12345L).title("테스트 타이틀").price(1000).build();
        Item dbItem = itemRepository.save(item);
        ItemUser itemUser = ItemUser.builder().user(user).item(item).build().addItemUser();
        ItemUser dbItemUser = itemUserRepository.save(itemUser);
        //when
        boolean result = itemUserRepository.findByUserIdAndProductId(dbUser.getId(), dbItem.getProductId()).isPresent();
        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("itemUser 객체 userId와 itemId로 찾아내는 메소드 성공 테스트")
    public void findItemUserByUserIdAndItemId() {
        //given
        User user = User.builder().name("홍길동").build();
        User dbUser = userRepository.save(user);
        Item item = Item.builder().title("테스트 타이틀").build();
        Item dbItem = itemRepository.save(item);
        ItemUser itemUser = ItemUser.builder().user(user).item(item).build().addItemUser();
        ItemUser dbItemUser = itemUserRepository.save(itemUser);
        //when
        ItemUser result = itemUserRepository.findByUserIdAndItemId(dbUser.getId(), item.getId())
                .orElseThrow(() -> new RuntimeException("ItemUser null값"));
        //then
        assertThat(result.getItem().getTitle()).isEqualTo(dbItem.getTitle());
        assertThat(result.getUser().getName()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("ItemUser 객체 삭제시 User와 Item객체안에 있는 ItemUserList에도 삭제되는지 테스트 진행 ")
    public void deleteItemUser_suc() {
        //given
        User user = User.builder().build();
        User dbUser = userRepository.save(user);

        Item item = Item.builder().build();
        Item dbItem = itemRepository.save(item);

        ItemUser itemUser = ItemUser.builder().item(dbItem).user(dbUser).itemStatus(ItemStatus.POST).build();
        ItemUser dbItemUser = itemUserRepository.save(itemUser);
        dbItemUser.addItemUser();

        assertThat(dbUser.getItemUserList().size()).isEqualTo(1);
        assertThat(dbItem.getItemUserList().size()).isEqualTo(1);

        //when
        dbItemUser.getUser().getItemUserList().remove(dbItemUser);
        dbItemUser.getItem().getItemUserList().remove(dbItemUser);
        itemUserRepository.delete(dbItemUser);

        boolean present =
                itemUserRepository.findByUserIdAndItemId(dbUser.getId(), dbItem.getId()).isPresent();
        //then
        assertThat(dbUser.getItemUserList().size()).isEqualTo(0);
        assertThat(dbItem.getItemUserList().size()).isEqualTo(0);
        assertThat(present).isFalse();
    }
}