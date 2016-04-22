package com.hz.util;

import com.hz.greendao.dao.MapLineItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 2016/4/22.
 */
public class MyList extends ArrayList<MapLineItemEntity>{

    private String id;
    private String lineId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
}
