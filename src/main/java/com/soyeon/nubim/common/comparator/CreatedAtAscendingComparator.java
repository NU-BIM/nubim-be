package com.soyeon.nubim.common.comparator;

import com.soyeon.nubim.common.BaseEntity;

import java.util.Comparator;

public class CreatedAtAscendingComparator<T extends BaseEntity> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        return o1.getCreatedAt().compareTo(o2.getCreatedAt()); // 오름차순 정렬
    }
}
