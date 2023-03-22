package com.project.mything.friend.repository.applyQueryDsl;

import com.project.mything.friend.dto.ApplyDto;
import com.project.mything.friend.dto.QApplyDto_ResponseSimpleApply;
import com.project.mything.friend.entity.enums.ApplyStatus;
import com.project.mything.image.dto.QImageDto_SimpleImageDto;
import com.project.mything.user.dto.QUserDto_ResponseSimpleUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.jetbrains.annotations.NotNull;
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
    public List<ApplyDto.ResponseSimpleApply> getApply(Long userId, Boolean isReceiveApply) {
        return queryFactory.select(new QApplyDto_ResponseSimpleApply(apply.id,
                        applyType(isReceiveApply)))
                .from(apply)
                .where(apply.applyStatus.eq(ApplyStatus.SUGGEST))
                .orderBy(apply.id.desc())
                .fetch();
    }

    private QUserDto_ResponseSimpleUser applyType(Boolean isReceivedApply) {
        return isReceivedApply ? receiveApply() : senderApply();
    }

    @NotNull
    private QUserDto_ResponseSimpleUser senderApply() {
        return new QUserDto_ResponseSimpleUser(apply.sendUser.id,
                apply.sendUser.name,
                new QImageDto_SimpleImageDto(apply.sendUser.image.id,
                        apply.sendUser.image.remotePath));
    }

    @NotNull
    private QUserDto_ResponseSimpleUser receiveApply() {
        return new QUserDto_ResponseSimpleUser(apply.receiveUser.id,
                apply.receiveUser.name,
                new QImageDto_SimpleImageDto(apply.receiveUser.image.id,
                        apply.receiveUser.image.remotePath));
    }
}

