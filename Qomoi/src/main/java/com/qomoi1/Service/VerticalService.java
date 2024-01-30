package com.qomoi1.Service;



import com.qomoi1.entity.VerticalEntity;

import java.util.List;

public interface VerticalService {

    List<VerticalEntity> getVerticals();

    void saveTopic(VerticalEntity verticalEntity);

    List<VerticalEntity> searchVerticals(String query);
}
