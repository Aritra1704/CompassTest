package in.arp.compasstest.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "pic_table")
public class PicDO {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "mLat")
    private double mLat;

    @ColumnInfo(name = "mLng")
    private double mLng;

    @ColumnInfo(name = "mAngle")
    private double mAngle;

    @ColumnInfo(name = "mPic")
    private String mPic;

    public PicDO(double mLat, double mLng, double mAngle, @NonNull String mPic) {
        this.mLat = mLat;
        this.mLng = mLng;
        this.mAngle = mAngle;
        this.mPic = mPic;
    }

    public double getLat(){
        return this.mLat;
    }

    public double getLng(){
        return this.mLng;
    }

    public double getAngle(){
        return this.mAngle;
    }

    public String getPic(){
        return this.mPic;
    }
}
