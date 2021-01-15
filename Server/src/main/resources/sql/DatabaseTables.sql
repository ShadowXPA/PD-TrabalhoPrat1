/*
    Database creation script
*/

create schema if not exists pd_trab;
create schema if not exists pd_trab2;
create schema if not exists pd_trab3;
use pd_trab;

drop user if exists 'pdtrab';
create user if not exists 'pdtrab'@'%' identified by 'PDTrabPrat123';
grant all privileges on pd_trab.* to 'pdtrab';
grant all privileges on pd_trab2.* to 'pdtrab';
grant all privileges on pd_trab3.* to 'pdtrab';

drop table if exists TDirectMessage;
drop table if exists TChannelMessages;
drop table if exists TChannelUsers;
drop table if exists TMessage;
drop table if exists TChannel;
drop table if exists TUser;

/*
    UID = User ID
    UName = User name
    UUsername = User username
    UPassword = User password (encrypted password)
    UPhoto = User photo (path to photo on the server)
    UDate = User creation date
*/
CREATE TABLE IF NOT EXISTS TUser (
    UID INT AUTO_INCREMENT PRIMARY KEY,
    UName VARCHAR(50) UNIQUE NOT NULL,
    UUsername VARCHAR(25) UNIQUE NOT NULL,
    UPassword VARCHAR(255) NOT NULL,
    UPhoto VARCHAR(512),
    UDate BIGINT NOT NULL
);

/*
    CID = Channel ID
    CUID = Channel User ID (User who created the channel)
    CName = Channel name
    CDescription = Channel description
    CPassowrd = Channel password
    CDate = Channel creation date
*/
CREATE TABLE IF NOT EXISTS TChannel (
    CID INT AUTO_INCREMENT PRIMARY KEY,
    CUID INT,
    CName VARCHAR(50) UNIQUE NOT NULL,
    CDescription VARCHAR(255),
    CPassword VARCHAR(255),
    CDate BIGINT NOT NULL,
    CONSTRAINT FK_CUID FOREIGN KEY (CUID)
        REFERENCES TUser (UID)
);

/*
    MID = Message ID
    MUID = Author ID (User ID)
    MText = Message text (In the case of it being a file this will be the original file name)
    MPath = File path (In case it's a file message)
    MDate = Message creation date
*/
CREATE TABLE IF NOT EXISTS TMessage (
    MID INT AUTO_INCREMENT PRIMARY KEY,
    MUID INT,
    MText VARCHAR(1024) NOT NULL,
    MPath VARCHAR(512),
    MDate BIGINT NOT NULL,
    CONSTRAINT FK_MUID FOREIGN KEY (MUID)
        REFERENCES TUser (UID)
);

/*
    CID = Channel ID (Channel reference)
    UID = User's in a certain channel (User reference)
*/
CREATE TABLE IF NOT EXISTS TChannelUsers (
    CID INT,
    UID INT,
    PRIMARY KEY (CID , UID),
    CONSTRAINT FK_CUCID FOREIGN KEY (CID)
        REFERENCES TChannel (CID)
        ON DELETE CASCADE,
    CONSTRAINT FK_CUUID FOREIGN KEY (UID)
        REFERENCES TUser (UID)
);

/*
    MID = Message ID (Message reference)
    CID = Channel in which the message was sent to (Channel reference)
*/
CREATE TABLE IF NOT EXISTS TChannelMessages (
    MID INT PRIMARY KEY,
    CID INT,
    CONSTRAINT FK_CMMID FOREIGN KEY (MID)
        REFERENCES TMessage (MID)
        ON DELETE CASCADE,
    CONSTRAINT FK_CMCID FOREIGN KEY (CID)
        REFERENCES TChannel (CID)
        ON DELETE CASCADE
);

/*
    MID = Message ID (Message reference)
    UID = Direct message destinatary (User reference)
*/
CREATE TABLE IF NOT EXISTS TDirectMessage (
    MID INT PRIMARY KEY,
    UID INT,
    CONSTRAINT FK_DMMID FOREIGN KEY (MID)
        REFERENCES TMessage (MID)
        ON DELETE CASCADE,
    CONSTRAINT FK_DMUIDDest FOREIGN KEY (UID)
        REFERENCES TUser (UID)
);

/*
    Encrypted Passwords: Teste123 -> HvwT6osnO0M/pyh4SFp3hA==
*/
/*
    TUser Dummy Data
    https://mockaroo.com/

insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (1, 'Junina Winter', 'jwinter0', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (2, 'Desi Strongman', 'dstrongman1', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (3, 'Giffard Bernlin', 'gbernlin2', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (4, 'Nissie Ledwidge', 'nledwidge3', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (5, 'Lena Cauley', 'lcauley4', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (6, 'Aluino Sawney', 'asawney5', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (7, 'Lou Matchell', 'lmatchell6', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (8, 'Betti Benditt', 'bbenditt7', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (9, 'Harold Latchmore', 'hlatchmore8', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (10, 'Philippine Cornewell', 'pcornewell9', 'HvwT6osnO0M/pyh4SFp3hA==', 'avatar/default.png', 1447607218000);

/*
    TChannel Dummy Data
    https://mockaroo.com/

insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (1, 5, 'Voyatouch', 'Morbi a ipsum.', 'HvwT6osnO0M/pyh4SFp3hA==', 1447607218000);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (2, 6, 'Cardify', 'In eleifend quam a odio. In hac habitasse platea dictumst.', 'HvwT6osnO0M/pyh4SFp3hA==', 1447607218000);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (3, 7, 'Y-Solowarm', 'Nunc nisl.', null, 1447607218000);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (4, 3, 'Latlux', 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit.', null, 1447607218000);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (5, 9, 'It', 'Sed accumsan felis.', null, 1447607218000);

/*
    TMessage Dummy Data
    https://mockaroo.com/

insert into TMessage (MID, MUID, MText, MPath, MDate) values (1, 3, 'Curabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo.', null, 1533897033920);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (2, 4, 'helloworld.txt', 'files/9fbe699b-8f1c-490b-b08b-06a4a156be13.txt', 1508689424151);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (3, 4, 'In hac habitasse platea dictumst. Morbi vestibulum, velit id pretium iaculis, diam erat fermentum justo, nec condimentum neque sapien placerat ante.', null, 1596150225823);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (4, 9, 'Integer a nibh.', null, 1502227070573);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (5, 2, 'Integer tincidunt ante vel ipsum.', null, 1604267931168);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (6, 9, 'Duis aliquam convallis nunc.', null, 1481097349654);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (7, 2, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros.', null, 1578014190805);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (8, 7, 'Nulla tellus. In sagittis dui vel nisl.', null, 1505688815813);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (9, 1, 'Integer pede justo, lacinia eget, tincidunt eget, tempus vel, pede.', null, 1601387577096);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (10, 10, 'Suspendisse ornare consequat lectus. In est risus, auctor sed, tristique in, tempus sit amet, sem.', null, 1581924879330);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (11, 3, 'Curabitur gravida nisi at nibh. In hac habitasse platea dictumst.', null, 1544347703610);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (12, 5, 'Nunc nisl.', null, 1507553033002);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (13, 10, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec pharetra, magna vestibulum aliquet ultrices, erat tortor sollicitudin mi, sit amet lobortis sapien sapien non mi. Integer ac neque.', null, 1510778840749);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (14, 4, 'alpha.txt', 'files/11a9aee0-f3b0-4062-98f3-c85e85ba19e4.txt', 1594229019918);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (15, 8, 'Praesent lectus. Vestibulum quam sapien, varius ut, blandit non, interdum in, ante.', null, 1601995953351);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (16, 6, 'Nullam porttitor lacus at turpis. Donec posuere metus vitae ipsum.', null, 1544405784246);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (17, 9, 'Pellentesque at nulla.', null, 1568812381462);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (18, 8, 'guids.txt', 'files/dd331510-dbce-4f3d-8737-8801db74d568.txt', 1567573333079);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (19, 7, 'Nam dui.', null, 1483984154507);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (20, 5, 'Proin interdum mauris non ligula pellentesque ultrices.', null, 1596843096098);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (21, 7, 'alpha.txt', 'files/11a9aee0-f3b0-4062-98f3-c85e85ba19e4.txt', 1588638847459);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (22, 6, 'Integer pede justo, lacinia eget, tincidunt eget, tempus vel, pede.', null, 1563805614778);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (23, 6, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros.', null, 1487657142761);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (24, 10, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros.', null, 1506789089458);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (25, 2, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros.', null, 1563543053010);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (26, 2, 'Vivamus vestibulum sagittis sapien. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.', null, 1485765120947);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (27, 6, 'Aenean auctor gravida sem. Praesent id massa id nisl venenatis lacinia.', null, 1529461793461);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (28, 6, 'Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh. Quisque id justo sit amet sapien dignissim vestibulum.', null, 1562599000878);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (29, 10, 'Morbi ut odio.', null, 1503174442824);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (30, 1, 'Cras pellentesque volutpat dui.', null, 1547367686049);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (31, 1, 'alpha.txt', 'files/11a9aee0-f3b0-4062-98f3-c85e85ba19e4.txt', 1514452984916);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (32, 4, 'Proin risus. Praesent lectus.', null, 1487436146784);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (33, 10, 'In est risus, auctor sed, tristique in, tempus sit amet, sem.', null, 1598623219627);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (34, 9, 'In hac habitasse platea dictumst.', null, 1588025231223);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (35, 4, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis faucibus accumsan odio.', null, 1529749919577);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (36, 8, 'Suspendisse ornare consequat lectus.', null, 1496399523854);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (37, 10, 'Integer aliquet, massa id lobortis convallis, tortor risus dapibus augue, vel accumsan tellus nisi eu orci. Mauris lacinia sapien quis libero.', null, 1574535333240);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (38, 8, 'In hac habitasse platea dictumst. Morbi vestibulum, velit id pretium iaculis, diam erat fermentum justo, nec condimentum neque sapien placerat ante.', null, 1551885875884);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (39, 9, 'Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl.', null, 1531561041086);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (40, 2, 'Maecenas leo odio, condimentum id, luctus nec, molestie sed, justo.', null, 1596641544828);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (41, 10, 'Donec ut mauris eget massa tempor convallis. Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh.', null, 1578928532033);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (42, 4, 'Suspendisse ornare consequat lectus. In est risus, auctor sed, tristique in, tempus sit amet, sem.', null, 1513102625595);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (43, 10, 'Vestibulum rutrum rutrum neque. Aenean auctor gravida sem.', null, 1499111952952);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (44, 8, 'Integer pede justo, lacinia eget, tincidunt eget, tempus vel, pede.', null, 1491537248110);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (45, 8, 'guids.txt', 'files/dd331510-dbce-4f3d-8737-8801db74d568.txt', 1481550396553);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (46, 7, 'In hac habitasse platea dictumst. Maecenas ut massa quis augue luctus tincidunt.', null, 1574567982194);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (47, 7, 'Curabitur in libero ut massa volutpat convallis.', null, 1496749800377);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (48, 8, 'Duis mattis egestas metus. Aenean fermentum.', null, 1548634514013);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (49, 7, 'Nunc rhoncus dui vel sem. Sed sagittis.', null, 1565544110756);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (50, 6, 'Integer non velit.', null, 1561514333612);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (51, 2, 'helloworld.txt', 'files/9fbe699b-8f1c-490b-b08b-06a4a156be13.txt', 1557302649322);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (52, 7, 'alpha.txt', 'files/11a9aee0-f3b0-4062-98f3-c85e85ba19e4.txt', 1564396457447);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (53, 2, 'Sed sagittis.', null, 1590576318669);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (54, 8, 'Fusce consequat.', null, 1590771310908);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (55, 10, 'Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh. Quisque id justo sit amet sapien dignissim vestibulum.', null, 1500920682211);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (56, 6, 'Morbi porttitor lorem id ligula. Suspendisse ornare consequat lectus.', null, 1516841700208);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (57, 2, 'Praesent blandit. Nam nulla.', null, 1543575801108);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (58, 6, 'helloworld.txt', 'files/9fbe699b-8f1c-490b-b08b-06a4a156be13.txt', 1599357073004);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (59, 7, 'Duis at velit eu est congue elementum.', null, 1569408761332);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (60, 3, 'Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.', null, 1596882297742);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (61, 3, 'Duis consequat dui nec nisi volutpat eleifend.', null, 1481389844449);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (62, 10, 'Fusce consequat. Nulla nisl.', null, 1494718709404);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (63, 4, 'helloworld.txt', 'files/9fbe699b-8f1c-490b-b08b-06a4a156be13.txt', 1599756193077);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (64, 1, 'Maecenas pulvinar lobortis est. Phasellus sit amet erat.', null, 1528413883096);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (65, 9, 'helloworld.txt', 'files/9fbe699b-8f1c-490b-b08b-06a4a156be13.txt', 1570056093512);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (66, 9, 'guids.txt', 'files/dd331510-dbce-4f3d-8737-8801db74d568.txt', 1532666034839);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (67, 3, 'Etiam vel augue. Vestibulum rutrum rutrum neque.', null, 1515991261254);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (68, 6, 'alpha.txt', 'files/11a9aee0-f3b0-4062-98f3-c85e85ba19e4.txt', 1565264147937);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (69, 7, 'Nulla ut erat id mauris vulputate elementum. Nullam varius.', null, 1568911399346);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (70, 3, 'Duis consequat dui nec nisi volutpat eleifend.', null, 1492400138356);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (71, 9, 'Pellentesque eget nunc. Donec quis orci eget orci vehicula condimentum.', null, 1557553145883);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (72, 1, 'Donec quis orci eget orci vehicula condimentum. Curabitur in libero ut massa volutpat convallis.', null, 1590365573145);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (73, 4, 'Duis ac nibh. Fusce lacus purus, aliquet at, feugiat non, pretium quis, lectus.', null, 1498655152193);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (74, 10, 'Donec vitae nisi. Nam ultrices, libero non mattis pulvinar, nulla pede ullamcorper augue, a suscipit nulla elit ac nulla.', null, 1537922342872);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (75, 1, 'Quisque ut erat. Curabitur gravida nisi at nibh.', null, 1563538954455);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (76, 4, 'Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat.', null, 1602116159221);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (77, 3, 'Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.', null, 1523440433646);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (78, 2, 'Ut tellus.', null, 1583286602550);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (79, 8, 'Duis aliquam convallis nunc.', null, 1505526972480);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (80, 1, 'Pellentesque viverra pede ac diam. Cras pellentesque volutpat dui.', null, 1510039293960);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (81, 3, 'Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.', null, 1496594360148);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (82, 5, 'Morbi ut odio.', null, 1508624063555);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (83, 10, 'In sagittis dui vel nisl. Duis ac nibh.', null, 1551446024041);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (84, 7, 'guids.txt', 'files/dd331510-dbce-4f3d-8737-8801db74d568.txt', 1514383020954);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (85, 4, 'Aenean lectus.', null, 1596207894179);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (86, 5, 'helloworld.txt', 'files/9fbe699b-8f1c-490b-b08b-06a4a156be13.txt', 1538576274304);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (87, 6, 'Duis bibendum.', null, 1528533275088);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (88, 6, 'alpha.txt', 'files/11a9aee0-f3b0-4062-98f3-c85e85ba19e4.txt', 1552172805839);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (89, 8, 'Nullam varius. Nulla facilisi.', null, 1596797940166);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (90, 1, 'In hac habitasse platea dictumst.', null, 1498206411180);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (91, 7, 'guids.txt', 'files/dd331510-dbce-4f3d-8737-8801db74d568.txt', 1584746956917);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (92, 2, 'Aenean auctor gravida sem.', null, 1567289102803);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (93, 6, 'Sed accumsan felis. Ut at dolor quis odio consequat varius.', null, 1487109605455);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (94, 1, 'Integer tincidunt ante vel ipsum. Praesent blandit lacinia erat.', null, 1565165630709);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (95, 8, 'Phasellus in felis.', null, 1585704124805);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (96, 3, 'Etiam pretium iaculis justo.', null, 1596051797671);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (97, 4, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est.', null, 1488966406282);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (98, 8, 'In hac habitasse platea dictumst.', null, 1491316225917);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (99, 6, 'Ut at dolor quis odio consequat varius. Integer ac leo.', null, 1549537188075);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (100, 5, 'Donec ut mauris eget massa tempor convallis.', null, 1504275156486);

/*
    TChannelUsers Dummy Data
    https://mockaroo.com/

insert into TChannelUsers (CID, UID) values (1, 2);
insert into TChannelUsers (CID, UID) values (2, 5);
insert into TChannelUsers (CID, UID) values (3, 2);
insert into TChannelUsers (CID, UID) values (4, 5);
insert into TChannelUsers (CID, UID) values (5, 5);
insert into TChannelUsers (CID, UID) values (2, 1);
insert into TChannelUsers (CID, UID) values (3, 9);
insert into TChannelUsers (CID, UID) values (4, 10);
insert into TChannelUsers (CID, UID) values (5, 4);

/*
    TChannelMessages Dummy Data
    https://mockaroo.com/

insert into TChannelMessages (MID, CID) values (1, 4);
insert into TChannelMessages (MID, CID) values (2, 3);
insert into TChannelMessages (MID, CID) values (3, 3);
insert into TChannelMessages (MID, CID) values (4, 3);
insert into TChannelMessages (MID, CID) values (5, 2);
insert into TChannelMessages (MID, CID) values (6, 5);
insert into TChannelMessages (MID, CID) values (7, 2);
insert into TChannelMessages (MID, CID) values (8, 3);
insert into TChannelMessages (MID, CID) values (9, 2);
insert into TChannelMessages (MID, CID) values (10, 5);
insert into TChannelMessages (MID, CID) values (11, 5);
insert into TChannelMessages (MID, CID) values (12, 1);
insert into TChannelMessages (MID, CID) values (13, 3);
insert into TChannelMessages (MID, CID) values (14, 2);
insert into TChannelMessages (MID, CID) values (15, 3);
insert into TChannelMessages (MID, CID) values (16, 4);
insert into TChannelMessages (MID, CID) values (17, 2);
insert into TChannelMessages (MID, CID) values (18, 3);
insert into TChannelMessages (MID, CID) values (19, 5);
insert into TChannelMessages (MID, CID) values (20, 2);
insert into TChannelMessages (MID, CID) values (21, 5);
insert into TChannelMessages (MID, CID) values (22, 2);
insert into TChannelMessages (MID, CID) values (23, 5);
insert into TChannelMessages (MID, CID) values (24, 1);
insert into TChannelMessages (MID, CID) values (25, 5);
insert into TChannelMessages (MID, CID) values (26, 1);
insert into TChannelMessages (MID, CID) values (27, 1);
insert into TChannelMessages (MID, CID) values (28, 5);
insert into TChannelMessages (MID, CID) values (29, 1);
insert into TChannelMessages (MID, CID) values (30, 2);
insert into TChannelMessages (MID, CID) values (31, 5);
insert into TChannelMessages (MID, CID) values (32, 5);
insert into TChannelMessages (MID, CID) values (33, 5);
insert into TChannelMessages (MID, CID) values (34, 3);
insert into TChannelMessages (MID, CID) values (35, 2);
insert into TChannelMessages (MID, CID) values (36, 5);
insert into TChannelMessages (MID, CID) values (37, 5);
insert into TChannelMessages (MID, CID) values (38, 2);
insert into TChannelMessages (MID, CID) values (39, 3);
insert into TChannelMessages (MID, CID) values (40, 1);
insert into TChannelMessages (MID, CID) values (41, 5);
insert into TChannelMessages (MID, CID) values (42, 2);
insert into TChannelMessages (MID, CID) values (43, 2);
insert into TChannelMessages (MID, CID) values (44, 4);
insert into TChannelMessages (MID, CID) values (45, 2);
insert into TChannelMessages (MID, CID) values (46, 3);
insert into TChannelMessages (MID, CID) values (47, 5);
insert into TChannelMessages (MID, CID) values (48, 2);
insert into TChannelMessages (MID, CID) values (49, 4);
insert into TChannelMessages (MID, CID) values (50, 2);
insert into TChannelMessages (MID, CID) values (51, 2);
insert into TChannelMessages (MID, CID) values (52, 2);
insert into TChannelMessages (MID, CID) values (53, 3);
insert into TChannelMessages (MID, CID) values (54, 1);
insert into TChannelMessages (MID, CID) values (55, 3);
insert into TChannelMessages (MID, CID) values (56, 3);
insert into TChannelMessages (MID, CID) values (57, 2);
insert into TChannelMessages (MID, CID) values (58, 1);
insert into TChannelMessages (MID, CID) values (59, 3);
insert into TChannelMessages (MID, CID) values (60, 4);
insert into TChannelMessages (MID, CID) values (61, 3);
insert into TChannelMessages (MID, CID) values (62, 2);
insert into TChannelMessages (MID, CID) values (63, 4);
insert into TChannelMessages (MID, CID) values (64, 3);
insert into TChannelMessages (MID, CID) values (65, 5);
insert into TChannelMessages (MID, CID) values (66, 4);
insert into TChannelMessages (MID, CID) values (67, 2);
insert into TChannelMessages (MID, CID) values (68, 5);
insert into TChannelMessages (MID, CID) values (69, 5);
insert into TChannelMessages (MID, CID) values (70, 1);
insert into TChannelMessages (MID, CID) values (71, 3);
insert into TChannelMessages (MID, CID) values (72, 1);
insert into TChannelMessages (MID, CID) values (73, 1);
insert into TChannelMessages (MID, CID) values (74, 3);
insert into TChannelMessages (MID, CID) values (75, 5);

/*
    TDirectMessage Dummy Data
    https://mockaroo.com/

insert into TDirectMessage (MID, UID) values (76, 8);
insert into TDirectMessage (MID, UID) values (77, 6);
insert into TDirectMessage (MID, UID) values (78, 7);
insert into TDirectMessage (MID, UID) values (79, 7);
insert into TDirectMessage (MID, UID) values (80, 10);
insert into TDirectMessage (MID, UID) values (81, 8);
insert into TDirectMessage (MID, UID) values (82, 8);
insert into TDirectMessage (MID, UID) values (83, 4);
insert into TDirectMessage (MID, UID) values (84, 2);
insert into TDirectMessage (MID, UID) values (85, 2);
insert into TDirectMessage (MID, UID) values (86, 6);
insert into TDirectMessage (MID, UID) values (87, 9);
insert into TDirectMessage (MID, UID) values (88, 7);
insert into TDirectMessage (MID, UID) values (89, 9);
insert into TDirectMessage (MID, UID) values (90, 1);
insert into TDirectMessage (MID, UID) values (91, 2);
insert into TDirectMessage (MID, UID) values (92, 3);
insert into TDirectMessage (MID, UID) values (93, 2);
insert into TDirectMessage (MID, UID) values (94, 6);
insert into TDirectMessage (MID, UID) values (95, 5);
insert into TDirectMessage (MID, UID) values (96, 7);
insert into TDirectMessage (MID, UID) values (97, 7);
insert into TDirectMessage (MID, UID) values (98, 4);
insert into TDirectMessage (MID, UID) values (99, 6);
insert into TDirectMessage (MID, UID) values (100, 2);

/*
    Database 2 and 3 creation script
*/

use pd_trab2;

drop table if exists TDirectMessage;
drop table if exists TChannelMessages;
drop table if exists TChannelUsers;
drop table if exists TMessage;
drop table if exists TChannel;
drop table if exists TUser;

/*
    UID = User ID
    UName = User name
    UUsername = User username
    UPassword = User password (encrypted password)
    UPhoto = User photo (path to photo on the server)
    UDate = User creation date
*/
CREATE TABLE IF NOT EXISTS TUser (
    UID INT AUTO_INCREMENT PRIMARY KEY,
    UName VARCHAR(50) UNIQUE NOT NULL,
    UUsername VARCHAR(25) UNIQUE NOT NULL,
    UPassword VARCHAR(255) NOT NULL,
    UPhoto VARCHAR(512),
    UDate BIGINT NOT NULL
);

/*
    CID = Channel ID
    CUID = Channel User ID (User who created the channel)
    CName = Channel name
    CDescription = Channel description
    CPassowrd = Channel password
    CDate = Channel creation date
*/
CREATE TABLE IF NOT EXISTS TChannel (
    CID INT AUTO_INCREMENT PRIMARY KEY,
    CUID INT,
    CName VARCHAR(50) UNIQUE NOT NULL,
    CDescription VARCHAR(255),
    CPassword VARCHAR(255),
    CDate BIGINT NOT NULL,
    CONSTRAINT FK_CUID FOREIGN KEY (CUID)
        REFERENCES TUser (UID)
);

/*
    MID = Message ID
    MUID = Author ID (User ID)
    MText = Message text (In the case of it being a file this will be the original file name)
    MPath = File path (In case it's a file message)
    MDate = Message creation date
*/
CREATE TABLE IF NOT EXISTS TMessage (
    MID INT AUTO_INCREMENT PRIMARY KEY,
    MUID INT,
    MText VARCHAR(1024) NOT NULL,
    MPath VARCHAR(512),
    MDate BIGINT NOT NULL,
    CONSTRAINT FK_MUID FOREIGN KEY (MUID)
        REFERENCES TUser (UID)
);

/*
    CID = Channel ID (Channel reference)
    UID = User's in a certain channel (User reference)
*/
CREATE TABLE IF NOT EXISTS TChannelUsers (
    CID INT,
    UID INT,
    PRIMARY KEY (CID , UID),
    CONSTRAINT FK_CUCID FOREIGN KEY (CID)
        REFERENCES TChannel (CID)
        ON DELETE CASCADE,
    CONSTRAINT FK_CUUID FOREIGN KEY (UID)
        REFERENCES TUser (UID)
);

/*
    MID = Message ID (Message reference)
    CID = Channel in which the message was sent to (Channel reference)
*/
CREATE TABLE IF NOT EXISTS TChannelMessages (
    MID INT PRIMARY KEY,
    CID INT,
    CONSTRAINT FK_CMMID FOREIGN KEY (MID)
        REFERENCES TMessage (MID)
        ON DELETE CASCADE,
    CONSTRAINT FK_CMCID FOREIGN KEY (CID)
        REFERENCES TChannel (CID)
        ON DELETE CASCADE
);

/*
    MID = Message ID (Message reference)
    UID = Direct message destinatary (User reference)
*/
CREATE TABLE IF NOT EXISTS TDirectMessage (
    MID INT PRIMARY KEY,
    UID INT,
    CONSTRAINT FK_DMMID FOREIGN KEY (MID)
        REFERENCES TMessage (MID)
        ON DELETE CASCADE,
    CONSTRAINT FK_DMUIDDest FOREIGN KEY (UID)
        REFERENCES TUser (UID)
);

use pd_trab3;

drop table if exists TDirectMessage;
drop table if exists TChannelMessages;
drop table if exists TChannelUsers;
drop table if exists TMessage;
drop table if exists TChannel;
drop table if exists TUser;

/*
    UID = User ID
    UName = User name
    UUsername = User username
    UPassword = User password (encrypted password)
    UPhoto = User photo (path to photo on the server)
    UDate = User creation date
*/
CREATE TABLE IF NOT EXISTS TUser (
    UID INT AUTO_INCREMENT PRIMARY KEY,
    UName VARCHAR(50) UNIQUE NOT NULL,
    UUsername VARCHAR(25) UNIQUE NOT NULL,
    UPassword VARCHAR(255) NOT NULL,
    UPhoto VARCHAR(512),
    UDate BIGINT NOT NULL
);

/*
    CID = Channel ID
    CUID = Channel User ID (User who created the channel)
    CName = Channel name
    CDescription = Channel description
    CPassowrd = Channel password
    CDate = Channel creation date
*/
CREATE TABLE IF NOT EXISTS TChannel (
    CID INT AUTO_INCREMENT PRIMARY KEY,
    CUID INT,
    CName VARCHAR(50) UNIQUE NOT NULL,
    CDescription VARCHAR(255),
    CPassword VARCHAR(255),
    CDate BIGINT NOT NULL,
    CONSTRAINT FK_CUID FOREIGN KEY (CUID)
        REFERENCES TUser (UID)
);

/*
    MID = Message ID
    MUID = Author ID (User ID)
    MText = Message text (In the case of it being a file this will be the original file name)
    MPath = File path (In case it's a file message)
    MDate = Message creation date
*/
CREATE TABLE IF NOT EXISTS TMessage (
    MID INT AUTO_INCREMENT PRIMARY KEY,
    MUID INT,
    MText VARCHAR(1024) NOT NULL,
    MPath VARCHAR(512),
    MDate BIGINT NOT NULL,
    CONSTRAINT FK_MUID FOREIGN KEY (MUID)
        REFERENCES TUser (UID)
);

/*
    CID = Channel ID (Channel reference)
    UID = User's in a certain channel (User reference)
*/
CREATE TABLE IF NOT EXISTS TChannelUsers (
    CID INT,
    UID INT,
    PRIMARY KEY (CID , UID),
    CONSTRAINT FK_CUCID FOREIGN KEY (CID)
        REFERENCES TChannel (CID)
        ON DELETE CASCADE,
    CONSTRAINT FK_CUUID FOREIGN KEY (UID)
        REFERENCES TUser (UID)
);

/*
    MID = Message ID (Message reference)
    CID = Channel in which the message was sent to (Channel reference)
*/
CREATE TABLE IF NOT EXISTS TChannelMessages (
    MID INT PRIMARY KEY,
    CID INT,
    CONSTRAINT FK_CMMID FOREIGN KEY (MID)
        REFERENCES TMessage (MID)
        ON DELETE CASCADE,
    CONSTRAINT FK_CMCID FOREIGN KEY (CID)
        REFERENCES TChannel (CID)
        ON DELETE CASCADE
);

/*
    MID = Message ID (Message reference)
    UID = Direct message destinatary (User reference)
*/
CREATE TABLE IF NOT EXISTS TDirectMessage (
    MID INT PRIMARY KEY,
    UID INT,
    CONSTRAINT FK_DMMID FOREIGN KEY (MID)
        REFERENCES TMessage (MID)
        ON DELETE CASCADE,
    CONSTRAINT FK_DMUIDDest FOREIGN KEY (UID)
        REFERENCES TUser (UID)
);

use pd_trab;
