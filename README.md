# 프로젝트 통합 소개
## 1. 소개
이 프로젝트는 **Spring Boot**와 **React**를 사용하여 개발된 백엔드 개발자 커뮤니티 사이트입니다. 
이 플랫폼은 백엔드 개발자들이 모여서 기술 관련 게시글을 작성하고, 토론을 하며, 서로의 경험을 공유할 수 있도록 만들어졌습니다. 
사용자는 다양한 게시판을 통해 질문을 올리고 답변을 받으며, 인기 게시글을 통해 트렌드를 파악할 수 있습니다.

## 2. 구성

프로젝트는 API서버, Batch서버, React 세 개의 독립된 모듈로 나뉘어 있으며, 
각각의 모듈은 서로 긴밀하게 연동되어 사이트의 다양한 기능을 제공하는 형태로 개발됐습니다.

- [API 서버](#api-서버) : Spring Boot를 기반으로 커뮤니티의 핵심 기능(게시글, 댓글, 사용자 인증 등)을 담당하는 백엔드 서버입니다.
- [Batch 서버](#batch-서버) : Spring Batch를 활용해 정기적으로 파일과 데이터를 관리하는 서버입니다.
- [프론트엔드](#프론트엔드) : React로 개발된 사용자 인터페이스로, 게시글 작성 및 탐색, 좋아요, 댓글 등 다양한 상호작용 기능을 제공합니다.


## 3. 주요 기능
### 3.1 회원가입 및 로그인
- **JWT 기반 인증**과 **OAuth2** 로그인을 지원
- Spring Security를 사용해 안전한 인증 시스템 구축

### 3.2 게시글 작성 및 관리
- **React Quill** 에디터를 통한 HTML 형식의 게시글 작성 지원
- 최대 10개의 이미지를 첨부 가능하며, AWS S3에 저장

### 3.3 카테고리 분류 및 필터링
- 재귀적 카테고리 구조로 게시글 분류
- 카테고리 메뉴는 다단계 구조로 구현되어 사용자 친화적인 네비게이션 제공

### 3.4 인기 게시물 및 최신 게시물
- 인기 게시물과 최신 게시물이 메인 페이지에 표시
- 게시물은 이미지와 제목이 함께 표시되어 쉽게 탐색 가능

### 3.5 댓글 및 좋아요
- 게시글에 대한 **댓글 작성**과 **좋아요** 기능
- 댓글과 좋아요 수를 기반으로 인기 게시물 선정 기능 제공

### 3.6 알림 기능
- 자신의 게시글이나 댓글에 대한 반응을 실시간으로 알림 (SSE 사용)

### 3.7 파일 관리
- AWS S3를 통해 이미지 파일을 저장 및 관리
- **Spring Batch**로 파일 삭제 및 주기적인 정리

### 3.8 관리자 기능
- **관리자 페이지**를 통해 카테고리 관리 및 회원 관리 기능 제공

---

# API 서버 소개
이 프로젝트는 Spring boot를 통해 개발된 백엔드 개발자 커뮤니티 사이트의 API서버 입니다.

## 프로젝트 구조
<pre>
archi-back/
├── src/
│   ├── main/
│   │   ├── java/com/architecture/archi/
│   │   │   ├── common/          # 서버 공통 로직 및 에러 처리 정의
│   │   │   ├── config/          # config 파일 관리
│   │   │   ├── content/         # API 개발 (비즈니스 로직 처리)
│   │   │   └── db/              # DB 관련 Entity, Repository 관리
│   │   └── resources/
│   │       ├── db/              # DB 마이그레이션 관련 파일 관리
│   │       │   └── migration/    # Flyway 마이그레이션 스크립트 파일
│   │       ├── profiles/        # 환경별 yml 설정 분리
│   │       │   ├── dev/         # dev 환경 설정
│   │       │   ├── local/       # local 환경 설정
│   │       │   ├── prod/        # prod 환경 설정
│   │       └── application-core.yml # 공통 환경 설정 파일
└── ...
</pre>



## 기술 스택
- Java 17
- Spring Boot, Spring Security
- JPA (Hibernate), QueryDSL, MySQL, Flyway
- Redis
- docker
- AWS
  - S3
  - EC2


## API 설계 (Swagger)
Swagger 문서
- API는 Swagger를 통해 문서화되어 있으며, 이를 통해 자세한 API 스펙을 확인할 수 있습니다.
- https://52.79.69.111.nip.io/swagger-ui/index.html

- **어드민 API**: `/api/v1/admin` - 관리자 전용 API (공지사항 및 카테고리 관련 기능)
- **알림 API**: `/api/v1/notification` - SSE를 통한 실시간 알림 기능 제공
- **유저 API**: `/api/v1/user` - 회원가입, 회원 정보 수정, 비밀번호 변경 등 유저 관련 API
- **게시글 API**: `/api/v1/content` - 게시글 작성, 조회, 수정, 삭제 API
- **댓글 API**: `/api/v1/comment` - 게시글에 대한 댓글 작성, 수정, 삭제 API
- **파일 API**: `/api/v1/file` - 파일 업로드 및 삭제 기능 API
- **인증/인가 API**: `/api/v1/auth` - JWT 기반 로그인, 로그아웃, 토큰 재발급 기능
  

## ERD
https://app.diagrams.net/#G1zUR_abSdtdSqhZkK-b0yGxIBGk7-plMk#%7B%22pageId%22%3A%22u4vEcps5jHnmp1nRnu5Y%22%7D

## 배포
서버 배포는 AWS를 사용하여 배포를 진행했습니다.
- EC2 인스턴스에서 Docker를 통해 MySQL과 Redis를 컨테이너 형태로 실행하여 데이터베이스와 캐싱 시스템을 관리.
- Spring Boot 애플리케이션은 개발(dev) 환경에서 JAR 파일을 빌드한 후 EC2에 업로드하여 실행.
- Caddy를 이용해 SSL 인증서를 자동으로 발급하고 갱신하여 HTTPS 연결을 보장.
