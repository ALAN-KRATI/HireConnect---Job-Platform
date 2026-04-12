package com.hireconnect.job.repository;

import com.hireconnect.job.document.JobDocument;
import com.hireconnect.job.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSearchRepository extends ElasticsearchRepository<JobDocument, Long> {

    Page<JobDocument> findByStatus(JobStatus status, Pageable pageable);

    @Query("{" +
           "  \"bool\": {" +
           "    \"must\": [" +
           "      { \"term\": { \"status\": \"OPEN\" } }," +
           "      { \"multi_match\": { \"query\": \"?0\", \"fields\": [\"title^3\", \"description\", \"skills\"] } }" +
           "    ]" +
           "  }" +
           "}")
    List<JobDocument> searchByKeyword(String keyword);

    @Query("{" +
           "  \"bool\": {" +
           "    \"must\": [" +
           "      { \"term\": { \"status\": \"OPEN\" } }," +
           "      { \"match\": { \"location\": \"?0\" } }" +
           "    ]" +
           "  }" +
           "}")
    List<JobDocument> findByLocation(String location);

    @Query("{" +
           "  \"bool\": {" +
           "    \"must\": [" +
           "      { \"term\": { \"status\": \"OPEN\" } }," +
           "      { \"range\": { \"minSalary\": { \"gte\": ?0 } } }," +
           "      { \"range\": { \"maxSalary\": { \"lte\": ?1 } } }" +
           "    ]" +
           "  }" +
           "}")
    List<JobDocument> findBySalaryRange(Double minSalary, Double maxSalary);

    @Query("{" +
           "  \"bool\": {" +
           "    \"must\": [" +
           "      { \"term\": { \"status\": \"OPEN\" } }," +
           "      { \"range\": { \"experienceRequired\": { \"gte\": ?0 } } }" +
           "    ]" +
           "  }" +
           "}")
    List<JobDocument> findByMinimumExperience(Integer experience);

    List<JobDocument> findByCategoryAndStatus(String category, JobStatus status);

    List<JobDocument> findByPostedBy(Long postedBy);
}
