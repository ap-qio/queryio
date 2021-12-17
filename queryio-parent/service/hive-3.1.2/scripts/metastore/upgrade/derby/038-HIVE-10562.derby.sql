-- Step 1: Add the column for format
ALTER TABLE "APP"."NOTIFICATION_LOG" ADD "MESSAGE_FORMAT" varchar(16);

-- Step 2 : Change the type of the MESSAGE field from long varchar to clob
ALTER TABLE "APP"."NOTIFICATION_LOG" ADD COLUMN "MESSAGE_CLOB" CLOB;
UPDATE "APP"."NOTIFICATION_LOG" SET MESSAGE_CLOB=CAST(MESSAGE AS CLOB);
ALTER TABLE "APP"."NOTIFICATION_LOG" DROP COLUMN MESSAGE;
RENAME COLUMN "APP"."NOTIFICATION_LOG"."MESSAGE_CLOB" TO "MESSAGE";

-- ALTER TABLE "APP"."NOTIFICATION_LOG" ALTER COLUMN "MESSAGE" SET DATA TYPE CLOB;

