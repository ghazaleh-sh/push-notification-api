create table FIREBASE_USER
(
    userId          bigint auto_increment primary key,

    fcmToken        varchar(200),
    nationalCode    varchar(200),
    mobileNumber    varchar(200),
    applicationName varchar(200),
    userStatus      varchar(200),
    serialId        varchar(200),
    userPlatform    varchar(200),
    isTrusted       boolean
)
