package com.project.mything.item.repository;

import com.project.mything.item.dto.ItemDto;
import com.project.mything.item.dto.QItemDto_ResponseSimpleItem;
import com.project.mything.item.entity.enums.ItemStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.project.mything.item.entity.QItemUser.itemUser;

@Repository
public class ItemUserQueryRepositoryImpl implements ItemUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ItemUserQueryRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ItemDto.ResponseSimpleItem> searchSimpleItem(Long userId, Pageable pageable) {
        List<ItemDto.ResponseSimpleItem> result = queryFactory.select(new QItemDto_ResponseSimpleItem(
                        itemUser.item.id,
                        itemUser.item.title,
                        itemUser.item.price,
                        itemUser.item.image,
                        itemUser.interestedItem,
                        itemUser.secretItem,
                        itemUser.itemStatus,
                        itemUser.createdAt,
                        itemUser.lastModifiedAt))
                .from(itemUser)
                .where(itemUser.user.id.eq(userId).and(
                        itemUser.itemStatus.eq(ItemStatus.POST)
                                .or(itemUser.itemStatus.eq(ItemStatus.RESERVE))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemUser.interestedItem.desc())
                .fetch();

        Long count = queryFactory
                .select(itemUser.count())
                .from(itemUser)
                .where(itemUser.itemStatus.eq(ItemStatus.POST)
                        .and(itemUser.itemStatus.eq(ItemStatus.RESERVE)))
                .fetchOne();


        return new PageImpl<>(result, pageable, count);
    }
}
