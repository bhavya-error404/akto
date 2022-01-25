package com.akto.dao;

import com.akto.dto.ApiCollection;
import com.mongodb.client.model.Filters;

public class ApiCollectionsDao extends AccountsContextDao<ApiCollection> {

    public static final ApiCollectionsDao instance = new ApiCollectionsDao();

    private ApiCollectionsDao() {}

    @Override
    public String getCollName() {
        return "api_collections";
    }

    @Override
    public Class<ApiCollection> getClassT() {
        return ApiCollection.class;
    }

    public ApiCollection findByName(String name) {
        return ApiCollectionsDao.instance.findOne(Filters.eq("name", name));
    }
}
