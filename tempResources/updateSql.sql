ALTER TABLE `genealogy`.`member`
    ADD COLUMN `deathDate` DATE NULL DEFAULT NULL ;


ALTER TABLE `genealogy`.`member`
    ADD COLUMN `open_identity` TINYINT(4) NULL COMMENT '公开身份证: 0:公开,1:后四位,2:不公开' AFTER `deathDate`,
ADD COLUMN `open_phone` TINYINT(4) NULL COMMENT '公开手机: 0:公开,1:后四位,2:不公开' AFTER `open_identity`;


ALTER TABLE `genealogy`.`member`
    CHANGE COLUMN `open_identity` `open_identity` TINYINT(4) NULL DEFAULT 1 COMMENT '公开身份证: 0:公开,1:后四位,2:不公开' ,
    CHANGE COLUMN `open_phone` `open_phone` TINYINT(4) NULL DEFAULT 1 COMMENT '公开手机: 0:公开,1:后四位,2:不公开' ;


ALTER TABLE `genealogy`.`member`
    CHANGE COLUMN `img` `img` VARCHAR(40) NULL DEFAULT NULL COMMENT '照片, 大文件ID' ,
    ADD COLUMN `album_id` BIGINT(20) NULL COMMENT '影像库相册ID' AFTER `open_phone`;


CREATE TABLE `document` (
                            `id` bigint(20) NOT NULL,
                            `name` varchar(255) DEFAULT NULL,
                            `create_time` datetime DEFAULT NULL,
                            `uploader_id` bigint(20) DEFAULT NULL,
                            `member_id` bigint(20) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8




ALTER TABLE `genealogy`.`pedigree`
    ADD COLUMN `create_time`  DATETIME NULL DEFAULT NULL ;

ALTER TABLE `genealogy`.`member`
    CHANGE COLUMN `identityId` `identityId` VARCHAR(100) NULL COMMENT '身份证号' ;


ALTER TABLE `genealogy`.`pedigree`
    ADD COLUMN `creator_id` BIGINT(20) NULL AFTER `create_time`;




ALTER TABLE `genealogy`.`family`
DROP FOREIGN KEY `family_memberId_foreign`;

ALTER TABLE `genealogy`.`relation`  DROP FOREIGN KEY `relation_u1_foreign`, DROP FOREIGN KEY `relation_u2_foreign`;

ALTER TABLE `genealogy`.`member`
    CHANGE COLUMN `id` `id` BIGINT(20) UNSIGNED NOT NULL ;

ALTER TABLE `genealogy`.`family`
    CHANGE COLUMN `memberId` `memberId` BIGINT(20) UNSIGNED NOT NULL ;




ALTER TABLE `genealogy`.`family`
DROP FOREIGN KEY `family_treeId_foreign`;
ALTER TABLE `genealogy`.`family`
    CHANGE COLUMN `treeId` `treeId` BIGINT(20) UNSIGNED NOT NULL ,
DROP INDEX `family_treeId_foreign` ;


ALTER TABLE `genealogy`.`relation`
DROP FOREIGN KEY `relation_FK`;
ALTER TABLE `genealogy`.`relation`
    CHANGE COLUMN `treeId` `treeId` BIGINT(20) UNSIGNED NULL DEFAULT NULL ,
DROP INDEX `relation_FK` ;


ALTER TABLE `genealogy`.`pedigree`
    CHANGE COLUMN `id` `id` BIGINT(20) UNSIGNED NOT NULL COMMENT '族谱id' ;

ALTER TABLE `genealogy`.`member`
    CHANGE COLUMN `description` `description` VARCHAR(2000) NULL DEFAULT NULL COMMENT '介绍' ;


ALTER TABLE `genealogy`.`member`
    ADD COLUMN `birthdate_type` TINYINT(4) NULL  DEFAULT 0  COMMENT '生日日期类型: 0: 年月日, 1:朝代' AFTER `album_id`,
    ADD COLUMN `deathdate_type` TINYINT(4) NULL DEFAULT 0 COMMENT '生日日期类型: 0: 年月日, 1:朝代' AFTER `birthdate_type`,
    ADD COLUMN `lunar_birthdate` varchar(45) NULL COMMENT '农历生日',
    ADD COLUMN `lunar_deathdate` varchar(45) NULL COMMENT '农历忌日';
