package com.hz.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.hz.greendao.dao.PointGalleryEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "POINT_GALLERY_ENTITY".
*/
public class PointGalleryEntityDao extends AbstractDao<PointGalleryEntity, String> {

    public static final String TABLENAME = "POINT_GALLERY_ENTITY";

    /**
     * Properties of entity PointGalleryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property ImgId = new Property(0, String.class, "imgId", true, "IMG_ID");
        public final static Property ImgPointId = new Property(1, String.class, "imgPointId", false, "IMG_POINT_ID");
        public final static Property ImgFrom = new Property(2, String.class, "imgFrom", false, "IMG_FROM");
        public final static Property ImgAddress = new Property(3, String.class, "imgAddress", false, "IMG_ADDRESS");
        public final static Property ImgUploadProgress = new Property(4, int.class, "imgUploadProgress", false, "IMG_UPLOAD_PROGRESS");
        public final static Property ImgRemoved = new Property(5, int.class, "imgRemoved", false, "IMG_REMOVED");
        public final static Property ImgNeedToUpload = new Property(6, boolean.class, "imgNeedToUpload", false, "IMG_NEED_TO_UPLOAD");
    };


    public PointGalleryEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PointGalleryEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"POINT_GALLERY_ENTITY\" (" + //
                "\"IMG_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: imgId
                "\"IMG_POINT_ID\" TEXT NOT NULL ," + // 1: imgPointId
                "\"IMG_FROM\" TEXT," + // 2: imgFrom
                "\"IMG_ADDRESS\" TEXT," + // 3: imgAddress
                "\"IMG_UPLOAD_PROGRESS\" INTEGER NOT NULL ," + // 4: imgUploadProgress
                "\"IMG_REMOVED\" INTEGER NOT NULL ," + // 5: imgRemoved
                "\"IMG_NEED_TO_UPLOAD\" INTEGER NOT NULL );"); // 6: imgNeedToUpload
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_POINT_GALLERY_ENTITY_IMG_ID ON POINT_GALLERY_ENTITY" +
                " (\"IMG_ID\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_POINT_GALLERY_ENTITY_IMG_POINT_ID ON POINT_GALLERY_ENTITY" +
                " (\"IMG_POINT_ID\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_POINT_GALLERY_ENTITY_IMG_UPLOAD_PROGRESS ON POINT_GALLERY_ENTITY" +
                " (\"IMG_UPLOAD_PROGRESS\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_POINT_GALLERY_ENTITY_IMG_REMOVED ON POINT_GALLERY_ENTITY" +
                " (\"IMG_REMOVED\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_POINT_GALLERY_ENTITY_IMG_NEED_TO_UPLOAD ON POINT_GALLERY_ENTITY" +
                " (\"IMG_NEED_TO_UPLOAD\");");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"POINT_GALLERY_ENTITY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PointGalleryEntity entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getImgId());
        stmt.bindString(2, entity.getImgPointId());
 
        String imgFrom = entity.getImgFrom();
        if (imgFrom != null) {
            stmt.bindString(3, imgFrom);
        }
 
        String imgAddress = entity.getImgAddress();
        if (imgAddress != null) {
            stmt.bindString(4, imgAddress);
        }
        stmt.bindLong(5, entity.getImgUploadProgress());
        stmt.bindLong(6, entity.getImgRemoved());
        stmt.bindLong(7, entity.getImgNeedToUpload() ? 1L: 0L);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PointGalleryEntity readEntity(Cursor cursor, int offset) {
        PointGalleryEntity entity = new PointGalleryEntity( //
            cursor.getString(offset + 0), // imgId
            cursor.getString(offset + 1), // imgPointId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // imgFrom
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // imgAddress
            cursor.getInt(offset + 4), // imgUploadProgress
            cursor.getInt(offset + 5), // imgRemoved
            cursor.getShort(offset + 6) != 0 // imgNeedToUpload
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PointGalleryEntity entity, int offset) {
        entity.setImgId(cursor.getString(offset + 0));
        entity.setImgPointId(cursor.getString(offset + 1));
        entity.setImgFrom(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setImgAddress(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setImgUploadProgress(cursor.getInt(offset + 4));
        entity.setImgRemoved(cursor.getInt(offset + 5));
        entity.setImgNeedToUpload(cursor.getShort(offset + 6) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(PointGalleryEntity entity, long rowId) {
        return entity.getImgId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(PointGalleryEntity entity) {
        if(entity != null) {
            return entity.getImgId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
