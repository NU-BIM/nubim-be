package com.soyeon.nubim.domain.userfollow;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.soyeon.nubim.common.BaseEntity;
import com.soyeon.nubim.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@SQLDelete(sql = "UPDATE user_follows SET is_deleted = true WHERE user_follow_id = ?")
@SQLRestriction("is_deleted = false")
public class UserFollow extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userFollowId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "follower_id", nullable = false)
	private User follower;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "followee_id", nullable = false)
	private User followee;

	public void addFollowerAndFolloweeByUserFollow() {
		User follower = this.getFollower();
		User followee = this.getFollowee();

		follower.addFollowee(this);
		followee.addFollower(this);
	}
	public void deleteFollowerAndFolloweeByUserFollow(){
		User follower = this.getFollower();
		User followee = this.getFollowee();

		follower.deleteFollowee(this);
		followee.deleteFollower(this);
	}
}
