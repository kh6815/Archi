CREATE TABLE USER (
                      `ID` varchar(255) NOT NULL,
                      `PW` varchar(255) NOT NULL,
                      `EMAIL` varchar(255) DEFAULT NULL,
                      `NICK_NAME` varchar(255) DEFAULT NULL,
                      `DEL_YN` ENUM('Y','N') NOT NULL DEFAULT 'N',
                      `CREATED_AT` DATETIME DEFAULT CURRENT_TIMESTAMP,
                      `UPDATED_AT` DATETIME DEFAULT NULL,
                      PRIMARY KEY (`ID`),
                      CONSTRAINT UK_USER_NICK_NAME UNIQUE (`NICK_NAME`)
) ENGINE=InnoDB;

CREATE TABLE CATEGORY (
                          ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                          PARENTS_CATEGORY_ID BIGINT,
                          CATEGORY_NAME VARCHAR(255) NOT NULL,
                          CONSTRAINT FK_CATEGORY_PARENT FOREIGN KEY (`PARENTS_CATEGORY_ID`) REFERENCES CATEGORY(`ID`)
);
-- 인덱스 추가 (필요에 따라)
-- CREATE INDEX IDX_CATEGORY_PARENT ON CATEGORY(PARENTS_CATEGORY_ID);

CREATE TABLE CONTENT (
                         ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         USER_ID VARCHAR(255) NOT NULL,
                         CATEGORY_ID BIGINT NOT NULL,
                         TITLE VARCHAR(255) NOT NULL,
                         CONTENT TEXT NOT NULL,
                         `LIKE` INT DEFAULT 0,
                         `DEL_YN` ENUM('Y','N') NOT NULL DEFAULT 'N',
                         `CREATED_AT` DATETIME DEFAULT CURRENT_TIMESTAMP,
                         `UPDATED_AT` DATETIME DEFAULT NULL,
                         CONSTRAINT FK_CONTENT_USER FOREIGN KEY (`USER_ID`) REFERENCES USER(`ID`),
                         CONSTRAINT FK_CONTENT_CATEGORY FOREIGN KEY (`CATEGORY_ID`) REFERENCES CATEGORY(`ID`)
);

-- 인덱스 추가 (필요에 따라)
-- CREATE INDEX IDX_CONTENT_USER ON CONTENT(USER_ID);
-- CREATE INDEX IDX_CONTENT_CATEGORY ON CONTENT(CATEGORY_ID);