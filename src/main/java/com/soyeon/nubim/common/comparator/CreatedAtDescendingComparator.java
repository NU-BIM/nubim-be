package com.soyeon.nubim.common.comparator;

import com.soyeon.nubim.common.BaseEntity;

import java.util.Comparator;

public class CreatedAtDescendingComparator<T extends BaseEntity> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        return o2.getCreatedAt().compareTo(o1.getCreatedAt()); // 내림차순 정렬
    }
}
