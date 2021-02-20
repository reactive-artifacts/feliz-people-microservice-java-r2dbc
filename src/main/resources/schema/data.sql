

INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (1,false,'GROUP1',1);
INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (2,false,'GROUP2',2);
INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (3,false,'GROUP1',5);
INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (4,false,'GROUP2',6);
INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (5,false,'GROUP1',9);
INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (6,false,'GROUP1',11);
INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (7,false,'GROUP2',12);
INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (8,false,'GROUP2',13);
INSERT INTO "groupe" (id,deleted,name,job_id) VALUES (9,false,'GROUP1',14);


INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (1,false,'Zenica French','ARCHI',1);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (2,false,null,'DEV',1);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (5,false,'Zenica French','ARCHI',5);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (6,false,null,'DEV',5);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (7,false,'Google','ARCHI',7);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (8,false,'Facebook','DEV',7);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (9,false,'Google','ARCHI',8);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (10,false,'Facebook','DEV',8);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (11,false,'Zenica French','ARCHI',9);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (12,false,null,'DEV',9);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (13,false,null,'DEV',11);
INSERT INTO "job" (id,deleted,location,name,developer_id) VALUES (14,false,'Zenica French','ARCHI',11);


INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (1,null,'From Web 1234','Matsu motoOOOO','60fb5cd7-786b-4450-8938-d4ec0e212eb8');
INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (2,null,null,'joshwa','60fb5cd7-786b-4450-8938-d4ec0e212eb8');
INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (5,null,'Address 1 in paris','josh','60fb5cd7-786b-4450-8938-d4ec0e212eb8');
INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (6,null,null,'joshwa','60fb5cd7-786b-4450-8938-d4ec0e212eb8');
INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (7,null,'From Web1','Matsu motoOOOOXX','eef014ef-ca73-4759-960b-ec0091f67f04');
INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (8,null,'From Web 14','Josh long','eef014ef-ca73-4759-960b-ec0091f67f04');
INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (9,null,'Address 1 in paris','josh','60fb5cd7-786b-4450-8938-d4ec0e212eb8');
INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (10,null,null,'joshwa','60fb5cd7-786b-4450-8938-d4ec0e212eb8');
INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (11,null,'Address 1 in paris','josh','60fb5cd7-786b-4450-8938-d4ec0e212eb8');
INSERT INTO "person" (id,deleted,address,name,user_id) VALUES (12,null,null,'joshwa','60fb5cd7-786b-4450-8938-d4ec0e212eb8');


INSERT INTO "tag" (id,deleted,name) VALUES (1,null,'Science');
INSERT INTO "tag" (id,deleted,name) VALUES (2,null,'Computer');
INSERT INTO "tag" (id,deleted,name) VALUES (3,null,'Lithum');


INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (1,null,'Clojure dev','coding some class',true,2);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (2,null,'Kotlin dev','best doing with coroutine',true,2);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (3,null,'Java dev','coding some class',false,1);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (4,null,null,null,false,1);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (5,null,'Clojure dev','coding some class',true,6);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (6,null,'Kotlin dev','best doing with coroutine',true,6);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (7,null,'Java dev','coding some class',false,5);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (8,null,null,null,false,5);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (9,null,null,'todo dO it',true,7);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (10,null,null,'another todo',false,7);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (11,null,null,'todo exclude',false,8);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (12,null,'Clojure dev','coding some class',true,10);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (13,null,'Kotlin dev','best doing with coroutine',true,10);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (14,null,'Java dev','coding some class',false,9);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (15,null,null,null,false,9);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (16,null,'Clojure dev','coding some class',true,12);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (17,null,'Kotlin dev','best doing with coroutine',true,12);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (18,null,'Java dev','coding some class',false,11);
INSERT INTO "todo" (id,deleted,description,details,done,person_id) VALUES (19,null,null,null,false,11);


INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (1,null,3,3);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (2,null,1,3);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (3,null,2,4);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (4,null,3,7);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (5,null,1,7);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (6,null,2,8);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (7,null,1,9);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (8,null,3,9);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (9,null,2,10);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (10,null,1,11);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (11,null,1,14);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (12,null,3,14);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (13,null,2,15);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (14,null,1,18);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (15,null,3,18);
INSERT INTO "todo_tag" (id,deleted,tag_id,todo_id) VALUES (16,null,2,19);
