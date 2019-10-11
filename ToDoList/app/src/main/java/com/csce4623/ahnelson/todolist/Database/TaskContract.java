package com.csce4623.ahnelson.todolist.Database;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.csce4623.ahnelson.todolist.Database";
    public static final int DB_VERSION = 2;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_CONTENT = "content";
        public static final String COL_TASK_DATE = "date";
        public static final String COL_TASK_TIME = "time";
    }
}