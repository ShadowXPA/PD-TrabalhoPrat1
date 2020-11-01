/*
    Database creation script
*/

create schema if not exists pd_trab;

drop table if exists pd_trab.TDirectMessage;
drop table if exists pd_trab.TChannelMessages;
drop table if exists pd_trab.TChannelUsers;
drop table if exists pd_trab.TMessage;
drop table if exists pd_trab.TChannel;
drop table if exists pd_trab.TUser;

/*
    UID = User ID
    UName = User name
    UUsername = User username
    UPassword = User password (encrypted password)
    UPhoto = User photo (path to photo on the server)
    UDate = User creation date
*/
create table if not exists pd_trab.TUser (
	UID int auto_increment PRIMARY KEY,
    UName varchar(50) UNIQUE NOT NULL,
    UUsername varchar(25) UNIQUE NOT NULL,
    UPassword varchar(255) NOT NULL,
    UPhoto varchar(512),
    UDate bigint NOT NULL
);

/*
    CID = Channel ID
    CUID = Channel User ID (User who created the channel)
    CName = Channel name
    CDescription = Channel description
    CPassowrd = Channel password
    CDate = Channel creation date
*/
create table if not exists pd_trab.TChannel (
	CID int auto_increment PRIMARY KEY,
    CUID int,
    CName varchar(50) UNIQUE NOT NULL,
    CDescription varchar(255),
    CPassword varchar(255),
    CDate bigint NOT NULL,
    CONSTRAINT FK_CUID FOREIGN KEY (CUID) REFERENCES pd_trab.TUser(UID)
);

/*
    MID = Message ID
    MUID = Author ID (User ID)
    MText = Message text (In the case of it being a file this will be the original file name)
    MPath = File path (In case it's a file message)
    MDate = Message creation date
*/
create table if not exists pd_trab.TMessage (
	MID int auto_increment PRIMARY KEY,
    MUID int,
    MText varchar(1024) NOT NULL,
    MPath varchar(512),
    MDate bigint NOT NULL,
    CONSTRAINT FK_MUID FOREIGN KEY (MUID) REFERENCES pd_trab.TUser(UID)
);

/*
    CID = Channel ID (Channel reference)
    UID = User's in a certain channel (User reference)
*/
create table if not exists pd_trab.TChannelUsers (
	CID int,
    UID int,
    PRIMARY KEY (CID, UID),
    CONSTRAINT FK_CUCID FOREIGN KEY (CID) REFERENCES pd_trab.TChannel(CID),
    CONSTRAINT FK_CUUID FOREIGN KEY (UID) REFERENCES pd_trab.TUser(UID)
);

/*
    MID = Message ID (Message reference)
    CID = Channel in which the message was sent to (Channel reference)
*/
create table if not exists pd_trab.TChannelMessages (
	MID int PRIMARY KEY,
    CID int,
    CONSTRAINT FK_CMMID FOREIGN KEY (MID) REFERENCES pd_trab.TMessage(MID),
    CONSTRAINT FK_CMCID FOREIGN KEY (CID) REFERENCES pd_trab.TChannel(CID)
);

/*
    MID = Message ID (Message reference)
    UID = Direct message destinatary (User reference)
*/
create table if not exists pd_trab.TDirectMessage (
    MID int PRIMARY KEY,
    UID int,
    CONSTRAINT FK_DMMID FOREIGN KEY (MID) REFERENCES pd_trab.TMessage(MID),
    CONSTRAINT FK_DMUIDDest FOREIGN KEY (UID) REFERENCES pd_trab.TUser(UID)
);
