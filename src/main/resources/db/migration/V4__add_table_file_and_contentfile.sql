CREATE TABLE FILE (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      NAME VARCHAR(100) NOT NULL,
                      ORIGIN_NAME VARCHAR(100) NOT NULL,
                      EXT VARCHAR(20) NOT NULL,
                      SIZE BIGINT NOT NULL,
                      URL VARCHAR(200) NOT NULL,
                      PATH VARCHAR(200) NOT NULL,
                      CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                      UPDATED_AT DATETIME DEFAULT NULL
);

CREATE TABLE CONTENT_FILE (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      CONTENT_ID BIGINT NOT NULL,
                      FILE_ID BIGINT NOT NULL,
                      DEL_YN ENUM('Y', 'N') DEFAULT 'N' NOT NULL,
                      CONSTRAINT FK_CONTENT_FILE_CONTENT FOREIGN KEY (CONTENT_ID) REFERENCES CONTENT(id),
                      CONSTRAINT FK_CONTENT_FILE_FILE FOREIGN KEY (FILE_ID) REFERENCES FILE(id)
);