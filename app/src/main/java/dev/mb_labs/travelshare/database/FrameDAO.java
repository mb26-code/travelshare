package dev.mb_labs.travelshare.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import dev.mb_labs.travelshare.model.Frame;

@Dao
public interface FrameDAO {
    @Query("SELECT * FROM frames ORDER BY createdAt DESC")
    List<Frame> getAllFrames();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Frame> frames);

    @Query("DELETE FROM frames")
    void clearAll();
}