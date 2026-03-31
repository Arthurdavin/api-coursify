package com.coursify.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "course_tags")
@IdClass(CourseTag.CourseTagId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseTag {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseTagId implements Serializable {
        private Long course;
        private Long tag;
    }
}
