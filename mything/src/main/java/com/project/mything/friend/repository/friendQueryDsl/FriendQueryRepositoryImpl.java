package com.project.mything.friend.repository.friendQueryDsl;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.dto.QFriendDto_ResponseSimpleFriend;
import com.project.mything.friend.entity.enums.FriendStatus;
import com.project.mything.user.dto.QImageDto_SimpleImageDto;
import com.project.mything.user.dto.QUserDto_ResponseDetailUser;
import com.project.mything.user.entity.enums.UserStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

import static com.project.mything.friend.entity.QFriend.friend;

@Repository
public class FriendQueryRepositoryImpl implements FriendQueryRepository {

    private final JPAQueryFactory queryFactory;

    public FriendQueryRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<FriendDto.ResponseSimpleFriend> getFriendList(Long userId, FriendStatus friendStatus, Boolean isBirthday, Pageable pageable) {
        List<FriendDto.ResponseSimpleFriend> result = queryFactory.select(new QFriendDto_ResponseSimpleFriend(
                        new QUserDto_ResponseDetailUser(friend.userFriend.id,
                                friend.userFriend.name,
                                friend.userFriend.phone,
                                friend.userFriend.birthday,
                                friend.userFriend.infoMessage,
                                new QImageDto_SimpleImageDto(friend.userFriend.image.id, friend.userFriend.image.remotePath)),
                        friend.userFriend.itemUserList.size()))
                .from(friend)
                .where(friend.user.id.eq(userId)
                                .and(friend.user.userStatus.eq(UserStatus.ACTIVE)),
                        birthday(isBirthday),
                        status(friendStatus))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(friend.user.name.asc())
                .fetch();

        Long count = queryFactory
                .select(friend.count())
                .from(friend)
                .where(status(friendStatus),
                        (friend.user.id.eq(userId))
                                .and(friend.user.userStatus.eq(UserStatus.ACTIVE)))
                .fetchOne();

        return new PageImpl<>(result, pageable, count);
    }

    private BooleanExpression birthday(Boolean isBirthday) {
        return isBirthday ? friend.userFriend.birthday.eq(LocalDate.now()) : null;
    }

    private BooleanExpression status(FriendStatus friendStatus) {
        return friend.friendStatus.eq(friendStatus);
    }
}
