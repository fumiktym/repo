@echo off
@rem -f force backup old versions
@rem -n do not backup old versions in backup dir.
java -classpath \\kuro-box\share\backup.jar ktymBackup.KtymBackup backup -l D:\Backup.log -f -n D:\ \\KURO-BOX\SHARE