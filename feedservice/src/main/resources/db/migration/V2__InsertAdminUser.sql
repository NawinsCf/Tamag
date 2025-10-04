-- V2__InsertAdminUser.sql
-- Inserts an admin user (pseudo=admin) if it does not already exist.
-- Password: 123456 (stored as bcrypt hash)

/*
 Note: the mdp column must contain the bcrypt hash of the password.
 If you want to generate a different hash locally, you can run a small script
 (Node example):
   node -e "const bcrypt=require('bcryptjs'); bcrypt.hash('123456',10,(e,h)=>console.log(h))"
 and paste the resulting hash below replacing the value in the INSERT.
*/

INSERT INTO users (pseudo, mdp, mail, est_admin)
SELECT 'admin', '$2b$10$8A0pjZGtV4xNZ69gvllSqeMmmxH6SIhxp2W9/7S//a022HZ2G.7u2', '', TRUE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE pseudo = 'admin');
