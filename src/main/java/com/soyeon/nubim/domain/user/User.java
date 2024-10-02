package com.soyeon.nubim.domain.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.soyeon.nubim.common.BaseEntity;
import com.soyeon.nubim.common.enums.Gender;
import com.soyeon.nubim.common.enums.Provider;
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

	@Enumerated(EnumType.STRING)
	@NotNull
	private Provider provider;

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

	/**
	 * 다른 엔티티 생성 시 매핑만을 위한 임시 User 엔티티 생성
	 * 실제 User의 값은 가지지 않으니 사용 시 주의할 것
	 */
	public User(Long userId) {
		this.userId = userId;
	}

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

	public static class NicknamePolicy {
		public static final String REGEXP = "^[a-zA-Z0-9][a-zA-Z0-9_.-]+$"; // 첫 글자는 알파벳 또는 숫자
		public static final int MIN_LENGTH = 4;
		public static final int MAX_LENGTH = 30;
		public static final String ERROR_MESSAGE = "닉네임은 알파벳 및 숫자, 언더바(_), 점(.), 하이픈(-)만 포함할 수 있습니다. 첫자는 알파벳 또는 숫자이어야 합니다.";

		private NicknamePolicy() {
		}
	}
}