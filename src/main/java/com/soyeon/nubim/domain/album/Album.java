package com.soyeon.nubim.domain.album;

import java.time.LocalDateTime;

import com.soyeon.nubim.common.BaseEntity;
import com.soyeon.nubim.domain.user.User;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.Type;

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
