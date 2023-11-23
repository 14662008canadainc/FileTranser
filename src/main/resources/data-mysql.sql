REPLACE INTO local(created, updated, id, ip, port, session_id, chunk_id, in_use)
VALUES (now(),now(),1,'localhost',8081,null,null,false);

REPLACE INTO local(created, updated, id, ip, port, session_id, chunk_id, in_use)
VALUES (now(),now(),2,'localhost',8082,null,null,false);

REPLACE INTO remote(created, updated, id, ip, port, session_id, chunk_id, in_use)
VALUES (now(),now(),1,'localhost',8081,null,null,false);

REPLACE INTO remote(created, updated, id, ip, port, session_id, chunk_id, in_use)
VALUES (now(),now(),2,'localhost',8082,null,null,false);
