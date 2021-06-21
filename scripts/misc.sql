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

select p.user_id, u.username, sum(score), count(p.id)
from post_report pr
inner join post p
on pr.post_id = p.id
inner join user u
on p.user_id = u.id
where p.created_date > (now() - INTERVAL 7 DAY)
group by p.user_id, u.username
order by sum(score) desc;

select p.user_id, u.username, sum(moderation_score), sum(moderation_result), sum(moderation_score) * sum(moderation_result), count(p.id)
from post p
inner join user u
on p.user_id = u.id
where  moderation_score > 0 and moderation_result < 0 and p.created_date < '2011-08-01' and p.created_date > ('2011-08-01' - INTERVAL 30 DAY)
group by p.user_id, u.username
order by sum(moderation_score) * sum(moderation_result);

select p.user_id, u.username, sum(moderation_score), sum(moderation_result), sum(moderation_score) * sum(moderation_result), count(p.id)
from post p
inner join user u
on p.user_id = u.id
where  moderation_score > 0 and moderation_result < 0 and p.created_date > (now() - INTERVAL 30 DAY)
group by p.user_id, u.username
order by sum(moderation_score) * sum(moderation_result);

