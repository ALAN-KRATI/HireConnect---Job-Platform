package com.hireconnect.job.document;

import com.hireconnect.job.enums.JobStatus;
import com.hireconnect.job.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "jobs")
public class JobDocument {

    @Id
    private Long jobId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private JobType type;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String location;

    @Field(type = FieldType.Double)
    private Double minSalary;

    @Field(type = FieldType.Double)
    private Double maxSalary;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private List<String> skills;

    @Field(type = FieldType.Integer)
    private Integer experienceRequired;

    @Field(type = FieldType.Long)
    private Long postedBy;

    @Field(type = FieldType.Keyword)
    private JobStatus status;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime postedAt;

    @Field(type = FieldType.Long)
    private Long viewCount;
}
