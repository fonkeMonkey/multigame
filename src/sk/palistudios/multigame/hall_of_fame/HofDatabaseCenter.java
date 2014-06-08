package sk.palistudios.multigame.hall_of_fame;

// @author Pali
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import sk.palistudios.multigame.game.GameActivity;

public class HofDatabaseCenter extends SQLiteOpenHelper {

    private static HofDatabaseCenter hofDb = null;
    private SQLiteDatabase database;
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "HallOfFame";
    private static final String DATABASE_NAME = "HallOfFame";
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME
            + "(ID INTEGER primary key autoincrement, "
            + "NAME TEXT NOT NULL, "
            + "SCORE INTEGER NOT NULL " + ");";

    public HofDatabaseCenter(Context context) {
        super(context, "HallOfFame", null, DATABASE_VERSION);
    }

    public static void initDB(GameActivity game) {
        hofDb = new HofDatabaseCenter(game);
    }
//
//    public boolean isEmpty() {
//        if(database.g)
//    }

    class Row extends Object {

        public long ID;
        public String name;
        public int score;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void createRow(String name, int score) {
        if (!isInHallOfFame(score)) {
            return;
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);
        initialValues.put("score", score);
        database.insert(TABLE_NAME, null, initialValues);
    }

    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    @Override
    public void close() {
        database.close();
    }

    public void deleteRow(long rowId) {
        database.delete(TABLE_NAME, "ID=" + rowId, null);
    }

    public void deleteAll() {
        database.delete(TABLE_NAME, null, null);
    }

    public ArrayList<HofItem> fetchAllRows() {
        ArrayList<HofItem> rows = new ArrayList<HofItem>();
        try {

            Cursor c =
                    database.query(TABLE_NAME, new String[]{
                        "ID", "name", "score"}, null, null, null, null, "score");

            int numRows = c.getCount();
            c.moveToFirst();

            for (int i = 0; i < numRows; i++) {
                HofItem tableRow = new HofItem(c.getString(1), c.getInt(2));
                rows.add(tableRow);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
            Log.e("Exception on query", e.toString());
        }

        //reverse to be descending
        Collections.reverse(rows);

        return rows;
    }

    public boolean isInHallOfFame(int score) {
        ArrayList<HofItem> rows = fetchAllRows();

        //if its empty
        if (rows.size() < 10) {
            return true;
        }

        for (int i = 9; i >= 0; i--) {
            if (score >= (rows.get(i).score)) {
                return true;
            }
        }
        return false;
    }

    public void fillDbFirstTime() {
        open();
//        ArrayList<HofItem> scoreList = new ArrayList<HofItem>();

        createRow("Chuck N.", 10000);
        createRow("Steven S.", 9000);
        createRow("Bruce L.", 8000);
        createRow("Bruce W.", 7000);
        createRow("Arnold S.", 6000);
        createRow("Sylvester S.", 5000);
        createRow("Jackie Ch.", 4000);
        createRow("Vin D.", 3000);
        createRow("Denzel W.", 2000);
        createRow("Jason S.", 1000);

//        ContentValues initialValues = new ContentValues();
//        initialValues.put("name", name);
//        initialValues.put("score", score);
//        database.insert(TABLE_NAME, null, initialValues);
//        
//        writeScoreList(scoreList);

        close();
    }

    public void writeIntoHallOfFame(HofItem userInfo) {
        open();

        ArrayList<HofItem> scoreList = fetchAllRows();
        int listSize = 10;

        //if empty list
        if (scoreList.isEmpty()) {
            scoreList.add(userInfo);
        } else {
            //if list not full
            if (scoreList.size() < 10 && !scoreList.isEmpty()) {
                listSize = scoreList.size();
                boolean smallest = true;

                for (int i = 0; i < listSize; i++) {
                    if (userInfo.score >= (scoreList.get(i).score)) {
                        scoreList = putIntoPosition(scoreList, i, userInfo);
                        smallest = false;
                        break;
                    }
                }

                //if it is the smallest put to the end
                if (smallest) {
                    scoreList.add(userInfo);
                }
            } else {

                //if list is allready full
                for (int i = 0; i < listSize; i++) {
                    if (userInfo.score >= (scoreList.get(i).score)) {
                        scoreList = putIntoPosition(scoreList, i, userInfo);
                        break;
                    }
                }
            }


        }

        deleteAll();
        writeScoreList(scoreList);

        close();
    }

    private void writeScoreList(ArrayList<HofItem> rows) {
        for (HofItem item : rows) {
            createRow(item.name, item.score);
        }
    }

    //creates new arraylist with new HofItem at specified position
    private ArrayList<HofItem> putIntoPosition(ArrayList<HofItem> oldList, int position, HofItem userInfo) {
        ArrayList<HofItem> newList = new ArrayList<HofItem>();

        int newSize = 10;

        if (oldList.size() < 10) {
            newSize = oldList.size() + 1;
        }

        for (int i = 0; i < newSize; i++) {
            if (i < position) {
                newList.add(oldList.get(i));
            } else if (i == position) {
                newList.add(userInfo);
            } else {
                newList.add(oldList.get(i - 1));
            }
        }


        return newList;
    }

    public static HofDatabaseCenter getHofDb() {
        return hofDb;
    }
}
