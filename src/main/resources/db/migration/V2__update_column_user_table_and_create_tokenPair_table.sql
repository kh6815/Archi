-- 1. EMAIL 필드에 UNIQUE 제약 조건 추가
ALTER TABLE USER
    ADD CONSTRAINT UK_USER_EMAIL UNIQUE (EMAIL);

-- 2. ROLE 필드 추가
ALTER TABLE USER
    ADD COLUMN ROLE ENUM('USER', 'ADMIN') DEFAULT 'USER' NOT NULL;


CREATE TABLE TOKEN_PAIR (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `ACCESS_TOKEN` varchar(255) DEFAULT NULL,
                            `REFRESH_TOKEN` varchar(255) DEFAULT NULL,
                            `USER_ID` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `UK_USER_ID` (`USER_ID`),
                            CONSTRAINT `UK_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3;