package ivansticky.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by _Ivan_ on 2017/5/24.
 */
public class Myspl extends SQLiteOpenHelper {
    public Myspl(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "info.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 适合做表结构的初始化
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //当数据库的版本发生改变时调用,适合做表结构的修改
    }
}
