alter table groupe drop foreign key if exists FKp59mphxh8am13rry5wrxvgn1c;
alter table job drop foreign key if exists FK3ty91eq8oeii1l7u0s71d9i47;
alter table todo drop foreign key if exists FKt4goeu4k0iv12fofnnc9cnw7o;
alter table todo_tag drop foreign key if exists FK3w1xrmwsoykr0mqgah1yhfjqt;
alter table todo_tag drop foreign key if exists FKjb2k1x5n6dkhyjpht94d3ynnu;
drop table if exists groupe;
drop table if exists job;
drop table if exists person;
drop table if exists tag;
drop table if exists todo;
drop table if exists todo_tag;
