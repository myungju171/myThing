package com.project.mything.friend.repository.applyQueryDsl;

import com.project.mything.friend.dto.ApplyDto;
import com.project.mything.friend.dto.QApplyDto_ResponseSimpleApply;
import com.project.mything.friend.entity.enums.ApplyStatus;
import com.project.mything.image.dto.QImageDto_SimpleImageDto;
import com.project.mything.image.entity.QImage;
import com.project.mything.user.dto.QUserDto_ResponseSimpleUser;
import com.project.mything.user.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.project.mything.friend.entity.QApply.apply;

@Repository
public class ApplyQueryRepositoryImpl implements ApplyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ApplyQueryRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ApplyDto.ResponseSimpleApply> getApply(Long userId, Boolean isReceiveApply, ApplyStatus applyStatus) {
        return queryFactory.select(new QApplyDto_ResponseSimpleApply(apply.id,
                        applyType(isReceiveApply)))
                .from(apply)
                .leftJoin(apply.receiveUser, QUser.user)
                .leftJoin(apply.receiveUser.image, QImage.image)
                .leftJoin(apply.sendUser, QUser.user)
                .leftJoin(apply.sendUser.image, QImage.image)
                .where(applyStatus(applyStatus),
                        isReceivedApply(isReceiveApply, userId))
                .orderBy(apply.createdAt.desc())
                .fetch();
    }

    private QUserDto_ResponseSimpleUser applyType(Boolean isReceivedApply) {
        return isReceivedApply ? receiveApply() : senderApply();
    }

    private QUserDto_ResponseSimpleUser senderApply() {
        return new QUserDto_ResponseSimpleUser(apply.receiveUser.id,
                apply.receiveUser.name,
                new QImageDto_SimpleImageDto(apply.receiveUser.image.id,
                        apply.receiveUser.image.remotePath));
    }

    private QUserDto_ResponseSimpleUser receiveApply() {
        return new QUserDto_ResponseSimpleUser(apply.sendUser.id,
                apply.sendUser.name,
                new QImageDto_SimpleImageDto(apply.sendUser.image.id,
                        apply.sendUser.image.remotePath));
    }

    private BooleanExpression isReceivedApply(Boolean isReceivedApply, Long userId) {
        return isReceivedApply ? apply.receiveUser.id.eq(userId) : apply.sendUser.id.eq(userId);
    }

    private BooleanExpression applyStatus(ApplyStatus applyStatus) {
        return apply.applyStatus.eq(applyStatus);
    }

}

