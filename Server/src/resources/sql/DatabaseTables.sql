/*
	Database creation script
*/

create schema if not exists pd_trab;

drop table if exists pd_trab.TDirectMessage;
drop table if exists pd_trab.TChannelUsers;
drop table if exists pd_trab.TMessage;
drop table if exists pd_trab.TChannel;
drop table if exists pd_trab.TUser;

/*
	UID = User ID
    UName = User Name
    UUsername = User Username
    ...
*/
create table if not exists pd_trab.TUser (
	UID int auto_increment PRIMARY KEY,
    UName varchar(50) UNIQUE NOT NULL,
    UUsername varchar(25) UNIQUE NOT NULL,
    UPassword varchar(255) NOT NULL,
    UPhoto varchar(512) UNIQUE NOT NULL
);

create table if not exists pd_trab.TChannel (
	CID int auto_increment PRIMARY KEY,
    CUID int,
    CName varchar(50) UNIQUE NOT NULL,
    CDescription varchar(255),
    CPassword varchar(255) NOT NULL,
    CONSTRAINT FK_CUID FOREIGN KEY (CUID) REFERENCES pd_trab.TUser(UID)
);

create table if not exists pd_trab.TMessage (
	MID int auto_increment PRIMARY KEY,
    MUID int,
    MCID int,
    MText varchar(1024),
    MPath varchar(512),
    CONSTRAINT FK_MUID FOREIGN KEY (MUID) REFERENCES pd_trab.TUser(UID),
    CONSTRAINT FK_MCID FOREIGN KEY (MCID) REFERENCES pd_trab.TChannel(CID)
);

create table if not exists pd_trab.TChannelUsers (
	CID int,
    UID int,
    PRIMARY KEY (CID, UID),
    CONSTRAINT FK_CUCID FOREIGN KEY (CID) REFERENCES pd_trab.TChannel(CID),
    CONSTRAINT FK_CUUID FOREIGN KEY (UID) REFERENCES pd_trab.TUser(UID)
);

create table if not exists pd_trab.TDirectMessage (
	DMID int auto_increment PRIMARY KEY,
    DMMID int,
    DMUIDDest int,
    CONSTRAINT FK_DMMID FOREIGN KEY (DMMID) REFERENCES pd_trab.TMessage(MID),
    CONSTRAINT FK_DMUIDDest FOREIGN KEY (DMUIDDest) REFERENCES pd_trab.TUser(UID)
);
