package com.hz.greendao.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.hz.greendao.dao.WireType;
import com.hz.greendao.dao.TransformerType;
import com.hz.greendao.dao.GeologicalConditionType;
import com.hz.greendao.dao.TowerType;
import com.hz.greendao.dao.EquimentInstallType;
import com.hz.greendao.dao.ElectricPoleType;
import com.hz.greendao.dao.ConductorWireEntity;
import com.hz.greendao.dao.ProjectEntity;
import com.hz.greendao.dao.MapPoiEntity;
import com.hz.greendao.dao.MapLineItemEntity;
import com.hz.greendao.dao.MapLineEntity;
import com.hz.greendao.dao.PointGalleryEntity;

import com.hz.greendao.dao.WireTypeDao;
import com.hz.greendao.dao.TransformerTypeDao;
import com.hz.greendao.dao.GeologicalConditionTypeDao;
import com.hz.greendao.dao.TowerTypeDao;
import com.hz.greendao.dao.EquimentInstallTypeDao;
import com.hz.greendao.dao.ElectricPoleTypeDao;
import com.hz.greendao.dao.ConductorWireEntityDao;
import com.hz.greendao.dao.ProjectEntityDao;
import com.hz.greendao.dao.MapPoiEntityDao;
import com.hz.greendao.dao.MapLineItemEntityDao;
import com.hz.greendao.dao.MapLineEntityDao;
import com.hz.greendao.dao.PointGalleryEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig wireTypeDaoConfig;
    private final DaoConfig transformerTypeDaoConfig;
    private final DaoConfig geologicalConditionTypeDaoConfig;
    private final DaoConfig towerTypeDaoConfig;
    private final DaoConfig equimentInstallTypeDaoConfig;
    private final DaoConfig electricPoleTypeDaoConfig;
    private final DaoConfig conductorWireEntityDaoConfig;
    private final DaoConfig projectEntityDaoConfig;
    private final DaoConfig mapPoiEntityDaoConfig;
    private final DaoConfig mapLineItemEntityDaoConfig;
    private final DaoConfig mapLineEntityDaoConfig;
    private final DaoConfig pointGalleryEntityDaoConfig;

    private final WireTypeDao wireTypeDao;
    private final TransformerTypeDao transformerTypeDao;
    private final GeologicalConditionTypeDao geologicalConditionTypeDao;
    private final TowerTypeDao towerTypeDao;
    private final EquimentInstallTypeDao equimentInstallTypeDao;
    private final ElectricPoleTypeDao electricPoleTypeDao;
    private final ConductorWireEntityDao conductorWireEntityDao;
    private final ProjectEntityDao projectEntityDao;
    private final MapPoiEntityDao mapPoiEntityDao;
    private final MapLineItemEntityDao mapLineItemEntityDao;
    private final MapLineEntityDao mapLineEntityDao;
    private final PointGalleryEntityDao pointGalleryEntityDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        wireTypeDaoConfig = daoConfigMap.get(WireTypeDao.class).clone();
        wireTypeDaoConfig.initIdentityScope(type);

        transformerTypeDaoConfig = daoConfigMap.get(TransformerTypeDao.class).clone();
        transformerTypeDaoConfig.initIdentityScope(type);

        geologicalConditionTypeDaoConfig = daoConfigMap.get(GeologicalConditionTypeDao.class).clone();
        geologicalConditionTypeDaoConfig.initIdentityScope(type);

        towerTypeDaoConfig = daoConfigMap.get(TowerTypeDao.class).clone();
        towerTypeDaoConfig.initIdentityScope(type);

        equimentInstallTypeDaoConfig = daoConfigMap.get(EquimentInstallTypeDao.class).clone();
        equimentInstallTypeDaoConfig.initIdentityScope(type);

        electricPoleTypeDaoConfig = daoConfigMap.get(ElectricPoleTypeDao.class).clone();
        electricPoleTypeDaoConfig.initIdentityScope(type);

        conductorWireEntityDaoConfig = daoConfigMap.get(ConductorWireEntityDao.class).clone();
        conductorWireEntityDaoConfig.initIdentityScope(type);

        projectEntityDaoConfig = daoConfigMap.get(ProjectEntityDao.class).clone();
        projectEntityDaoConfig.initIdentityScope(type);

        mapPoiEntityDaoConfig = daoConfigMap.get(MapPoiEntityDao.class).clone();
        mapPoiEntityDaoConfig.initIdentityScope(type);

        mapLineItemEntityDaoConfig = daoConfigMap.get(MapLineItemEntityDao.class).clone();
        mapLineItemEntityDaoConfig.initIdentityScope(type);

        mapLineEntityDaoConfig = daoConfigMap.get(MapLineEntityDao.class).clone();
        mapLineEntityDaoConfig.initIdentityScope(type);

        pointGalleryEntityDaoConfig = daoConfigMap.get(PointGalleryEntityDao.class).clone();
        pointGalleryEntityDaoConfig.initIdentityScope(type);

        wireTypeDao = new WireTypeDao(wireTypeDaoConfig, this);
        transformerTypeDao = new TransformerTypeDao(transformerTypeDaoConfig, this);
        geologicalConditionTypeDao = new GeologicalConditionTypeDao(geologicalConditionTypeDaoConfig, this);
        towerTypeDao = new TowerTypeDao(towerTypeDaoConfig, this);
        equimentInstallTypeDao = new EquimentInstallTypeDao(equimentInstallTypeDaoConfig, this);
        electricPoleTypeDao = new ElectricPoleTypeDao(electricPoleTypeDaoConfig, this);
        conductorWireEntityDao = new ConductorWireEntityDao(conductorWireEntityDaoConfig, this);
        projectEntityDao = new ProjectEntityDao(projectEntityDaoConfig, this);
        mapPoiEntityDao = new MapPoiEntityDao(mapPoiEntityDaoConfig, this);
        mapLineItemEntityDao = new MapLineItemEntityDao(mapLineItemEntityDaoConfig, this);
        mapLineEntityDao = new MapLineEntityDao(mapLineEntityDaoConfig, this);
        pointGalleryEntityDao = new PointGalleryEntityDao(pointGalleryEntityDaoConfig, this);

        registerDao(WireType.class, wireTypeDao);
        registerDao(TransformerType.class, transformerTypeDao);
        registerDao(GeologicalConditionType.class, geologicalConditionTypeDao);
        registerDao(TowerType.class, towerTypeDao);
        registerDao(EquimentInstallType.class, equimentInstallTypeDao);
        registerDao(ElectricPoleType.class, electricPoleTypeDao);
        registerDao(ConductorWireEntity.class, conductorWireEntityDao);
        registerDao(ProjectEntity.class, projectEntityDao);
        registerDao(MapPoiEntity.class, mapPoiEntityDao);
        registerDao(MapLineItemEntity.class, mapLineItemEntityDao);
        registerDao(MapLineEntity.class, mapLineEntityDao);
        registerDao(PointGalleryEntity.class, pointGalleryEntityDao);
    }
    
    public void clear() {
        wireTypeDaoConfig.getIdentityScope().clear();
        transformerTypeDaoConfig.getIdentityScope().clear();
        geologicalConditionTypeDaoConfig.getIdentityScope().clear();
        towerTypeDaoConfig.getIdentityScope().clear();
        equimentInstallTypeDaoConfig.getIdentityScope().clear();
        electricPoleTypeDaoConfig.getIdentityScope().clear();
        conductorWireEntityDaoConfig.getIdentityScope().clear();
        projectEntityDaoConfig.getIdentityScope().clear();
        mapPoiEntityDaoConfig.getIdentityScope().clear();
        mapLineItemEntityDaoConfig.getIdentityScope().clear();
        mapLineEntityDaoConfig.getIdentityScope().clear();
        pointGalleryEntityDaoConfig.getIdentityScope().clear();
    }

    public WireTypeDao getWireTypeDao() {
        return wireTypeDao;
    }

    public TransformerTypeDao getTransformerTypeDao() {
        return transformerTypeDao;
    }

    public GeologicalConditionTypeDao getGeologicalConditionTypeDao() {
        return geologicalConditionTypeDao;
    }

    public TowerTypeDao getTowerTypeDao() {
        return towerTypeDao;
    }

    public EquimentInstallTypeDao getEquimentInstallTypeDao() {
        return equimentInstallTypeDao;
    }

    public ElectricPoleTypeDao getElectricPoleTypeDao() {
        return electricPoleTypeDao;
    }

    public ConductorWireEntityDao getConductorWireEntityDao() {
        return conductorWireEntityDao;
    }

    public ProjectEntityDao getProjectEntityDao() {
        return projectEntityDao;
    }

    public MapPoiEntityDao getMapPoiEntityDao() {
        return mapPoiEntityDao;
    }

    public MapLineItemEntityDao getMapLineItemEntityDao() {
        return mapLineItemEntityDao;
    }

    public MapLineEntityDao getMapLineEntityDao() {
        return mapLineEntityDao;
    }

    public PointGalleryEntityDao getPointGalleryEntityDao() {
        return pointGalleryEntityDao;
    }

}