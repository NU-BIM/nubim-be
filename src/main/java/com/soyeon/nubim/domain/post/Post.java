package com.soyeon.nubim.domain.post;

import com.soyeon.nubim.common.BaseEntity;
import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.comment.Comment;
import com.soyeon.nubim.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE post SET is_deleted = true WHERE post_id = ?")
@SQLRestriction("is_deleted = false")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Column(nullable = false, length = 100)
    private String postTitle;

    @Column(length = 2200)
    private String postContent;

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
}

class CreatedAtComparator implements Comparator<Post> {
    @Override
    public int compare(Post o1, Post o2) {
        return o1.getCreatedAt().compareTo(o2.getCreatedAt());
    }
}