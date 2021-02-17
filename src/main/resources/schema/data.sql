

INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (1,false,'GROUP1',1);
INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (2,false,'GROUP2',2);


INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (1,false,'Zenica French','ARCHI',1);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (2,false,null,'DEV',1);


INSERT INTO "person" (id,deleted,address,name) VALUES (1,null,'Address 1 in paris','josh');
INSERT INTO "person" (id,deleted,address,name) VALUES (2,null,null,'joshwa');


INSERT INTO "tag" (id,deleted,name) VALUES (1,null,'Science');
INSERT INTO "tag" (id,deleted,name) VALUES (2,null,'Computer');
INSERT INTO "tag" (id,deleted,name) VALUES (3,null,'Lithum');


INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (1,null,'Clojure dev','coding some class',true,2);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (2,null,'Kotlin dev','best doing with coroutine',true,2);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (3,null,'Java dev','coding some class',false,1);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (4,null,null,null,false,1);


INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (1,null,3,3);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (2,null,1,3);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (3,null,2,4);
