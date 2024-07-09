package com.soyeon.nubim.domain.album;

import com.soyeon.nubim.common.BaseEntity;
import com.soyeon.nubim.domain.user.User;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Album extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String description;

    @Type(JsonType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String photoUrls;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String coordinate;

    private LocalDateTime coordinateTime;

}
