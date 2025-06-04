DROP DATABASE petclinic;
DROP USER petclinic;
CREATE DATABASE petclinic;
CREATE USER 'petclinic' IDENTIFIED BY 'petclinic';
GRANT ALL ON petclinic.* TO 'petclinic'@'%';
USE petclinic;
