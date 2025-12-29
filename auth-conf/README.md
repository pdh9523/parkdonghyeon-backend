# 인증 서버 구성

해당 프로젝트에서 keycloak을 사용하면서,  
테스트를 위해선 인증 서버가 안정적인 주소를 가져야 한다는 판단 하에,  
실험 환경이 아닌 외부에서 접근 가능한 독립적인 실제 클라우드 환경으로 구성했습니다.

## 1. 별도 구성 이유

### 1) 인증 책임 분리
인증/인가 로직은 다음과 같은 특성을 가집니다.
- 보안 설정의 변경 가능성
- 외부 공개 엔드포인트 필요
- 토큰 발급, 검증, 관리 등 인프라 의존도가 높음

이를 메인 API에서 분리함으로써
- 도메인 서버는 비즈니스 로직에 집중
- 인증 관련 스펙 변경이 도메인 서버에 영향을 주지 않음
- 향후 정책 변경에 유연하게 대처

와 같은 구조를 유지할 수 있습니다.

### 2) 실 서비스 구조를 고려한 설계
실제 서비스 환경에서는 인증 서버를 위와 같이 분리하는 경우가 일반적이기에,  
단순히 userId를 변수로 주거나, 직접 security 설정에서 토큰을 조작하는 것보다  
실무에 가까운 설계라고 판단하여 이를 목표로 했습니다. 

---

## 2. 인증 서버 구성 방식
인증 서버는 Oracle Cloud VM 위에 Dokcer compose 기반으로 구성되어 있습니다.

### 1) 구성 요소
- Keycloak
  - OAuth2 / OpenID Connect 인증 서버
  - 토큰 발급 및 검증
- PostgreSQL
  - Keycloak 연동 데이터베이스
  - 인증 정보, Realm, Client 설정 저장
- Nginx
  - reverse Proxy 역할
  - 외부 HTTPS 요청을 Keycloak으로 전달
- Certbot (Let's Encrypt)
  - TLS 인증서 발급 및 갱신
  - HTTPS 환경 구성

### 2) 전체 흐름

1. Oracle Cloud VM 생성
2. Docker 및 Docker compose 설치
3. certbot + nginx 로 HTTPS 환경 구성
4. nginx 뒤에서 Keycloak, postgres 실행
5. 메인 API 서버는 인증 서버의 issuer URL('auth.dong-hyeon.site')를 통해 토큰 검증

### 3) AWS 대신 Oracle Cloud를 선택한 이유

초기에는 AWS 환경을 고려했으나, 무료 제공 수준 차이로 인해 Oracle Cloud를 선택했습니다.

postgres + keycloak 을 안정적으로 구동하기 위해선 AWS 프리티어 (1c 1gb) 수준으로는 불가하다고 판단했습니다.

Oracle Cloud VM은 AWS EC2와 동일하게 설정할 수 있지만,  
무료 제공 30일 에 4~8GB 메모리 수준의 인스턴스를 제공받을 수 있어 안정적으로 해당 서버를 구동할 수 있다고 판단했습니다. 



