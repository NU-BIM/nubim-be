package com.soyeon.nubim.domain.album;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;

import com.soyeon.nubim.common.BaseEntity;
import com.soyeon.nubim.domain.album.exception.AlbumAlreadyLinkedToPostException;
import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.user.User;
import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE album SET is_deleted = true WHERE album_id = ?")
@SQLRestriction("is_deleted = false")
public class Album extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long albumId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private String description;

	@Builder.Default
	private boolean postLinked = false;

	@Builder.Default
	@Type(JsonType.class)
	@Column(nullable = false, columnDefinition = "jsonb")
	private Map<Integer, String> photoUrls = new TreeMap<>();

	@Builder.Default
	@OneToMany(mappedBy = "album", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Location> locations = new ArrayList<>();

	public void bindLocations() {
		for (Location location : locations) {
			location.setAlbum(this);
		}
	}

	public void linkPost() {
		if (postLinked) {
			throw new AlbumAlreadyLinkedToPostException();
		}
		this.postLinked = true;
	}

	public void unlinkPost() {
		this.postLinked = false;
	}

}
