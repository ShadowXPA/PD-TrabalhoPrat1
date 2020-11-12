/*
    Database creation script
*/

create schema if not exists pd_trab;
use pd_trab;

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
CREATE TABLE IF NOT EXISTS pd_trab.TUser (
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
CREATE TABLE IF NOT EXISTS pd_trab.TChannel (
    CID INT AUTO_INCREMENT PRIMARY KEY,
    CUID INT,
    CName VARCHAR(50) UNIQUE NOT NULL,
    CDescription VARCHAR(255),
    CPassword VARCHAR(255),
    CDate BIGINT NOT NULL,
    CONSTRAINT FK_CUID FOREIGN KEY (CUID)
        REFERENCES pd_trab.TUser (UID)
        ON DELETE CASCADE
);

/*
    MID = Message ID
    MUID = Author ID (User ID)
    MText = Message text (In the case of it being a file this will be the original file name)
    MPath = File path (In case it's a file message)
    MDate = Message creation date
*/
CREATE TABLE IF NOT EXISTS pd_trab.TMessage (
    MID INT AUTO_INCREMENT PRIMARY KEY,
    MUID INT,
    MText VARCHAR(1024) NOT NULL,
    MPath VARCHAR(512),
    MDate BIGINT NOT NULL,
    CONSTRAINT FK_MUID FOREIGN KEY (MUID)
        REFERENCES pd_trab.TUser (UID)
        ON DELETE CASCADE
);

/*
    CID = Channel ID (Channel reference)
    UID = User's in a certain channel (User reference)
*/
CREATE TABLE IF NOT EXISTS pd_trab.TChannelUsers (
    CID INT,
    UID INT,
    PRIMARY KEY (CID , UID),
    CONSTRAINT FK_CUCID FOREIGN KEY (CID)
        REFERENCES pd_trab.TChannel (CID)
        ON DELETE CASCADE,
    CONSTRAINT FK_CUUID FOREIGN KEY (UID)
        REFERENCES pd_trab.TUser (UID)
        ON DELETE CASCADE
);

/*
    MID = Message ID (Message reference)
    CID = Channel in which the message was sent to (Channel reference)
*/
CREATE TABLE IF NOT EXISTS pd_trab.TChannelMessages (
    MID INT PRIMARY KEY,
    CID INT,
    CONSTRAINT FK_CMMID FOREIGN KEY (MID)
        REFERENCES pd_trab.TMessage (MID)
        ON DELETE CASCADE,
    CONSTRAINT FK_CMCID FOREIGN KEY (CID)
        REFERENCES pd_trab.TChannel (CID)
        ON DELETE CASCADE
);

/*
    MID = Message ID (Message reference)
    UID = Direct message destinatary (User reference)
*/
CREATE TABLE IF NOT EXISTS pd_trab.TDirectMessage (
    MID INT PRIMARY KEY,
    UID INT,
    CONSTRAINT FK_DMMID FOREIGN KEY (MID)
        REFERENCES pd_trab.TMessage (MID)
        ON DELETE CASCADE,
    CONSTRAINT FK_DMUIDDest FOREIGN KEY (UID)
        REFERENCES pd_trab.TUser (UID)
        ON DELETE CASCADE
);

/*
    Encrypted Passwords: Teste123 -> HvwT6osnO0M/pyh4SFp3hA==
*/
/*
    TUser Dummy Data
    https://mockaroo.com/
*/
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (1, 'Junina Winter', 'jwinter0', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/178x220.bmp/dddddd/000000', 1878977919867921675);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (2, 'Desi Strongman', 'dstrongman1', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/153x205.jpg/cc0000/ffffff', 3960689096945798614);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (3, 'Giffard Bernlin', 'gbernlin2', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/212x200.bmp/ff4444/ffffff', 292640833351492321);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (4, 'Nissie Ledwidge', 'nledwidge3', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/115x234.jpg/cc0000/ffffff', 8848585378289092427);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (5, 'Lena Cauley', 'lcauley4', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/229x172.jpg/dddddd/000000', 5093676446930255959);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (6, 'Aluino Sawney', 'asawney5', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/180x141.bmp/ff4444/ffffff', 2807500374081027477);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (7, 'Lou Matchell', 'lmatchell6', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/185x236.png/ff4444/ffffff', 6092557808829433484);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (8, 'Betti Benditt', 'bbenditt7', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/137x107.png/5fa2dd/ffffff', 8525566939009626988);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (9, 'Harold Latchmore', 'hlatchmore8', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/226x249.bmp/ff4444/ffffff', 5976153636421780651);
insert into TUser (UID, UName, UUsername, UPassword, UPhoto, UDate) values (10, 'Philippine Cornewell', 'pcornewell9', 'HvwT6osnO0M/pyh4SFp3hA==', 'http://dummyimage.com/165x179.bmp/dddddd/000000', 2673836096340170341);

/*
    TChannel Dummy Data
    https://mockaroo.com/
*/
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (1, 5, 'Voyatouch', 'Morbi a ipsum.', 'HvwT6osnO0M/pyh4SFp3hA==', 8562225665738134961);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (2, 6, 'Cardify', 'In eleifend quam a odio. In hac habitasse platea dictumst.', 'HvwT6osnO0M/pyh4SFp3hA==', 5541541691243508266);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (3, 7, 'Y-Solowarm', 'Nunc nisl.', null, 2273348736886513890);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (4, 3, 'Latlux', 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit.', null, 8418513426683433257);
insert into TChannel (CID, CUID, CName, CDescription, CPassword, CDate) values (5, 9, 'It', 'Sed accumsan felis.', null, 139496771453579888);

/*
    TMessage Dummy Data
    https://mockaroo.com/
*/
insert into TMessage (MID, MUID, MText, MPath, MDate) values (1, 1, 'Proin risus.', null, 2025511693759916711);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (2, 6, 'Cras pellentesque volutpat dui.', null, 7741102602218305836);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (3, 7, 'Aenean fermentum. Donec ut mauris eget massa tempor convallis.', null, 4130346812406942851);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (4, 7, 'Sed ante. Vivamus tortor.', null, 915915826545212068);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (5, 9, 'Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam vel augue.', null, 6711199592202621444);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (6, 5, 'Morbi ut odio. Cras mi pede, malesuada in, imperdiet et, commodo vulputate, justo.', null, 8402561172415659218);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (7, 3, 'Cras in purus eu magna vulputate luctus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.', 'http://dummyimage.com/177x234.jpg/dddddd/000000', 5405929147091314984);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (8, 9, 'Pellentesque eget nunc. Donec quis orci eget orci vehicula condimentum.', null, 7763652757938095809);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (9, 1, 'Pellentesque at nulla.', null, 2821099968429935968);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (10, 4, 'Phasellus in felis. Donec semper sapien a libero.', null, 225643927700244577);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (11, 1, 'Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.', null, 7320690339778852942);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (12, 4, 'Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl.', null, 394511261898472803);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (13, 1, 'Mauris ullamcorper purus sit amet nulla.', null, 3091587628421460824);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (14, 6, 'Morbi non quam nec dui luctus rutrum.', null, 8167614226248894087);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (15, 9, 'Donec quis orci eget orci vehicula condimentum. Curabitur in libero ut massa volutpat convallis.', null, 1817129465168579658);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (16, 8, 'Mauris ullamcorper purus sit amet nulla.', null, 2592152169072392974);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (17, 8, 'In blandit ultrices enim. Lorem ipsum dolor sit amet, consectetuer adipiscing elit.', null, 7424537670069930106);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (18, 8, 'Aliquam augue quam, sollicitudin vitae, consectetuer eget, rutrum at, lorem.', null, 5476292344476662162);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (19, 7, 'Integer a nibh. In quis justo.', null, 7230580144022328680);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (20, 1, 'In blandit ultrices enim.', null, 8589547038963549307);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (21, 10, 'In hac habitasse platea dictumst. Etiam faucibus cursus urna.', null, 6230810061037070916);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (22, 4, 'Etiam vel augue. Vestibulum rutrum rutrum neque.', null, 2152979407332693580);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (23, 3, 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit.', null, 2999356433512033424);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (24, 6, 'Suspendisse accumsan tortor quis turpis. Sed ante.', null, 6554036132336733096);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (25, 6, 'Praesent lectus. Vestibulum quam sapien, varius ut, blandit non, interdum in, ante.', null, 1583101193583821176);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (26, 6, 'Vivamus in felis eu sapien cursus vestibulum.', null, 9151224953647819094);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (27, 7, 'Donec ut mauris eget massa tempor convallis. Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh.', null, 851441881515841962);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (28, 10, 'Integer tincidunt ante vel ipsum. Praesent blandit lacinia erat.', null, 2242061611421861771);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (29, 2, 'Vivamus tortor.', null, 1587462173235688762);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (30, 9, 'Nulla ut erat id mauris vulputate elementum. Nullam varius.', null, 7951519908050332027);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (31, 9, 'Nulla justo.', null, 32915231539254872);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (32, 1, 'Integer ac neque.', null, 6943925812711139642);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (33, 6, 'Nulla ac enim.', null, 7262448743588063368);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (34, 1, 'Maecenas ut massa quis augue luctus tincidunt. Nulla mollis molestie lorem.', null, 5972416452205279961);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (35, 3, 'Quisque arcu libero, rutrum ac, lobortis vel, dapibus at, diam.', null, 6451825060941955635);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (36, 2, 'Nam nulla. Integer pede justo, lacinia eget, tincidunt eget, tempus vel, pede.', null, 6628975749345211694);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (37, 9, 'Aenean sit amet justo. Morbi ut odio.', null, 2329483158462282452);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (38, 4, 'Mauris sit amet eros.', null, 5903627893851761508);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (39, 9, 'Nulla nisl.', null, 3491819167321403125);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (40, 7, 'In quis justo.', null, 1852452197903682228);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (41, 10, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Mauris viverra diam vitae quam. Suspendisse potenti.', null, 929038565242247194);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (42, 2, 'Maecenas leo odio, condimentum id, luctus nec, molestie sed, justo.', null, 688171531309604961);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (43, 1, 'Mauris lacinia sapien quis libero. Nullam sit amet turpis elementum ligula vehicula consequat.', null, 7882088232523419265);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (44, 9, 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec pharetra, magna vestibulum aliquet ultrices, erat tortor sollicitudin mi, sit amet lobortis sapien sapien non mi. Integer ac neque.', null, 3997655324914534386);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (45, 4, 'Nulla suscipit ligula in lacus. Curabitur at ipsum ac tellus semper interdum.', null, 6866217125021382549);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (46, 7, 'Vivamus tortor. Duis mattis egestas metus.', null, 186237250769504764);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (47, 4, 'Vestibulum quam sapien, varius ut, blandit non, interdum in, ante. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis faucibus accumsan odio.', null, 7617343277793536720);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (48, 6, 'Donec posuere metus vitae ipsum. Aliquam non mauris.', null, 5372310797412214043);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (49, 5, 'Cras pellentesque volutpat dui. Maecenas tristique, est et tempus semper, est quam pharetra magna, ac consequat metus sapien ut nunc.', null, 3561865560854684211);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (50, 6, 'Morbi porttitor lorem id ligula. Suspendisse ornare consequat lectus.', null, 8298366329375479080);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (51, 1, 'Maecenas leo odio, condimentum id, luctus nec, molestie sed, justo. Pellentesque viverra pede ac diam.', null, 4291223486063310868);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (52, 1, 'Maecenas ut massa quis augue luctus tincidunt.', null, 5619556528707462451);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (53, 3, 'In quis justo.', null, 3122628406863178377);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (54, 6, 'Sed vel enim sit amet nunc viverra dapibus. Nulla suscipit ligula in lacus.', null, 3948630982793470896);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (55, 8, 'Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vivamus vestibulum sagittis sapien.', null, 7249365220933799433);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (56, 7, 'Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla. Nunc purus.', null, 7370886061493237127);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (57, 7, 'Etiam vel augue.', null, 898150316387391502);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (58, 8, 'In hac habitasse platea dictumst. Etiam faucibus cursus urna.', null, 8717303233967780050);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (59, 4, 'Suspendisse potenti. Cras in purus eu magna vulputate luctus.', null, 6920670538976610269);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (60, 5, 'Mauris enim leo, rhoncus sed, vestibulum sit amet, cursus id, turpis.', null, 7849390593698108780);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (61, 3, 'Nulla mollis molestie lorem. Quisque ut erat.', null, 6816774419103955794);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (62, 6, 'Praesent blandit. Nam nulla.', null, 5382161457326222563);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (63, 8, 'Sed vel enim sit amet nunc viverra dapibus.', null, 2434922011767625731);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (64, 10, 'Aenean fermentum. Donec ut mauris eget massa tempor convallis.', 'http://dummyimage.com/160x114.bmp/cc0000/ffffff', 8780808331528311091);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (65, 9, 'Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla. Nunc purus.', null, 5936156541441044530);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (66, 4, 'Integer tincidunt ante vel ipsum. Praesent blandit lacinia erat.', null, 3885168152452683819);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (67, 5, 'Proin at turpis a pede posuere nonummy. Integer non velit.', null, 826638253712773247);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (68, 9, 'Nunc purus.', null, 4510476702416226111);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (69, 3, 'Donec diam neque, vestibulum eget, vulputate ut, ultrices vel, augue. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec pharetra, magna vestibulum aliquet ultrices, erat tortor sollicitudin mi, sit amet lobortis sapien sapien non mi.', 'http://dummyimage.com/161x102.png/cc0000/ffffff', 5470340869445026626);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (70, 6, 'Nam congue, risus semper porta volutpat, quam pede lobortis ligula, sit amet eleifend pede libero quis orci.', null, 195523574214256003);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (71, 1, 'Sed vel enim sit amet nunc viverra dapibus. Nulla suscipit ligula in lacus.', null, 646080395994285964);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (72, 1, 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue.', null, 7690509784651208846);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (73, 5, 'Donec dapibus. Duis at velit eu est congue elementum.', null, 4767736114598122082);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (74, 10, 'In quis justo.', null, 2976948065644742602);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (75, 6, 'In hac habitasse platea dictumst.', null, 2662464466996407164);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (76, 7, 'Vivamus vestibulum sagittis sapien.', null, 585942402796223668);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (77, 10, 'Maecenas tincidunt lacus at velit. Vivamus vel nulla eget eros elementum pellentesque.', null, 3210462452550571020);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (78, 2, 'In hac habitasse platea dictumst.', null, 8812568130287697388);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (79, 4, 'Morbi ut odio. Cras mi pede, malesuada in, imperdiet et, commodo vulputate, justo.', 'http://dummyimage.com/103x219.bmp/cc0000/ffffff', 2316992833244758512);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (80, 7, 'Nulla ut erat id mauris vulputate elementum.', null, 2221181621267977035);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (81, 2, 'Suspendisse potenti.', null, 7650539146672624399);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (82, 7, 'In hac habitasse platea dictumst. Etiam faucibus cursus urna.', null, 5074921607633023252);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (83, 1, 'Suspendisse potenti.', null, 8003535012011096962);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (84, 5, 'Aliquam augue quam, sollicitudin vitae, consectetuer eget, rutrum at, lorem.', null, 8149991339791272423);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (85, 6, 'Etiam pretium iaculis justo. In hac habitasse platea dictumst.', null, 4876579740190453251);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (86, 2, 'In hac habitasse platea dictumst.', null, 913816206230802638);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (87, 5, 'Mauris sit amet eros.', null, 8349284961165542219);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (88, 2, 'Proin risus.', null, 5869539296880585208);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (89, 3, 'Nam congue, risus semper porta volutpat, quam pede lobortis ligula, sit amet eleifend pede libero quis orci. Nullam molestie nibh in lectus.', null, 8981777578764289779);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (90, 10, 'In eleifend quam a odio.', null, 4421601156851197809);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (91, 1, 'Praesent id massa id nisl venenatis lacinia. Aenean sit amet justo.', null, 5420103935480390210);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (92, 1, 'Integer ac neque.', null, 6281732444498488465);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (93, 1, 'Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros.', null, 3395669783863870306);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (94, 1, 'Integer ac neque.', null, 4371365973832364078);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (95, 10, 'In est risus, auctor sed, tristique in, tempus sit amet, sem. Fusce consequat.', null, 7765386442072050213);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (96, 9, 'Etiam pretium iaculis justo.', null, 2668743915773258191);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (97, 6, 'Etiam faucibus cursus urna.', null, 4908984462796219822);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (98, 9, 'Proin eu mi. Nulla ac enim.', null, 1138159566635068337);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (99, 9, 'Nam congue, risus semper porta volutpat, quam pede lobortis ligula, sit amet eleifend pede libero quis orci.', null, 8107794433985531566);
insert into TMessage (MID, MUID, MText, MPath, MDate) values (100, 10, 'Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.', 'http://dummyimage.com/235x215.png/5fa2dd/ffffff', 8287640180787152753);

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
