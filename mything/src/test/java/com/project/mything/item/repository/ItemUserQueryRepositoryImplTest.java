package com.project.mything.item.repository;

import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.entity.Item;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.item.entity.enums.ItemStatus;
import com.project.mything.user.entity.User;
import com.project.mything.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemUserQueryRepositoryImplTest {
    @Autowired
    ItemUserRepository itemUserRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("조회시 관심상품이 맨먼저 정렬되고 최신순으로 정렬된다.")
    public void searchSimpleItem_suc() {
        //given
        User user = User.builder()
                .name("홍길동")
                .build();
        User dbUser = userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            Boolean interest = false;
            if (i == 9) {
                interest = true;
            }
            Item item = Item.builder()
                    .productId((long) i)
                    .title("테스트 타이틀")
                    .price(1000)
                    .image("imageLink")
                    .link("link")
                    .build();
            itemRepository.save(item);
            ItemUser itemUser = ItemUser.builder()
                    .user(user)
                    .item(item)
                    .itemStatus(ItemStatus.POST)
                    .interestedItem(interest)
                    .secretItem(false)
                    .build()
                    .addItemUser();
            itemUserRepository.save(itemUser);
        }
        //when
        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemDto.ResponseSimpleItem> result = itemUserRepository.searchSimpleItem(dbUser.getId(), pageable);
        //then
        assertThat(result.getContent().size()).isEqualTo(10);
        assertThat(result.getContent().get(0).getInterestedItem()).isEqualTo(true);
        assertThat(result.getContent().get(1).getInterestedItem()).isEqualTo(false);
    }

    @Test
    @DisplayName("조회시 아이템상태가 BOUGHT 또는 RECEIVED 라면 조회 되지않는다.")
    public void searchSimpleItem_suc2() {
        //given
        User user = User.builder()
                .name("홍길동")
                .build();
        User dbUser = userRepository.save(user);

        ItemStatus itemStatus;

        for (int i = 0; i < 5; i++) {
            itemStatus = i % 2 == 0 ? ItemStatus.BOUGHT : ItemStatus.RECEIVED;
            Item item = Item.builder()
                    .productId((long) i)
                    .title("테스트 타이틀")
                    .price(1000)
                    .image("imageLink")
                    .link("link")
                    .build();
            itemRepository.save(item);
            ItemUser itemUser = ItemUser.builder()
                    .user(user)
                    .item(item)
                    .itemStatus(itemStatus)
                    .interestedItem(true)
                    .secretItem(false)
                    .build()
                    .addItemUser();
            itemUserRepository.save(itemUser);
        }
        //when
        Pageable pageable = PageRequest.of(0, 5);
        Page<ItemDto.ResponseSimpleItem> result = itemUserRepository.searchSimpleItem(dbUser.getId(), pageable);
        //then
        assertThat(result.getTotalPages()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }
}