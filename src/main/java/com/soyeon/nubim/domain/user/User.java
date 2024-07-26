package com.soyeon.nubim.domain.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.soyeon.nubim.common.BaseEntity;
import com.soyeon.nubim.common.enums.Gender;
import com.soyeon.nubim.common.enums.Role;
import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.comment.Comment;
import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.userfollow.UserFollow;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"userId"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE user_id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false, unique = true)
	private String nickname;

	private String profileImageUrl;

	private String profileIntroduction;

	@Column(nullable = false, unique = true)
	private String email;

	private String phoneNumber;

	private LocalDateTime birthDate;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	@NotNull
	private Role role;

	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Album> albums = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Post> posts = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Comment> comments = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "followee", fetch = FetchType.LAZY)
	private List<UserFollow> followers = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
	private List<UserFollow> followees = new ArrayList<>();

	public User updateNameFromOAuthProfile(String name) {
		this.username = name;
		return this;
	}

	public String getRoleKey() {
		return this.role.getKey();
	}

	public void addFollower(UserFollow userFollow) {
		this.followers.add(userFollow);
	}

	public void addFollowee(UserFollow userFollow) {
		this.followees.add(userFollow);
	}

	public void deleteFollower(UserFollow userFollow) {
		this.followers.remove(userFollow);
	}

	public void deleteFollowee(UserFollow userFollow) {
		this.followees.remove(userFollow);
	}
}
