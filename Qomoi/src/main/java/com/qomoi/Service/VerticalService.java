package com.qomoi.Service;



import com.qomoi.entity.VerticalEntity;

import java.util.List;

public interface VerticalService {

    List<VerticalEntity> getVerticals();

    void saveTopic(VerticalEntity verticalEntity);

    List<VerticalEntity> searchVerticals(String query);
}
