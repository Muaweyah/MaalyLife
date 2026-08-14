package com.maaly.life.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TaskDao_Impl implements TaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Task> __insertionAdapterOfTask;

  private final EntityDeletionOrUpdateAdapter<Task> __deletionAdapterOfTask;

  private final EntityDeletionOrUpdateAdapter<Task> __updateAdapterOfTask;

  public TaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTask = new EntityInsertionAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `tasks` (`id`,`title`,`category`,`priority`,`date`,`isCompleted`,`reminderTime`,`repeatRule`,`customSound`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Task entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getCategory());
        statement.bindLong(4, entity.getPriority());
        statement.bindString(5, entity.getDate());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getReminderTime() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getReminderTime());
        }
        if (entity.getRepeatRule() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getRepeatRule());
        }
        if (entity.getCustomSound() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCustomSound());
        }
      }
    };
    this.__deletionAdapterOfTask = new EntityDeletionOrUpdateAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tasks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Task entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTask = new EntityDeletionOrUpdateAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tasks` SET `id` = ?,`title` = ?,`category` = ?,`priority` = ?,`date` = ?,`isCompleted` = ?,`reminderTime` = ?,`repeatRule` = ?,`customSound` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Task entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getCategory());
        statement.bindLong(4, entity.getPriority());
        statement.bindString(5, entity.getDate());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getReminderTime() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getReminderTime());
        }
        if (entity.getRepeatRule() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getRepeatRule());
        }
        if (entity.getCustomSound() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCustomSound());
        }
        statement.bindLong(10, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Task task, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTask.insertAndReturnId(task);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Task task, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTask.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Task task, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTask.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Task>> getTasksForDate(final String date) {
    final String _sql = "SELECT * FROM tasks WHERE date = ? ORDER BY priority DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<Task>>() {
      @Override
      @NonNull
      public List<Task> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfRepeatRule = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatRule");
          final int _cursorIndexOfCustomSound = CursorUtil.getColumnIndexOrThrow(_cursor, "customSound");
          final List<Task> _result = new ArrayList<Task>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Task _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getString(_cursorIndexOfReminderTime);
            }
            final String _tmpRepeatRule;
            if (_cursor.isNull(_cursorIndexOfRepeatRule)) {
              _tmpRepeatRule = null;
            } else {
              _tmpRepeatRule = _cursor.getString(_cursorIndexOfRepeatRule);
            }
            final String _tmpCustomSound;
            if (_cursor.isNull(_cursorIndexOfCustomSound)) {
              _tmpCustomSound = null;
            } else {
              _tmpCustomSound = _cursor.getString(_cursorIndexOfCustomSound);
            }
            _item = new Task(_tmpId,_tmpTitle,_tmpCategory,_tmpPriority,_tmpDate,_tmpIsCompleted,_tmpReminderTime,_tmpRepeatRule,_tmpCustomSound);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTasksInRange(final String startDate, final String endDate,
      final Continuation<? super List<Task>> $completion) {
    final String _sql = "SELECT * FROM tasks WHERE date BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Task>>() {
      @Override
      @NonNull
      public List<Task> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfRepeatRule = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatRule");
          final int _cursorIndexOfCustomSound = CursorUtil.getColumnIndexOrThrow(_cursor, "customSound");
          final List<Task> _result = new ArrayList<Task>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Task _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getString(_cursorIndexOfReminderTime);
            }
            final String _tmpRepeatRule;
            if (_cursor.isNull(_cursorIndexOfRepeatRule)) {
              _tmpRepeatRule = null;
            } else {
              _tmpRepeatRule = _cursor.getString(_cursorIndexOfRepeatRule);
            }
            final String _tmpCustomSound;
            if (_cursor.isNull(_cursorIndexOfCustomSound)) {
              _tmpCustomSound = null;
            } else {
              _tmpCustomSound = _cursor.getString(_cursorIndexOfCustomSound);
            }
            _item = new Task(_tmpId,_tmpTitle,_tmpCategory,_tmpPriority,_tmpDate,_tmpIsCompleted,_tmpReminderTime,_tmpRepeatRule,_tmpCustomSound);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCompletedCount(final String date,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM tasks WHERE date = ? AND isCompleted = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalCount(final String date, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM tasks WHERE date = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
