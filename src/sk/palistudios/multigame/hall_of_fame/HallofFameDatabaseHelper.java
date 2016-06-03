package sk.palistudios.multigame.hall_of_fame;

import java.util.ArrayList;
import java.util.Collections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// @author Pali
public class HallofFameDatabaseHelper extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 3;
  private static final String TABLE_NAME = "HallOfFame";
  private static final String DICTIONARY_TABLE_CREATE =
      "CREATE TABLE " + TABLE_NAME + "(ID INTEGER primary key autoincrement, " +
          "NAME TEXT NOT NULL, " + "SCORE INTEGER NOT NULL " + ");";
  private static final String DATABASE_NAME = "HallOfFame";

  private static HallofFameDatabaseHelper sHofDbHelper = null;
  private SQLiteDatabase mDatabase;

  public HallofFameDatabaseHelper(Context context) {
    super(context, "HallOfFame", null, DATABASE_VERSION);
  }

  public static HallofFameDatabaseHelper getInstance(Context context) {
    if (sHofDbHelper == null) {
      sHofDbHelper = new HallofFameDatabaseHelper(context);
    }
    return sHofDbHelper;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(DICTIONARY_TABLE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }

  private void openDb() throws SQLException {
    if (mDatabase == null || !mDatabase.isOpen()) {
      mDatabase = getWritableDatabase();
    }
  }

  private void closeDb() {
    mDatabase.close();
  }

  public synchronized void deleteAll() {
    openDb();
    mDatabase.delete(TABLE_NAME, null, null);
    closeDb();
  }

  public synchronized ArrayList<HofItem> fetchAllRows() {
    ArrayList<HofItem> rows = new ArrayList<HofItem>();
    try {

      openDb();
      Cursor c = mDatabase.query(TABLE_NAME, new String[]{"ID", "name", "score"}, null, null, null,
          null, "score");

      int numRows = c.getCount();
      c.moveToFirst();

      for (int i = 0; i < numRows; i++) {
        HofItem tableRow = new HofItem(c.getString(1), c.getInt(2));
        rows.add(tableRow);
        c.moveToNext();
      }
      c.close();
      closeDb();
    } catch (SQLException e) {
      Log.e("Exception on query", e.toString());
    }

    //reverse to be descending
    Collections.reverse(rows);

    return rows;
  }

  public synchronized boolean isInHallOfFame(int score) {
    openDb();
    ArrayList<HofItem> rows = fetchAllRows();
    closeDb();

    //if its empty
    if (rows.size() < 10) {
      return true;
    }

    for (int i = 9; i >= 0; i--) {
      if (score >= (rows.get(i).getScore())) {
        return true;
      }
    }
    return false;
  }

  public synchronized void fillDbFirstTime() {
    openDb();

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

    closeDb();
  }

  public synchronized int writeIntoHallOfFame(HofItem winnerInfo) {
    int position = -1;
    openDb();

    ArrayList<HofItem> scoreList = fetchAllRows();
    int listSize = 10;

    //if empty list
    if (scoreList.isEmpty()) {
      scoreList.add(winnerInfo);
    } else {
      //if list not full
      if (scoreList.size() < 10 && !scoreList.isEmpty()) {
        listSize = scoreList.size();
        boolean smallest = true;

        for (int i = 0; i < listSize; i++) {
          if (winnerInfo.getScore() >= (scoreList.get(i).getScore())) {
            scoreList = putIntoPosition(scoreList, i, winnerInfo);
            smallest = false;
            break;
          }
        }

        //if it is the smallest put to the end
        if (smallest) {
          scoreList.add(winnerInfo);
        }
      } else {

        //if list is allready full
        for (int i = 0; i < listSize; i++) {
          if (winnerInfo.getScore() >= (scoreList.get(i).getScore())) {
            scoreList = putIntoPosition(scoreList, i, winnerInfo);
            position = i;
            break;
          }
        }
      }

    }

    deleteAll();
    writeScoreList(scoreList);

    closeDb();

    return position;
  }

  private synchronized void writeScoreList(ArrayList<HofItem> rows) {
    for (HofItem item : rows) {
      createRow(item.getName(), item.getScore());
    }
  }

  private synchronized void createRow(String name, int score) {
    if (!isInHallOfFame(score)) {
      return;
    }
    ContentValues initialValues = new ContentValues();
    initialValues.put("name", name);
    initialValues.put("score", score);
    openDb();
    mDatabase.insert(TABLE_NAME, null, initialValues);
    closeDb();
  }

  //creates new arraylist with new HofItem at specified position
  private synchronized ArrayList<HofItem> putIntoPosition(ArrayList<HofItem> oldList, int position,
      HofItem userInfo) {
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

  @Deprecated
  private synchronized void deleteRow(long rowId) {
    mDatabase.delete(TABLE_NAME, "ID=" + rowId, null);
  }
}
