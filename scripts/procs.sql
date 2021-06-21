-- /*
--  * This file is part of the NOTtheTalk distribution (https://github.com/jdudmesh/notthetalk).
--  * Copyright (c) 2011-2021 John Dudmesh.
--  *
--  * This program is free software: you can redistribute it and/or modify
--  * it under the terms of the GNU General Public License as published by
--  * the Free Software Foundation, version 3.
--  *
--  * This program is distributed in the hope that it will be useful, but
--  * WITHOUT ANY WARRANTY; without even the implied warranty of
--  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
--  * General Public License for more details.
--  *
--  * You should have received a copy of the GNU General Public License
--  * along with this program. If not, see <http://www.gnu.org/licenses/>.
--  */


#update user_options set watch = false;

DROP PROCEDURE IF EXISTS FixPostNumbers;

delimiter //

create procedure FixPostNumbers()
begin

declare done INT DEFAULT 0;
declare did, ldid int;
declare pid int default 0;
declare cur_post_num int default 0;

#declare c1 cursor for select id, discussion_id from post where discussion_id = 7607 order by discussion_id, id;
declare c1 cursor for select id, discussion_id from post order by discussion_id, id;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

open c1;

set ldid = 0;

read_loop: LOOP
    fetch c1 into pid, did;

    #select ldid, did, pid;

    IF done THEN
      LEAVE read_loop;
    END IF;

    if did <> ldid then
        update discussion set post_count = cur_post_num - 1 where id = ldid;
        set cur_post_num = 1;
    end if;

    update post set post_num = cur_post_num where id = pid;

    set cur_post_num = cur_post_num + 1;
    set ldid = did;


END LOOP;

close c1;

update discussion set post_count = cur_post_num - 1 where id = ldid;

end//

delimiter ;


#call FixPostNumbers();

DROP PROCEDURE IF EXISTS GetFrontPage;

delimiter //

create procedure GetFrontPage(uid bigint)
begin

drop temporary table if exists recent_discussions;

create temporary table recent_discussions (
discussion_id bigint not null primary key,
last_post datetime,
title varchar(255),
folder_id bigint,
post_count int,
last_post_id bigint);

insert into recent_discussions (discussion_id, last_post, title, folder_id, post_count, last_post_id)
select d.id, d.last_post, d.title, d.folder_id, d.post_count, d.last_post_id
from discussion  d
inner join folder f
on d.folder_id = f.id
where f.type = 0
and d.status = 0
order by last_post desc limit 30;

select d.discussion_id,
d.last_post,
d.post_count,
d.title as discussion_name,
f.id as folder_id,
f.description as folder_name,
f.folder_key,
coalesce(u.last_post_count, 0) as last_post_count,
d.post_count - coalesce(u.last_post_count, 0) as new_post_count
from recent_discussions d
inner join folder f
on d.folder_id = f.id
left outer join (select * from user_discussion where user_id = uid) u
on d.discussion_id = u.discussion_id
order by d.last_post desc;

drop table recent_discussions;

end//

delimiter ;

DROP PROCEDURE IF EXISTS GetFrontPageAnon;

delimiter //

create procedure GetFrontPageAnon()
begin

drop temporary table if exists recent_discussions;

create temporary table recent_discussions (
discussion_id bigint not null primary key,
last_post datetime,
title varchar(255),
folder_id bigint,
post_count int,
last_post_id bigint);

insert into recent_discussions (discussion_id, last_post, title, folder_id, post_count, last_post_id)
select d.id, d.last_post, d.title, d.folder_id, d.post_count, d.last_post_id
from discussion  d
inner join folder f
on d.folder_id = f.id
where f.type = 0
and d.status = 0
order by last_post desc limit 30;

select d.discussion_id,
d.last_post,
d.post_count,
d.title as discussion_name,
f.id as folder_id,
f.description as folder_name,
f.folder_key,
0 as last_post_count,
d.post_count as new_post_count
from recent_discussions d
inner join folder f
on d.folder_id = f.id
order by d.last_post desc;

drop table recent_discussions;

end//

delimiter ;

drop procedure if exists GetFrontPageAdmin;

delimiter //

create procedure GetFrontPageAdmin(uid bigint)
begin

drop temporary table if exists recent_discussions;

create temporary table recent_discussions (
discussion_id bigint not null primary key,
last_post datetime,
title varchar(255),
folder_id bigint,
post_count int,
last_post_id bigint);

insert into recent_discussions (discussion_id, last_post, title, folder_id, post_count, last_post_id)
select d.id, d.last_post, d.title, d.folder_id, d.post_count, d.last_post_id
from discussion  d
inner join folder f
on d.folder_id = f.id
where f.type in (0, 3)
and d.status = 0
order by last_post desc limit 30;

select d.discussion_id,
d.last_post,
d.post_count,
d.title as discussion_name,
f.id as folder_id,
f.description as folder_name,
f.folder_key,
coalesce(u.last_post_count, 0) as last_post_count,
d.post_count - coalesce(u.last_post_count, 0) as new_post_count
from recent_discussions d
inner join folder f
on d.folder_id = f.id
left outer join (select * from user_discussion where user_id = uid) u
on d.discussion_id = u.discussion_id
order by d.last_post desc;

drop table recent_discussions;

end//

delimiter ;

drop procedure if exists GetDiscussionsByFolder;

delimiter //

create procedure GetDiscussionsByFolder(uid bigint, fid bigint, page_start int, page_size int)
begin

set @fid = fid;
set @page_start = page_start;
set @page_size = page_size;

drop temporary table if exists recent_discussions;

create temporary table recent_discussions (
discussion_id bigint not null primary key,
last_post datetime,
title varchar(255),
folder_id bigint,
post_count int,
last_post_id bigint);

PREPARE STMT FROM "
insert into recent_discussions (discussion_id, last_post, title, folder_id, post_count, last_post_id)
select d.id, d.last_post, d.title, d.folder_id, d.post_count, d.last_post_id
from discussion  d
inner join folder f
on d.folder_id = f.id
where f.id = ?
and d.status = 0
order by last_post desc
limit ?, ?;";

EXECUTE STMT USING @fid, @page_start, @page_size;

select d.discussion_id,
d.last_post,
d.post_count,
d.title as discussion_name,
f.id as folder_id,
f.description as folder_name,
f.folder_key,
coalesce(u.last_post_count, 0) as last_post_count,
d.post_count - coalesce(u.last_post_count, 0) as new_post_count
from recent_discussions d
inner join folder f
on d.folder_id = f.id
left outer join (select * from user_discussion where user_id = uid) u
on d.discussion_id = u.discussion_id
order by d.last_post desc;

drop table recent_discussions;

end//

delimiter ;

drop procedure if exists GetDiscussionsByFolderAnon;

delimiter //

create procedure GetDiscussionsByFolderAnon(fid bigint, page_start int, page_size int)
begin

set @fid = fid;
set @page_start = page_start;
set @page_size = page_size;

drop temporary table if exists recent_discussions;

create temporary table recent_discussions (
discussion_id bigint not null primary key,
last_post datetime,
title varchar(255),
folder_id bigint,
post_count int,
last_post_id bigint);

PREPARE STMT FROM "
insert into recent_discussions (discussion_id, last_post, title, folder_id, post_count, last_post_id)
select d.id, d.last_post, d.title, d.folder_id, d.post_count, d.last_post_id
from discussion  d
inner join folder f
on d.folder_id = f.id
where f.id = ?
and d.status = 0
order by last_post desc
limit ?, ?;";

EXECUTE STMT USING @fid, @page_start, @page_size;

select d.discussion_id,
d.last_post,
d.post_count,
d.title as discussion_name,
f.id as folder_id,
f.description as folder_name,
f.folder_key,
0 as last_post_count,
d.post_count as new_post_count
from recent_discussions d
inner join folder f
on d.folder_id = f.id
order by d.last_post desc;

drop table recent_discussions;

end//

delimiter ;

drop procedure if exists GetSubscriptions;

delimiter //

create procedure GetSubscriptions(uid bigint, sortorder int)
begin

drop temporary table if exists subscribed_discussions;

CREATE TEMPORARY TABLE `subscribed_discussions` (
  `discussionId` bigint(20) NOT NULL,
  `discussionName` varchar(255) NOT NULL,
  `folderId` bigint(20) NOT NULL,
  `folderKey` varchar(255) NOT NULL,
  `folderName` varchar(255) NOT NULL,
  `lastPost` datetime NOT NULL,
  `lastPostId` bigint(20) DEFAULT NULL,
  `postCount` int(11) NOT NULL
);

insert into subscribed_discussions
select fp.discussion_id,
fp.discussion_name,
fp.folder_id,
fp.folder_key,
fp.folder_name,
fp.last_post,
fp.last_post_id,
fp.post_count
from subscription s
left outer join user_discussion u
on s.user_id = u.user_id
and s.discussion_id = u.discussion_id
inner join  front_page_entry fp
on s.discussion_id = fp.discussion_id
where s.user_id = uid;

if sortorder = 0 then select * from subscribed_discussions order by folderName; #SORT_SUBS_FOLDER
elseif sortorder = 1 then select * from subscribed_discussions order by discussionName; #SORT_SUBS_DISCUSSION
elseif sortorder = 2 then select * from subscribed_discussions order by folderName; #SORT_SUBS_DATE
elseif sortorder = 3 then select * from subscribed_discussions order by lastPost desc; #SORT_SUBS_RECENT
else select * from updated_discussions order by last_post desc;
end if;

drop temporary table if exists subscribed_discussions;

end//

delimiter ;


DROP PROCEDURE IF EXISTS GetUpdatedDiscussions;

delimiter //

create procedure GetUpdatedDiscussions(uid bigint, sortorder int)
begin

drop temporary table if exists updated_discussions;

CREATE TEMPORARY TABLE `updated_discussions` (
  `id` bigint(20) NOT NULL PRIMARY KEY,
  `version` bigint(20) NOT NULL,
  `discussion_id` bigint(20) NOT NULL,
  `discussion_name` varchar(255) NOT NULL,
  `folder_id` bigint(20) NOT NULL,
  `folder_key` varchar(255) NOT NULL,
  `folder_name` varchar(255) NOT NULL,
  `last_post` datetime NOT NULL,
  `last_post_id` bigint(20) DEFAULT NULL,
  `post_count` int(11) NOT NULL,
   admin_only bit not null
);

insert into updated_discussions(id,
version,
discussion_id,
discussion_name,
folder_id,
folder_key,
folder_name,
last_post,
last_post_id,
post_count,
admin_only)
select fp.id,
fp.version,
fp.discussion_id,
fp.discussion_name,
fp.folder_id,
fp.folder_key,
fp.folder_name,
fp.last_post,
fp.last_post_id,
fp.post_count,
fp.admin_only
from subscription s
left outer join user_discussion u
on s.user_id = u.user_id
and s.discussion_id = u.discussion_id
inner join  front_page_entry fp
on s.discussion_id = fp.discussion_id
where s.user_id = uid
and u.last_post_count < fp.post_count
union distinct
select fp.id,
fp.version,
fp.discussion_id,
fp.discussion_name,
fp.folder_id,
fp.folder_key,
fp.folder_name,
fp.last_post,
fp.last_post_id,
fp.post_count,
fp.admin_only
from folder_subscription s
inner join  front_page_entry fp
on s.folder_id = fp.folder_id
left outer join user_discussion u
on s.user_id = u.user_id
and fp.discussion_id = u.discussion_id
where s.user_id = uid
and fp.post_count > coalesce(u.last_post_count, 0)
and fp.last_post_id > coalesce(u.last_post_id, 0)
and fp.last_post > s.created_date
and fp.last_post > date_add(now(), INTERVAL -90 DAY)
and not fp.discussion_id in (select discussion_id from folder_subscription_exception e where e.subscription_id = s.id)
order by 1 desc;

if sortorder = 0 then select * from updated_discussions order by folder_name; #SORT_SUBS_FOLDER
elseif sortorder = 1 then select * from updated_discussions order by discussion_name; #SORT_SUBS_DISCUSSION
elseif sortorder = 2 then select * from updated_discussions order by folder_name; #SORT_SUBS_DATE
elseif sortorder = 3 then select * from updated_discussions order by last_post desc; #SORT_SUBS_RECENT
else select * from updated_discussions order by last_post desc;
end if;

drop temporary table if exists updated_discussions;

end//

delimiter ;


DROP PROCEDURE IF EXISTS GetSubscribedUsers;

delimiter //

create procedure GetSubscribedUsers(did bigint)
begin

select user_id from subscription where discussion_id = did
union distinct
select user_id
from folder_subscription fs
left outer join (select * from folder_subscription_exception where discussion_id = did) fse
on fs.id = fse.subscription_id
where fs.folder_id in (select folder_id from discussion where id = did)
and fse.id is null;

end//

delimiter ;

DROP PROCEDURE IF EXISTS GetDeletedDiscussions;

delimiter //

create procedure GetDeletedDiscussions(uid bigint)
begin

drop temporary table if exists recent_discussions;

create temporary table recent_discussions (
discussion_id bigint not null primary key,
last_post datetime,
title varchar(255),
folder_id bigint,
post_count int,
last_post_id bigint);

insert into recent_discussions (discussion_id, last_post, title, folder_id, post_count, last_post_id)
select d.id, d.last_post, d.title, d.folder_id, d.post_count, d.last_post_id
from discussion  d
inner join folder f
on d.folder_id = f.id
where f.type in (0, 3)
and d.status = 2
order by last_post desc limit 50;

select d.discussion_id,
d.last_post,
d.post_count,
d.title as discussion_name,
f.id as folder_id,
f.description as folder_name,
f.folder_key,
coalesce(u.last_post_count, 0) as last_post_count,
d.post_count - coalesce(u.last_post_count, 0) as new_post_count
from recent_discussions d
inner join folder f
on d.folder_id = f.id
left outer join (select * from user_discussion where user_id = uid) u
on d.discussion_id = u.discussion_id
order by d.last_post_id desc;

drop table recent_discussions;

end//

delimiter ;





DROP PROCEDURE IF EXISTS GetUserPostCount;

delimiter //

create procedure GetUserPostCount(uid bigint, did bigint)
begin

	select last_post_count from user_discussion where user_id = uid and discussion_id = did;

end//

delimiter ;

drop procedure if exists GetPostsForDiscussion;

delimiter //

create procedure GetPostsForDiscussion(did bigint, page_start int, page_size int)
begin

select p.id, p.user_id userId,
case
    when (p.status = 1 or p.status = 4) then true
    when p.moderation_score > 0 then true
    else false
end showModerationReport,
case
    when p.post_num = d.post_count then true
    else false
end isLastPost,
p.post_num postNum,
p.created_date createdDate,
p.status
from post p
inner join discussion d
on p.discussion_id = d.id
where p.discussion_id = did
and p.post_num > page_start
and p.post_num <= page_start + page_size
order by p.post_num;

end//

delimiter ;



create index idx_front_page_entry_last_post on front_page_entry(last_post);
create index idx_front_page_entry_folder_id on front_page_entry(folder_id);
create index idx_front_page_entry_discussion_id on front_page_entry(discussion_id);
create index idx_front_page_entry_admin_only on front_page_entry(admin_only);

delete from front_page_entry;

insert into front_page_entry (version, discussion_id, discussion_name, folder_id, folder_key, folder_name, last_post, last_post_id, post_count, admin_only)
select 0, d.id, d.title, d.folder_id, f.folder_key, f.description, d.last_post, d.last_post_id , d.post_count, case when f.type  = 3 then 1 else 0 end
from discussion  d
inner join folder f
on d.folder_id = f.id
where f.type  in (0, 3)
and d.status = 0
order by last_post;

#insert moderation_queue (version, created_date, post_id)
#select 0, now(), id
#from post
#where (status = 0 and moderation_result = 0 and moderation_score > 0)
#and id not in (select post_id from moderation_queue)
#or (status = 1 or status = 4);


drop procedure if exists GetPostsForModeration;

delimiter //

create procedure GetPostsForModeration()
begin

select p.id, p.user_id userId,
case
    when p.status != 0 then true
    when p.moderation_score > 0 then true
    else false
end showModerationReport,
case
    when p.id in (select max(id) from post where discussion_id = p.discussion_id) then true
    else false
end isLastPost,
p.post_num postNum,
p.created_date createdDate,
p.status
from post p
inner join moderation_queue q
on p.id = q.post_id
where p.status <> 256
order by p.id;

end//

delimiter ;


drop procedure if exists UpdateFolderActivity;

delimiter //

create procedure UpdateFolderActivity()
begin

update folder set activity = 0;

drop table if exists folder_counts;

create temporary table folder_counts(folder_id int not null primary key, count int);

insert folder_counts
select folder_id, count(*) as count
from front_page_entry
where last_post > (now() - INTERVAL 120 MINUTE)
group by folder_id;

update folder f, folder_counts c
set f.activity = c.count
where f.id = c.folder_id;

drop table folder_counts;

end//

delimiter ;

drop procedure if exists GetRecentlyModeratedPosts;

delimiter //

create procedure GetRecentlyModeratedPosts()
begin

select p.id, p.user_id userId,
true showModerationReport,
false isLastPost,
p.post_num postNum,
p.created_date createdDate,
p.status
from post p
where moderation_result != 0
and moderation_Score != 0
and p.created_date > (now() - INTERVAL 30 DAY)
order by p.id desc;

end//

delimiter ;

DROP PROCEDURE IF EXISTS GetPostSearchDetail;

delimiter //

create procedure GetPostSearchDetail()
begin


select p.id, p.created_date, p.text, d.title, f.description, u.username
from post p
inner join user u
on p.user_id = u.id
inner join discussion d
on p.discussion_id = d.id
inner join folder f
on d.folder_id = f.id
where p.status = 0
and d.status = 0
and f.type = 0;

end//

delimiter ;


DELIMITER ;;
CREATE PROCEDURE `GetUpdatedDiscussions`(uid bigint, sortorder int)
begin

drop temporary table if exists updated_discussions;

CREATE TEMPORARY TABLE `updated_discussions` (
  `id` bigint(20) NOT NULL PRIMARY KEY,
  `version` bigint(20) NOT NULL,
  `discussion_id` bigint(20) NOT NULL,
  `discussion_name` varchar(255) NOT NULL,
  `folder_id` bigint(20) NOT NULL,
  `folder_key` varchar(255) NOT NULL,
  `folder_name` varchar(255) NOT NULL,
  `last_post` datetime NOT NULL,
  `last_post_id` bigint(20) DEFAULT NULL,
  `post_count` int(11) NOT NULL,
   admin_only bit not null
);

insert into updated_discussions(id,
version,
discussion_id,
discussion_name,
folder_id,
folder_key,
folder_name,
last_post,
last_post_id,
post_count,
admin_only)
select fp.id,
fp.version,
fp.discussion_id,
fp.discussion_name,
fp.folder_id,
fp.folder_key,
fp.folder_name,
fp.last_post,
fp.last_post_id,
fp.post_count,
fp.admin_only
from subscription s
left outer join user_discussion u
on s.user_id = u.user_id
and s.discussion_id = u.discussion_id
inner join  front_page_entry fp
on s.discussion_id = fp.discussion_id
where s.user_id = uid
and u.last_post_count < fp.post_count
union distinct
select fp.id,
fp.version,
fp.discussion_id,
fp.discussion_name,
fp.folder_id,
fp.folder_key,
fp.folder_name,
fp.last_post,
fp.last_post_id,
fp.post_count,
fp.admin_only
from folder_subscription s
inner join  front_page_entry fp
on s.folder_id = fp.folder_id
left outer join user_discussion u
on s.user_id = u.user_id
and fp.discussion_id = u.discussion_id
where s.user_id = uid
and fp.post_count > coalesce(u.last_post_count, 0)
and fp.last_post_id > coalesce(u.last_post_id, 0)
and fp.last_post > s.created_date
and not fp.discussion_id in (select discussion_id from folder_subscription_exception e where e.subscription_id = s.id)
order by 1 desc;

if sortorder = 0 then select * from updated_discussions order by folder_name;
elseif sortorder = 1 then select * from updated_discussions order by discussion_name;
elseif sortorder = 2 then select * from updated_discussions order by folder_name;
elseif sortorder = 3 then select * from updated_discussions order by last_post desc;
else select * from updated_discussions order by last_post desc;
end if;

drop temporary table if exists updated_discussions;

end ;;
DELIMITER ;

DELIMITER ;;
CREATE PROCEDURE `GetPostsForDiscussion`(did bigint, page_start int, page_size int)
begin

set @did = did;
set @page_start = page_start;
set @page_size = page_size;

PREPARE STMT FROM "
select p.id, p.user_id userId,
case
    when (p.status = 1 or p.status = 4) then true
    when p.moderation_score > 0 then true
    else false
end showModerationReport,
case
    when p.id in (select max(id) from post where discussion_id = p.discussion_id) then true
    else false
end isLastPost,
p.post_num postNum,
p.created_date createdDate,
p.status
from post p
where p.discussion_id = ?
order by p.id
limit ?, ?;";

EXECUTE STMT USING @did, @page_start, @page_size;

end ;;
DELIMITER ;


DROP PROCEDURE IF EXISTS GetUserSockpuppets;

delimiter //

create procedure GetUserSockpuppets(IN user_id int)

begin
select l1.ip_address, l1.geo_location, u1.username as `username`, u2.username as `sockpuppet`, l2.last_login
from user_login_location l1
inner join user_login_location l2
on l1.ip_address = l2.ip_address
inner join user u1
on l1.user_id = u1.id
inner join user u2
on l2.user_id = u2.id
where l1.user_id != l2.user_id
and l1.user_id = user_id
order by u1.username, u2.username;

end //

DELIMITER ;

DROP PROCEDURE IF EXISTS update_user_login_location;

delimiter //

create procedure update_user_login_location()

begin
    insert user_login_location(version, user_id, ip_address, last_login)
    select 0, h.user_id, h.ip_address, max(logged_in_date)
    from login_history h
    left join user_login_location l
    on h.user_id = l.user_id
    and h.ip_address = l.ip_address
    where l.id is null
    group by h.user_id, h.ip_address;

end //

DELIMITER ;

DROP PROCEDURE IF EXISTS GetTop10Tags;

delimiter //

create procedure GetTop10Tags()

begin
	select id, tag, sum(weight) as weight
	from discussion_tag
	group by id, tag
	order by weight desc
	limit 10;
end //

DELIMITER ;

DROP PROCEDURE IF EXISTS GetDiscussionsForTag;

delimiter //

create procedure GetDiscussionsForTag(IN tag text)

begin
	select distinct fp.*
	from front_page_entry fp
	inner join discussion_tag t
	on fp.discussion_id = t.discussion_id
	where t.tag = tag
	and fp.admin_only = 0
	order by t.weight, t.created_date, fp.last_post desc
	limit 25;
end //

DELIMITER ;

DROP PROCEDURE IF EXISTS AgeDiscussionTags;

delimiter //

create procedure AgeDiscussionTags()

begin
	update discussion_tag set weight = weight * 0.95;
	delete from discussion_tag where weight < 0.1;
end //

DELIMITER ;

