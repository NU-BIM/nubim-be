package com.soyeon.nubim.domain.post;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.soyeon.nubim.common.BaseEntity;
import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.comment.Comment;
import com.soyeon.nubim.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
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
@SQLDelete(sql = "UPDATE post SET is_deleted = true, album_id = null WHERE post_id = ?")
@SQLRestriction("is_deleted = false")
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id")
	private Album album;

	@Column(nullable = false, length = 100)
	private String postTitle;

	@Column(length = 2200)
	private String postContent;

	@Builder.Default
	@OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
	@OrderBy("createdAt DESC")
	private List<Comment> comments = new ArrayList<>();

	/**
	 * 다른 엔티티 생성 시 매핑만을 위해 임시 Post 엔티티 생성
	 * 실제 Post의 값은 가지지 않으니 사용 시 주의할 것
	 */
	public Post(Long postId) {
		this.postId = postId;
	}

	public void linkAlbum(Album album) {
		this.album = album;
		album.linkPost();
	}

	public void unlinkAlbum() {
		album.unlinkPost();
		this.album = null;
	}
}