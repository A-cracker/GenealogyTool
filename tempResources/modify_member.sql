ALTER TABLE `genealogy`.`member`
    ADD COLUMN `creator_id` BIGINT(20) NULL AFTER `lunar_deathdate`,
ADD COLUMN `create_time` DATETIME NULL AFTER `creator_id`;
