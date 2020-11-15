/*
    Database creation script
*/

create schema if not exists pd_trab;
use pd_trab;

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
        ON DELETE CASCADE
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
        ON DELETE CASCADE
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
        ON DELETE CASCADE
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
        ON DELETE CASCADE
);

/*
    Encrypted Passwords: Teste123 -> HvwT6osnO0M/pyh4SFp3hA==
*/
/*
    TUser Dummy Data
    https://mockaroo.com/
*/
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (1, 'Junina Winter', 'jwinter0', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1479405210);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (2, 'Desi Strongman', 'dstrongman1', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1520069188);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (3, 'Giffard Bernlin', 'gbernlin2', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1552881117);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (4, 'Nissie Ledwidge', 'nledwidge3', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1578095295);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (5, 'Lena Cauley', 'lcauley4', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1518309977);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (6, 'Aluino Sawney', 'asawney5', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1580831460);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (7, 'Lou Matchell', 'lmatchell6', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1593028642);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (8, 'Betti Benditt', 'bbenditt7', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1582106426);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (9, 'Harold Latchmore', 'hlatchmore8', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1529110388);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (10, 'Philippine Cornewell', 'pcornewell9', 'HvwT6osnO0M/pyh4SFp3hA==', 'pd_trab_files/avatar/default.png', 1539418798);

/*
    TChannel Dummy Data
    https://mockaroo.com/
*/
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (1, 5, 'Voyatouch', 'Morbi a ipsum.', 'HvwT6osnO0M/pyh4SFp3hA==', 1587563529);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (2, 6, 'Cardify', 'In eleifend quam a odio. In hac habitasse platea dictumst.', 'HvwT6osnO0M/pyh4SFp3hA==', 1530171268);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (3, 7, 'Y-Solowarm', 'Nunc nisl.', null, 1574457112);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (4, 3, 'Latlux', 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit.', null, 1546084627);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (5, 9, 'It', 'Sed accumsan felis.', null, 1547931823);

/*
    TMessage Dummy Data
    https://mockaroo.com/
*/
insert into TMessage (MID, MUID, MText, MPath, MDate) values (1, 2, 'Vestibulum ac est lacinia nisi venenatis tristique.', null, 1496325467);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (2, 2, 'Vivamus tortor. Duis mattis egestas metus.', null, 1511241099);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (3, 7, 'Donec diam neque, vestibulum eget, vulputate ut, ultrices vel, augue.', null, 1510914641);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (4, 1, 'In hac habitasse platea dictumst. Maecenas ut massa quis augue luctus tincidunt.', null, 1529638960);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (5, 4, 'Nulla mollis molestie lorem.', null, 1553953257);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (6, 1, 'Phasellus in felis.', null, 1543612295);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (7, 1, 'Sed accumsan felis.', null, 1496236320);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (8, 3, 'Etiam faucibus cursus urna.', null, 1488202665);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (9, 1, 'Ut tellus. Nulla ut erat id mauris vulputate elementum.', 'pd_trab_files/avatar/default.png', 1484770832);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (10, 7, 'Fusce posuere felis sed lacus.', null, 1532399215);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (11, 1, 'Ut at dolor quis odio consequat varius.', null, 1547062197);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (12, 1, 'Aliquam erat volutpat.', null, 1504539930);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (13, 10, 'Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue.', null, 1584651781);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (14, 1, 'Nunc nisl.', null, 1596403639);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (15, 8, 'Nulla mollis molestie lorem. Quisque ut erat.', null, 1505932834);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (16, 4, 'Sed accumsan felis.', null, 1529520865);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (17, 10, 'Duis consequat dui nec nisi volutpat eleifend.', null, 1556917439);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (18, 6, 'Maecenas tincidunt lacus at velit.', null, 1517196818);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (19, 8, 'Maecenas pulvinar lobortis est.', null, 1484516130);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (20, 5, 'Nam tristique tortor eu pede.', null, 1496909374);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (21, 1, 'Quisque id justo sit amet sapien dignissim vestibulum. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est.', 'pd_trab_files/avatar/default.png', 1530658799);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (22, 1, 'Mauris sit amet eros.', null, 1545483823);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (23, 9, 'Pellentesque ultrices mattis odio. Donec vitae nisi.', null, 1535673530);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (24, 6, 'Etiam faucibus cursus urna.', null, 1569123315);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (25, 6, 'Proin interdum mauris non ligula pellentesque ultrices.', 'pd_trab_files/avatar/default.png', 1581938436);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (26, 1, 'Pellentesque at nulla.', null, 1490673358);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (27, 5, 'Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.', null, 1596563438);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (28, 6, 'Vestibulum ac est lacinia nisi venenatis tristique.', null, 1536374252);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (29, 9, 'Sed sagittis.', null, 1572697284);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (30, 3, 'Praesent lectus.', null, 1577680769);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (31, 10, 'Sed sagittis.', null, 1578611067);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (32, 4, 'Nam tristique tortor eu pede.', null, 1486644997);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (33, 9, 'Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo.', null, 1502601517);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (34, 7, 'Nullam molestie nibh in lectus.', null, 1573263970);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (35, 3, 'Aliquam non mauris.', null, 1497491084);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (36, 2, 'Curabitur in libero ut massa volutpat convallis.', 'pd_trab_files/avatar/default.png', 1594615587);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (37, 2, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Mauris viverra diam vitae quam.', null, 1594628038);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (38, 5, 'Nullam sit amet turpis elementum ligula vehicula consequat.', null, 1556249851);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (39, 3, 'Morbi a ipsum. Integer a nibh.', 'pd_trab_files/avatar/default.png', 1545292842);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (40, 7, 'Morbi non lectus.', 'pd_trab_files/avatar/default.png', 1507569997);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (41, 1, 'Proin risus. Praesent lectus.', 'pd_trab_files/avatar/default.png', 1494164323);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (42, 9, 'In hac habitasse platea dictumst. Etiam faucibus cursus urna.', null, 1495717488);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (43, 7, 'In quis justo.', null, 1505015823);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (44, 5, 'Curabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo.', 'pd_trab_files/avatar/default.png', 1572075519);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (45, 4, 'Pellentesque viverra pede ac diam. Cras pellentesque volutpat dui.', null, 1510741630);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (46, 9, 'Proin leo odio, porttitor id, consequat in, consequat ut, nulla.', null, 1538484888);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (47, 6, 'Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.', null, 1514776262);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (48, 6, 'Nulla suscipit ligula in lacus. Curabitur at ipsum ac tellus semper interdum.', 'pd_trab_files/avatar/default.png', 1549424519);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (49, 6, 'Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros. Vestibulum ac est lacinia nisi venenatis tristique.', null, 1515534523);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (50, 6, 'Nunc purus.', 'pd_trab_files/avatar/default.png', 1541600498);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (51, 9, 'Vivamus vestibulum sagittis sapien. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.', null, 1532733718);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (52, 1, 'Proin at turpis a pede posuere nonummy.', null, 1551999280);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (53, 9, 'Nam ultrices, libero non mattis pulvinar, nulla pede ullamcorper augue, a suscipit nulla elit ac nulla.', null, 1549097261);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (54, 3, 'Morbi ut odio.', null, 1532600430);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (55, 9, 'Praesent blandit lacinia erat. Vestibulum sed magna at nunc commodo placerat.', null, 1523336008);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (56, 8, 'Duis aliquam convallis nunc. Proin at turpis a pede posuere nonummy.', 'pd_trab_files/avatar/default.png', 1522736127);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (57, 6, 'Aliquam erat volutpat.', null, 1573919511);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (58, 5, 'Maecenas pulvinar lobortis est.', null, 1562625126);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (59, 2, 'Integer tincidunt ante vel ipsum. Praesent blandit lacinia erat.', null, 1505898339);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (60, 7, 'Sed vel enim sit amet nunc viverra dapibus. Nulla suscipit ligula in lacus.', 'pd_trab_files/avatar/default.png', 1504367870);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (61, 4, 'Donec quis orci eget orci vehicula condimentum.', null, 1523592575);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (62, 1, 'Proin leo odio, porttitor id, consequat in, consequat ut, nulla.', 'pd_trab_files/avatar/default.png', 1496639842);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (63, 2, 'Praesent blandit.', null, 1518777760);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (64, 4, 'Etiam vel augue.', null, 1500908255);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (65, 1, 'Aliquam quis turpis eget elit sodales scelerisque. Mauris sit amet eros.', null, 1575021805);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (66, 6, 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue.', null, 1547326055);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (67, 8, 'Nullam orci pede, venenatis non, sodales sed, tincidunt eu, felis. Fusce posuere felis sed lacus.', null, 1583407142);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (68, 8, 'Integer non velit.', 'pd_trab_files/avatar/default.png', 1498687478);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (69, 7, 'In quis justo. Maecenas rhoncus aliquam lacus.', null, 1590989478);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (70, 5, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec pharetra, magna vestibulum aliquet ultrices, erat tortor sollicitudin mi, sit amet lobortis sapien sapien non mi.', null, 1558981294);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (71, 1, 'Morbi ut odio.', null, 1480413295);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (72, 10, 'Integer aliquet, massa id lobortis convallis, tortor risus dapibus augue, vel accumsan tellus nisi eu orci.', null, 1576090090);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (73, 3, 'Morbi ut odio. Cras mi pede, malesuada in, imperdiet et, commodo vulputate, justo.', null, 1558061394);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (74, 6, 'Phasellus sit amet erat. Nulla tempus.', 'pd_trab_files/avatar/default.png', 1509194988);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (75, 7, 'Phasellus sit amet erat. Nulla tempus.', null, 1548684906);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (76, 6, 'Nullam varius. Nulla facilisi.', 'pd_trab_files/avatar/default.png', 1500265961);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (77, 5, 'Aliquam quis turpis eget elit sodales scelerisque.', null, 1514610804);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (78, 10, 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit.', 'pd_trab_files/avatar/default.png', 1492515133);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (79, 6, 'Sed accumsan felis. Ut at dolor quis odio consequat varius.', null, 1517508721);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (80, 5, 'Fusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl.', 'pd_trab_files/avatar/default.png', 1494270902);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (81, 3, 'Nam tristique tortor eu pede.', null, 1484454825);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (82, 1, 'Nulla suscipit ligula in lacus.', null, 1603018720);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (83, 7, 'Fusce consequat.', 'pd_trab_files/avatar/default.png', 1587398735);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (84, 7, 'Nam congue, risus semper porta volutpat, quam pede lobortis ligula, sit amet eleifend pede libero quis orci.', null, 1524033204);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (85, 7, 'Nulla tellus. In sagittis dui vel nisl.', null, 1523255566);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (86, 2, 'Vivamus metus arcu, adipiscing molestie, hendrerit at, vulputate vitae, nisl. Aenean lectus.', 'pd_trab_files/avatar/default.png', 1604617888);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (87, 7, 'Quisque arcu libero, rutrum ac, lobortis vel, dapibus at, diam. Nam tristique tortor eu pede.', null, 1590939084);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (88, 4, 'Integer ac leo.', null, 1500413130);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (89, 2, 'Nunc purus.', null, 1480654410);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (90, 1, 'Nulla facilisi.', null, 1557101001);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (91, 7, 'Nulla tellus. In sagittis dui vel nisl.', null, 1484040767);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (92, 10, 'Pellentesque at nulla.', null, 1541469445);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (93, 7, 'Pellentesque viverra pede ac diam. Cras pellentesque volutpat dui.', null, 1585259368);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (94, 5, 'Integer ac leo. Pellentesque ultrices mattis odio.', null, 1522107689);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (95, 9, 'Quisque arcu libero, rutrum ac, lobortis vel, dapibus at, diam.', null, 1524074784);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (96, 10, 'Nulla tellus.', null, 1522230755);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (97, 6, 'In est risus, auctor sed, tristique in, tempus sit amet, sem.', null, 1488792541);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (98, 3, 'Morbi non quam nec dui luctus rutrum.', null, 1558493429);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (99, 5, 'Aenean sit amet justo. Morbi ut odio.', null, 1524442237);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (100, 3, 'Nulla ac enim.', null, 1580071594);

/*
    TChannelUsers Dummy Data
    https://mockaroo.com/
*/
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
*/
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
*/
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
