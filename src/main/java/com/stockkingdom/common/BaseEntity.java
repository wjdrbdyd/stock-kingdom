package com.stockkingdom.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
/* TODO: 빌더로 할수 없더라.. 등록자 수정자 또한 자동으로 값 들어가게 세팅할수있다.
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // 1. Spring Security의 권한 관리자에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 로그인하지 않은 상태(예: 비회원 가입 등)이거나 인증 정보가 없다면 null이나 시스템 계정 반환
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SYSTEM"); // 또는 Optional.empty();
        }

        // 3. 로그인한 유저의 식별자(ID, 이메일, 사번 등)를 리턴!
        // (보통 CustomUserDetails를 캐스팅해서 꺼내옵니다)
        String userId = authentication.getName();

        return Optional.of(userId); // 🎯 이 리턴값이 @CreatedBy에 자동으로 꽂힙니다!
    }
}
*/
public abstract class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false, length = 50)
    protected String createdBy;

    @LastModifiedBy
    @Column(length = 50)
    protected String lastModifiedBy;
    
}
