CREATE TABLE USER (
                      `ID` varchar(255) NOT NULL,
                      `PW` varchar(255) NOT NULL,
                      `EMAIL` varchar(255) DEFAULT NULL,
                      `NICK_NAME` varchar(255) DEFAULT NULL,
                      `DEL_YN` enum('Y','N') NOT NULL DEFAULT 'N',
                      `CREATED_AT` datetime DEFAULT CURRENT_TIMESTAMP,
                      `UPDATED_AT` datetime DEFAULT NULL,
                      PRIMARY KEY (`ID`)
) ENGINE=InnoDB;