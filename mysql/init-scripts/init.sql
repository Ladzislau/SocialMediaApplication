create database social_media;
use social_media;

create table person(
                       id int primary key auto_increment,
                       username varchar(20) unique not null,
                       email varchar(256) not null unique,
                       password varchar(60) not null
);

create table publication(
                            id int primary key auto_increment,
                            title varchar(50),
                            content varchar(1000),
                            created_at timestamp default current_timestamp,
                            person_id int not null references person(id) on delete cascade
);

create table image(
                      id int primary key auto_increment,
                      path_to_image varchar(200) not null unique,
                      publication_id int not null references publication(id) on delete cascade
);


create table following(
                          id int primary key auto_increment,
                          person_id int not null references person(id) on delete cascade,
                          followee_id int not null references person(id) on delete cascade,
                          status varchar(15) not null,
                          constraint unique (person_id, followee_id),
                          constraint check(following.person_id != following.followee_id)
);

create table friendship(
                           id int primary key auto_increment,
                           person_id int not null references person(id) on delete cascade,
                           friend_id int not null references person(id) on delete cascade,
                           constraint unique (person_id, friend_id),
                           constraint check(friendship.person_id != friendship.friend_id)
);

create table message(
                        id int primary key auto_increment,
                        sender_id int not null references person(id) on delete set null ,
                        recipient_id int not null references person(id) on delete set null,
                        content varchar(1024) not null,
                        sent_at timestamp not null default current_timestamp
);
